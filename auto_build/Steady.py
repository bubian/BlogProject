#coding:utf8

import os
import re
import time
import sys, getopt
import platform
import subprocess
import shutil
from pgyModule import PgyUploader
from emailModule import EmailSender

class SteadyAndroid:

    def __init__(self):
        self.secret_id = '乐固id'
        self.secret_key = '乐固key'
        self.steady_file_directory = 'android/app/build/Steady/'


    def Steady(self):
        steady_file_path = ''
        for fpath, dirname, fnames in os.walk(self.steady_file_directory):
            print(dirname)
            break
        steady_file_path = dirname[0]


        apkResignerForWallePath = 'android/ProtectedApkResignerForWalle'
        apk_path = self.steady_file_directory + steady_file_path + '/online/app-online-release.apk'
        download_path = self.steady_file_directory + '/download'

        cmdLeguStr = (
                ' rm -rf ' + download_path + '/*.apk'
                + '\n rm -rf ' + download_path
                + '\n mkdir ' + download_path
                + '\n java -Dfile.encoding=utf-8 -jar auto_build/lib/ms-shield.jar '
                + '-sid %s -skey %s -uploadPath %s -downloadPath %s' %(self.secret_id, self.secret_key, apk_path, download_path)
        )

        print('===========开始上传，并加固，请耐心等待～================')

        self.exeShellCmd(cmdLeguStr)

        legu_app_path = ''
        app_version_name = ''
        current_time = time.strftime("%Y-%m-%d_%H%M%S", time.localtime())

        for fpath, dirname, fnames in os.walk(download_path):
            print(fnames)
            print(fpath)
            fname = fnames[0]
            legu_app_path = fpath
            legu_app_path = legu_app_path + '/' + fname
            app_version_name = self.getApkVersionName(legu_app_path)

            new_name = 'app-release-v%s-%s-legu.apk' %(app_version_name, current_time)
            old_path = os.path.join(fpath, fname)
            new_path = os.path.join(fpath, new_name)
            os.rename(old_path, new_path)
            legu_app_path = new_path

            print('leguAppPath = ' + legu_app_path)
            break

        self.buildChannels(apk_path,app_version_name,current_time,'_360appmarket')


    def exeShellCmd(cmd):
        popen = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)
        cmdRetBytes = popen.stdout.read()
        cmdRetStr = str(cmdRetBytes, encoding='utf-8')
        print(cmdRetStr)
        return cmdRetStr


    def getApkVersionName(apkpath):
        #检查版本号等信息
        output = os.popen("./auto_build/lib/aapt d badging %s" % apkpath).read()
        match = re.compile("package: name='(\S+)' versionCode='(\d+)' versionName='(\S+)' platformBuildVersionName='\S+'").match(output)
        if not match:
            raise Exception("can't get packageinfo")
        versionName = match.group(3)
        return versionName


    def createChannelsApk(baseApkPath, app_version_name, current_time, channel_names):#使用walle生成渠道包（360应用市场需要强制用360加固）
        print('！！！！！！！！！！执行walle生成渠道包：' + channel_names)
        apk_resigner_for_walle_path = 'auto_build/'
        lib_path = apk_resigner_for_walle_path + 'lib/'
        walle_channel_writter_path = lib_path + 'walle-cli-all.jar'
        channels_output_file_path = walle_channel_writter_path + 'apk'

        new_name = 'app-release-v%s-%s.apk' %(app_version_name, current_time)
        new_base_apk_path = apk_resigner_for_walle_path + 'apk/' + new_name
        shutil.copyfile(baseApkPath, new_base_apk_path)
        write_channel_shell = "java -jar " + walle_channel_writter_path + " batch -c " + channel_names + " " + app_version_name + " " + channels_output_file_path
        os.system(write_channel_shell)
        print('！！！！！！！！！！生成渠道包完成')
        os.remove(new_base_apk_path)

    def buildChannels(self,apk_resigner_for_walle_path,app_version_name,current_time):

        # 生成渠道包
        cmd_walle_str = 'cd %s && python ApkResigner.py' %(apk_resigner_for_walle_path)
        print(cmd_walle_str)
        os.system(cmd_walle_str)
        # 将medlinker渠道apk上传蒲公英
        an_aligned_signed_apk = ''
        for fpath, dirname, fnames in os.walk(apk_resigner_for_walle_path + '/channels'):
            for fname in fnames:
                if('android' in fname):
                    an_aligned_signed_apk = fname
                    break

        apk_path = apk_resigner_for_walle_path + '/channels/' + an_aligned_signed_apk
        print(apk_path)
        self.createChannelsApk(apk_resigner_for_walle_path, app_version_name, current_time, '_360appmarket')
