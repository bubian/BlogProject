import 'package:flutter/cupertino.dart';
import 'package:flutter_module/item/item_gv_tb.dart';
import 'package:flutter/material.dart';

class BlogHome extends StatefulWidget {
  BlogHome({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<BlogHome> {
  List<ItemModel> _icons = [ItemModel("assets/home/ic_widgets.png","组件view",12,false)]; //保存Icon数据
  @override
  Widget build(BuildContext context) {
    return GridView.builder(
        padding: EdgeInsets.only(top: 16),
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 4, //每行三列
            childAspectRatio: 1.0 //显示区域宽高相等
        ),
        itemCount: _icons.length,
        itemBuilder: (context, index) {
          return ItemGvTb(data: _icons[index]);
        }
    );
  }
}