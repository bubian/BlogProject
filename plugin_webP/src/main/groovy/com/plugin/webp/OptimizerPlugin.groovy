package com.plugin.webp

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class OptimizerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        reflectAapt2Flag()

        project.afterEvaluate {
            project.android.applicationVariants.all {
                //debug release
                BaseVariant variant ->
                    def task = project.tasks.create("optimize${variant.name.capitalize()}", OptimizerTask) {
                        //获得处理完成之后 确定的manifest文件
                        def processManifest = variant.outputs.first().processManifest
                        def processResources = variant.outputs.first().processResources
                        //android的gradle插件 2.3.3能直接拿到 3.0.0弃用
                        if (processManifest.properties['manifestOutputFile'] != null) {
                            manifestFile = processManifest.manifestOutputFile
                        } else if (processResources.properties['manifestFile'] != null) {
                            manifestFile = processResources.manifestFile
                        }
                        res = variant.mergeResources.outputDir
                        apiLevel = variant.mergeResources.minSdk
                    }
//
//                    System.out.printf("------->:processResources = ${variant.outputs.first().processResources}\n")
//                    System.out.printf("------->:processManifest = ${variant.outputs.first().processManifest}\n")
                    //将android插件的处理资源任务依赖于自定义任务
                    variant.outputs.first().processResources.dependsOn task
                    task.dependsOn variant.outputs.first().processManifest
            }
        }
    }

    void reflectAapt2Flag() {
        try {
            def booleanOptClazz = Class.forName('com.android.build.gradle.options.BooleanOption')
            def enableAAPT2Field = booleanOptClazz.getDeclaredField('ENABLE_AAPT2')
            enableAAPT2Field.setAccessible(true)
            def enableAAPT2EnumObj = enableAAPT2Field.get(null)
            def defValField = enableAAPT2EnumObj.getClass().getDeclaredField('defaultValue')
            defValField.setAccessible(true)
            defValField.set(enableAAPT2EnumObj, false)
        } catch (Throwable thr) {
            // To some extends, class not found means we are in lower version of android gradle
            // plugin, so just ignore that exception.
            if (!(thr instanceof ClassNotFoundException)) {
                project.logger.error("reflectAapt2Flag error: ${thr.getMessage()}.")
            }
        }
    }
}