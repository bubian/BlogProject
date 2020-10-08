### module介绍

    module主要用于存放项目公共资料，比如activity统一切换动画，导航图标，尺寸文件，主题等。如果其它项目整体风格一样，可以直接引用，
    如果不一致，可以再单独新建module，切勿都放在改module下。

### 问题记录

##### activity切换动画无效问题

    1、android系统版本2.0以下，这个没办法，想其他办法解决切换动画吧。
    2、在ActivityGroup等的嵌入式Activity中，这个比较容易解决，用如下方法就可以了：
    this.getParent().overridePendingTransition 就可以解决。
    3、在一个Activity的内部类中，或者匿名类中，这时候只好用handler来解决了。
    4、进入和退出设置的动画时间需要一样，不然存在异常。
    5、复写切换方法overridePendingTransition时，只能在startActivity和finish方法之后调用。
