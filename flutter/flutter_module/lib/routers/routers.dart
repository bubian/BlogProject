import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/model/widget.dart';
import 'package:flutter_module/routers/router_hander.dart';
import '../widgets/index.dart';

class Routers {
  static String root = "/";
  static void configureRouters(Router router) {
    List<WidgetPoint> widgetList = new WidgetList().getWidget();
    router.notFoundHandler = new Handler(
      handlerFunc: (BuildContext context,Map<String, List<String>> params){

      });
    router.define(root, handler: homeHandler);

    widgetList.forEach((w){
      Handler handler = new Handler(
        handlerFunc: (BuildContext context, Map<String, List<String>> params){
          return w.buildRouter(context);
        }
      );
      router.define('${w.routerName}', handler: handler);
    });
  }
}
