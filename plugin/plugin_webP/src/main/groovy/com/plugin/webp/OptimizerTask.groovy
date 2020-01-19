package com.plugin.webp

import groovy.xml.Namespace
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class OptimizerTask extends DefaultTask {
///root/.ssh/id_rsa)
    static def PNG_TOOL = "pngcrush"
    static def JPG_TOOL = "guetzli"
    static def WEBP_TOOL = "cwebp"

    @Input
    File manifestFile

    //需要打包的资源目录
    @Input
    File res
    // minSdkVersion
    @Input
    int apiLevel

    def webpTool
    def jpgTool
    def pngTool
    String launcher
    String round_launcher

    OptimizerTask() {
        group "optimize"
        webpTool = OptimizerUtils.getTool(project, WEBP_TOOL)
        jpgTool = OptimizerUtils.getTool(project, JPG_TOOL)
        pngTool = OptimizerUtils.getTool(project, PNG_TOOL)
    }

    @TaskAction
    def run() {
        project.logger.error "==============================optimizer================================="
        if (!res.exists() || !manifestFile.exists()){
            project.logger.error "--->: res dir null or manifestFile is null"
            return
        }

        if (new File(webpTool).length() < 1 || new File(jpgTool) < 1 || new File(pngTool) < 1){
            project.logger.error "--->: toots is error"
            return
        }
        /**
         * 1、解析AndroidManifest.xml获得 app的icon(以及roundIcon)
         */
        project.logger.error "--->: start parse AndroidManifest.xml."
        // dom sax 解析 xml 这里会用gdk 内的函数解析
        def ns = new Namespace("http://schemas.android.com/apk/res/android", "android")
        Node xml = new XmlParser().parse(manifestFile)
        Node application = xml.application[0]
        //找到apk启动图标，官方建议不要转换
        launcher = application.attributes()[ns.icon]
        launcher = launcher.substring(launcher.lastIndexOf("/") + 1, launcher.length())
        round_launcher = application.attributes()[ns.roundIcon]
        if (null != round_launcher)
            round_launcher = round_launcher.substring(round_launcher.lastIndexOf("/") + 1, round_launcher.length())
        else round_launcher = ""

        /**
         * 2、遍历需要打包的res资源目录 获得所有图片资源
         */
        def pngList = []
        def jpgList = []

        res.eachDir { dir ->
            if (OptimizerUtils.isImgFolder(dir)) {
                dir.eachFile { f ->
                    //launcher就不管
                    if (OptimizerUtils.isPreOptimizeJpg(f) && isNonLauncher(f))
                        jpgList << f
                    if (OptimizerUtils.isPreOptimizePng(f) && isNonLauncher(f))
                        pngList << f
                }
            }
        }
        /**
         * 3、开始压缩/转换
         */
        project.logger.error "--->: compress/convert."
        //无效的转换图片(转换后比原图还大)
        def jpgNotConvertList = []
        def pngNotConvertList = []
        if (apiLevel >= 14 && apiLevel < 18) {
            //记录不能转换为webp的png图片 用于压缩
            def compress = []
            pngList.each {
                //如果有alpha通道 则压缩
                if (OptimizerUtils.isTransparent(it)) {
                    compress << it
                    project.logger.error "   ${it.name} has alpha channel,don't convert webp"
                } else {
                    //转换webp
                    convertWebp(webpTool, it,pngNotConvertList)
                }
            }
            //压缩 png
            compressImg(pngTool, true, compress)
            //jpeg本身就不带alpha 都可以转换为webp
            jpgList.each {
                convertWebp(webpTool, it,jpgNotConvertList)
            }

        } else if (apiLevel >= 18) {
            //能够使用有透明的webp
            pngList.each {
                convertWebp(webpTool, it,pngNotConvertList)
            }
            jpgList.each {
                convertWebp(webpTool, it,jpgNotConvertList)
            }
        } else {
            //不能使用webp 进行压缩
            compressImg(pngTool, true, pngList)
            compressImg(jpgTool, false, jpgList)
        }

        //转换无效的再压缩
        compressImg(pngTool, true, pngNotConvertList)
        compressImg(pngTool, false, jpgNotConvertList)
    }

    def isNonLauncher(File f) {
        return f.name != "${launcher}.png" && f.name != "${launcher}.jpg" && f.name != "${round_launcher}.png" && f.name != "${round_launcher}.jpg"
    }

    def convertWebp(String tool, File file,def noValidConvert) {

        def name = file.name

        name = name.substring(0, name.lastIndexOf("."))
        def output = new File(file.parent, "${name}.webp")

        //google 建议75的质量 参考https://developers.google.cn/speed/webp/docs/using
        def result = "$tool -q 75 ${file.absolutePath} -o ${output.absolutePath}".execute()
        result.waitFor()

        if (result.exitValue() == 0) {
            def rawLen = file.length()
            def outLen = output.length()

            if (output > 0 && rawLen > outLen) {
                file.delete()
            } else {
                project.logger.error "convert error name = ${file.name}"
                noValidConvert << file
                output.delete()
            }
        } else {
            noValidConvert << file
            project.logger.error "   convert ${file.absolutePath} to webp error"
        }
    }

    //pngcrush  -brute -rem alla -reduce -q in.png out.png
    //guetzli --quality quality  in.jpg out.jpg
    //参考：https://pmt.sourceforge.io/pngcrush/
    //参考：https://github.com/google/guetzli
    def compressImg(String tool, boolean isPng, def files) {
        files.each {
            File file ->
                def output = new File(file.parent, "temp-preOptimizer-${file.name}")
                def result
                if (isPng)
                    result = "$tool -brute -rem alla -reduce -q ${file.absolutePath}  ${output.absolutePath}"
                            .execute()
                else
                    result = "$tool --quality 84 ${file.absolutePath}  ${output.absolutePath}"
                            .execute()
                result.waitForProcessOutput()
                //压缩成功
                if (result.exitValue() == 0) {
                    def rawLen = file.length()
                    def outLen = output.length()
                    // 压缩后文件确实减小了
                    if (outLen > 0 && outLen < rawLen) {
                        //删除原图片
                        file.delete()
                        //将压缩后的图片重命名为原图片
                        output.renameTo(file)
                    } else {
                        project.logger.error " compress error name ${file.name}"
                        output.delete()
                    }
                } else {
                    project.logger.error "  compress execute ${file.absolutePath} error"
                }
        }
    }
}