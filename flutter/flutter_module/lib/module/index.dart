
import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:flutter_module/model/widget.dart';
import 'package:flutter_module/module/widget_demo_home.dart';

HashMap<String,WidgetPoint> getHashMap(){
  HashMap<String,WidgetPoint> hashMap = new HashMap<String,WidgetPoint>();
  WidgetPoint widgetPoint = new WidgetPoint(
      name: 'widget demo home',
      routerName: WidgetDemoHome.routeName,
      buildRouter: (BuildContext context) => WidgetDemoHome()
  );
  hashMap.putIfAbsent(widgetPoint.routerName, () => widgetPoint);
  return hashMap;
}