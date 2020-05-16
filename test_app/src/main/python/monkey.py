# coding: utf-8

# 导入 monkeyrunner依赖
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# 连接当前设备, 返回一个MonkeyDevice对象
device = MonkeyRunner.waitForConnection()

# 安装apk，返回一个boolean值表示是否安装成功
device.installPackage('/Users/pengdaosong/pds/BlogProject/test_app/monkey_runner/app-debug.apk')

# 安装应用包名
package = 'com.pds.blog'

# 需要启动的Activity全路径名
activity = 'com.pds.blog.GlideTestActivity'

# 需要的启动的组件
runComponent = package + '/' + activity

# 启动组件
device.startActivity(component=runComponent)

# 按手机菜单按钮
device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)

# 截图
result = device.takeSnapshot()

# 截图保存路径
result.writeToFile('/Users/pengdaosong/pds/BlogProject/test_app/monkey_runner/Snapshot/shot1.png','png')