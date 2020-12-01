import 'package:fluro/fluro.dart';
import 'package:flutter_module/routers/router_hander.dart';

class Routers {
  static String root = "/";
  static void configureRouters(Router router) {
    router.define(root, handler: homeHandler);
  }
}
