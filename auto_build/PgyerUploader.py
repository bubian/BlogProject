#coding:utf8

import os
import sys
import requests
import subprocess
from EmailSender import EmailSender
from Constant import *

class PgyerUploader:

    def uploadPgyer(self, path, platform, msg):

        # ----------------上传蒲公英---------------
        print('-------------start upload pgyer------------')
        # 蒲公英上传地址
        url = 'https://www.pgyer.com/apiv2/app/upload'
        # 打印参数
        print ('path = %s,platformService = %s,msg = %s'%(path,platform,msg))
        # 为真时的结果 if 判断条件 else 为假时的结果（注意，没有冒号）类似 ?:
        platform_des = '测试环境' if(platform == ANDROID_TEST)  else '正式环境'
        # 描述信息
        des = platform_des +','+ msg
        # 蒲公英上传参数
        pgyer_params = {
            "uKey": (None, "自己的蒲公英key"),
            "_api_key": (None, "自己的蒲公英api key"),
            "file":('app-online-release.apk',open(path,'rb'),'application/x-zip-compressed'),
            "buildUpdateDescription": (None, des)
        }
        # 发送请求，上传蒲公英
        response = requests.post(url, files=pgyer_params)
        # 获取请求返回参数
        json_data = response.json()
        print(json_data)

        # ----------------开始组装发送邮件所需的数据---------------
        data_obj = json_data.get('data')
        url_key =  json_data.get('data').get('buildShortcutUrl')
        shortcut_url = 'https://www.pgyer.com/'+url_key
        app_code_url = json_data.get('data').get('buildQRCodeURL')
        build_version = data_obj.get('buildVersion')
        build_build_version = data_obj.get('buildBuildVersion')
        print('shortcutUrl = %s,appQRCodeURL = %s'%(shortcut_url,app_code_url))

        print('=================upload pgyer success!!! =================')

        # 最近20条提交日志
        log_20_cmd = 'git log -10 --pretty=format:%h--%s--%an--%cr --no-merges'
        popen = subprocess.Popen(log_20_cmd, stdout=subprocess.PIPE, shell=True)
        git_str = popen.stdout.read()
        git_commit_msg = str(git_str, encoding='utf-8')
        # 处理便签
        format_git_commit_msg = git_commit_msg.replace('\n','<br/>')

        print('=================start send emails !!!================')

        email_content = """
            <p>  hi all:</p>
            <p>安卓最新测试包。</p>
            <p> 蒲公英版本号：%s</p>
            <p> 版本：%s, platform_des : %s </p>
            <p><a href=%s>详情页</a></p>
            <p> 二维码：</p>
            <p><img src=%s></p>
            <p>git最近10条提交日志，<b>格式：简短hash--commitMsg--anthor--time</b></p>
            <p>%s</p>
            """%(build_build_version, build_version, platform_des, shortcut_url, app_code_url,
                 format_git_commit_msg)

        sender = EmailSender()
        sender.send(email_content)
        print('=================complete package work!!!================')