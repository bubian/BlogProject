import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/item/item_gv_tb.dart';

import 'widgets/elements/Form/Text/Text/demo.dart';

class BlogHome extends StatefulWidget {
  BlogHome({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _BlogHomeState createState() => _BlogHomeState();
}

class _BlogHomeState extends State<BlogHome> {
  List<ItemModel> _icons = [
    ItemModel("assets/home/ic_widgets.png", "组件view", TextDemo.routeName)
  ]; //保存Icon数据
  @override
  Widget build(BuildContext context) {
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
}
