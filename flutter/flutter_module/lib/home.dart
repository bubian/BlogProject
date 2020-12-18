 import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/item/item_gv_tb.dart';
import 'package:flutter_module/module/widget_demo_home.dart';
///  生命周期流程图
///
/// 整个过程分为四个阶段：
///
/// 初始化阶段，包括两个生命周期函数 createState 和 initState；
///
/// 组件创建阶段，也可以称组件出生阶段，包括 didChangeDependencies 和 build；
///
/// 触发组件多次 build ，这个阶段有可能是因为 didChangeDependencies、setState 或者 didUpdateWidget 而引发的组件重新 build ，在组件运行过程中会多次被触发，这也是优化过程中需要着重需要注意的点；
///
/// 最后是组件销毁阶段，deactivate 和 dispose。

class BlogHome extends StatefulWidget {
  BlogHome({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _BlogHomeState createState() => _BlogHomeState();

}

class _BlogHomeState extends State<BlogHome> {

  String name = 'test';

  final List<ItemModel> _icons = [
    ItemModel('assets/home/ic_widgets.png', '组件view', WidgetDemoHome.routeName)
  ]; //保存Icon数据
  @override
  Widget build(BuildContext context) {
    print('build');
    return GridView.builder(
        padding: EdgeInsets.all(16),
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 4, //每行三列
            childAspectRatio: 1.0 //显示区域宽高相等
            ),
        itemCount: _icons.length,
        itemBuilder: (context, index) {
          return ItemGvTb(data: _icons[index]);
        });
  }


  @override
  void initState() {
    print('init state');
    super.initState();
  }
  @override
  void didChangeDependencies() {
    print('did change dependencies');
    super.didChangeDependencies();
  }
  @override
  void didUpdateWidget(covariant BlogHome oldWidget) {
    print('did update widget');
    super.didUpdateWidget(oldWidget);
  }

  @override
  void deactivate() {
    print('deactivate');
    super.deactivate();
  }

  @override
  void dispose() {
    print('dispose');
    super.dispose();
  }

  @override
  void reassemble() {
    print('reassemble');
    super.reassemble();
  }

  void changeName(){
    setState(() {
      print('set state');
      name = 'flutter';
    });
  }

}
