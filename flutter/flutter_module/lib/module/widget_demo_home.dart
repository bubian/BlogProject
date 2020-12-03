import 'package:flutter/material.dart';
import 'package:flutter_module/components/cate_card.dart';
import 'package:flutter_module/model/cat.dart';

class WidgetDemoHome extends StatefulWidget {
  static const String routeName = '/module/widgetDemoHome';

  final CatControlModel catControlModel;

  WidgetDemoHome()
      : catControlModel = new CatControlModel(),
        super();

  @override
  State<StatefulWidget> createState() => _WidgetDemoHomeState(catControlModel);
}

class _WidgetDemoHomeState extends State<WidgetDemoHome> {
  CatControlModel catModel;
  List<Cat> categories = [];

  _WidgetDemoHomeState(this.catModel) : super() {
    initData();
  }

  void initData() {
    catModel.getList().then((List < Cat > data){
      if(data.isNotEmpty){
        setState(() {
          categories = data;
        });
      }
    });
  }

  Widget buildGrid() {
    List<Widget> tiles =[];
    for(Cat item in categories){
      tiles.add(new CateCard(category: item));
    }
    return new ListView(
      children: tiles,
    );
  }

  @override
  Widget build(BuildContext context) {
    if (categories.length == 0){
      return ListView(
        children: <Widget>[new Container()],
      );
    }
    return Container(
      color: Theme.of(context).backgroundColor,
      padding: EdgeInsets.only(bottom: 16),
      child: this.buildGrid(),
    );
  }
}