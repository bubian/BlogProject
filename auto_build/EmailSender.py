#coding:utf8

import email
import smtplib
import os
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.utils import formataddr

class EmailSender:

    def __init__(self):
        self.user = '自己的邮箱帐号'
        self.passwd = '邮箱密码(像163邮箱，使用客户端授权密码)'
        self.to_list = ['收件人邮箱1','收件人邮箱2']
        # 抄送列表
        self.cc_list = []
        self.tag = "安卓应用构建邮件(本邮件是程序自动下发的，请勿回复！)"
        self.path = None



    def send(self, content):
        try:
            server = smtplib.SMTP_SSL("smtp.163.com",port=465)
            server.login(self.user,self.passwd)
            server.sendmail("<%s>"%self.user, self.to_list, self.build_email(content))
            server.close()
            print("============send email successful!!!===========")
        except Exception as e:
            print("============send email failed %s!!!============"%e)


    def build_email(self, content):
        '''
        构造邮件内容
        '''
        attach = MIMEMultipart()

        #添加邮件内容
        #txt = MIMEText(self.content, 'plain', 'utf-8')
        txt = MIMEText(content, 'html', 'utf-8')
        attach.attach(txt)

        if self.tag is not None:
            # 主题,最上面的一行
            attach["Subject"] = self.tag
        if self.user is not None:
            # 显示在发件人
            attach['From'] = formataddr(["安卓组", self.user])
        if self.to_list:
            # 收件人列表
            attach["To"] = ";".join(self.to_list)
        if self.cc_list:
            # 抄送列表
            attach["Cc"] = ";".join(self.cc_list)
        if self.doc:
            # 估计任何文件都可以用base64，比如rar等
            name = os.path.basename(self.doc)
            f = open(self.doc,'rb')
            doc = MIMEText(f.read(), "base64", "gb2312")
            doc["Content-Type"] = 'application/octet-stream'
            doc["Content-Disposition"] = 'attachment; filename="'+name+'"'
            attach.attach(doc)
            f.close()
        return attach.as_string()