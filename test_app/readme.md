### 异常
- cannot access androidx.appcompat.app.appcompatactivity

  如果报错其它类，添加相应依赖库即可
- 添加依赖

  implementation 'androidx.appcompat:appcompat:1.1.0'

- 启动Activity不能弹出界面
  需要在应用管理里面，打开 "允许从后台打开窗口"，因为测试里面启动activity是在后台服务里面。