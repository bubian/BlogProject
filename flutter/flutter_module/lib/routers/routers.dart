import 'dart:collection';

import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/model/widget.dart';
import 'package:flutter_module/routers/router_hander.dart';
import '../widgets/index.dart';

import 'package:flutter_module/module/index.dart' as moduleWidget;

class Routers {
  static String root = "/";
  static void configureRouters(FluroRouter router) {
    List<WidgetPoint> widgetList = new WidgetList().getWidget();
    router.notFoundHandler = new Handler(
      // ignore: missing_return
      handlerFunc: (BuildContext context,Map<String, List<String>> params){

      });
    router.define(root, handler: homeHandler);

    router.define('/category/:type', handler: categoryHandler);
    router.define('/category/error/404', handler: widgetNotFoundHandler);

    widgetList.forEach((w){
      Handler handler = new Handler(
        handlerFunc: (BuildContext context, Map<String, List<String>> params){
          return w.buildRouter(context);
        }
      );
      router.define('${w.routerName}', handler: handler);
    });
  }

  static HashMap<String, WidgetPoint> widgetsGroupRouter;

  static void _initWidgetsGroupRouter(){
    widgetsGroupRouter = new HashMap<String, WidgetPoint>();
    widgetsGroupRouter.addAll(moduleWidget.getHashMap());
  }

  static Widget getWidgetsGroupRouter(String path,BuildContext context){
    if(null == widgetsGroupRouter){
      _initWidgetsGroupRouter();
    }
    WidgetPoint widgetPoint = widgetsGroupRouter[path];
    Widget widget;
    if(null != widgetPoint){
      widget = widgetPoint.buildRouter(context);
    }
    return widget;
  }
}
