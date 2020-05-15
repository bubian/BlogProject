### 异常
- cannot access androidx.appcompat.app.appcompatactivity

  如果报错其它类，添加相应依赖库即可
- 添加依赖

  implementation 'androidx.appcompat:appcompat:1.1.0'

- 启动Activity不能弹出界面
  需要在应用管理里面，打开 "允许从后台打开窗口"，因为测试里面启动activity是在后台服务里面。
  
- android.content.res.Resources$NotFoundException: Drawable com.pds.blog:dimen/compat_button_inset_vertical_material with resource ID #0x7f060055

  将AppCompatActivity换成Activity
  
  ### 测试学习参考资源
  [Android单元测试](https://blog.csdn.net/qq_17766199/category_9270906.html)
  
### monkeyrunner使用
参考：[monkeyrunner](https://developer.android.google.cn/studio/test/monkeyrunner)

- 使用

  monkeyrunner可执行文件存在于sdk/tools/bin目录下，编写好的python脚本用monkeyrunner命令执行，例如： monkeyrunner monkey.py。
  单独执行python文件是不行的，没法导入python用到的库。
  
  