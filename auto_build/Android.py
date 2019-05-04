#coding:utf8

import os
import sys
import getopt
import platform
import subprocess
from PgyerUploader import PgyerUploader
from Constant import *

# 获取帮助信息
def getUsageStr():
    tipStr = (' \n============Android.py================\n'
              + ' -m, --msg <msg> 说明信息 \n'
              + ' -p, --platform <platform>  平台信息,默认test\n')
    return(tipStr)

# 执行命令
def exeShellCmd(cmd):
    popen = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)
    cmdRetBytes = popen.stdout.read()
    cmdRetStr = str(cmdRetBytes, encoding='utf-8')
    print(cmdRetStr)
    return cmdRetStr

# 更新工程代码
def updateGitRepo():
    print('update git repo start')
    ret = exeShellCmd('git pull')
    hasError = 'error' in ret
    if(hasError):
        raise ValueError('git pull failed')
    # ret = exeShellCmd('cd android && git pull')
    print('update git repo end')
    return
# 开始构建应用
def startBuild(platformService):
    # 获取平台信息
    sysstr = platform.system()
    cmdPrefix = './gradlew'
    print (sysstr)
    if(sysstr =="Windows"):
        cmdPrefix = 'gradlew'

    suffix = '-PplatformService=%s'%ANDROID_TEST_INT
    assemble = 'assembleDebug'

    if(platformService == ANDROID_ON_LINE):
        suffix = '-PplatformService=%s'%ANDROID_ON_LINE_INT
        assemble = 'assembleRelease'

    cmdStr = '%s clean %s %s' %(cmdPrefix, assemble, suffix)
    print (cmdStr)
    os.system(cmdStr)
    return

if __name__ == '__main__':
    # 获取命令行参数，不明白的可以参考我的文章：Python-解析命令行参数的两中方式。
    argv = sys.argv[1:]
    print (argv)
    # 打包附加信息
    msg = ''
    # 平台信息，测试平台，灰度平台，线上平台
    platformService = ANDROID_TEST
    try:
        # 这里的 h 就表示该选项无参数，冒号(:)表示该选项必须有附加的参数，不带冒号表示该选项不附加参数。
        opts, args = getopt.getopt(argv, "hm:p:",["msg=", "platformService="])
    except getopt.GetoptError:
        print (getUsageStr())
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print (getUsageStr())
            sys.exit()
        elif opt in ("-m", "--msg"):
            msg = arg
            print('-m arg =%s'%arg)
        elif opt in ("-p", "--platformService"):
            buildEnv = arg
            print('-e arg =%s'%arg)

    print('msg = %s, platformService = %s'%(msg, platform))

    updateGitRepo()
    startBuild(platformService)
    # 需要上传蒲公英的apk地址
    uploadApkPatch = 'android/app/build/outputs/apk/online/release/app-online-release.apk'
    # 上传蒲公英
    uploader = PgyerUploader()
    uploader.uploadPgyer(uploadApkPatch, buildEnv, msg)