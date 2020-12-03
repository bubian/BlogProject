import 'package:flutter/material.dart';
import 'package:flutter_module/routers/routers.dart';

class CustomPageRoute extends PageRouteBuilder {
  final String router;

  CustomPageRoute(this.router)
      : super(
            transitionDuration: Duration(milliseconds: 300),
            pageBuilder: (BuildContext context, Animation<double> animation1,
                Animation<double> animation2) {
              return Routers.getWidgetsGroupRouter(router, context);
            },
            transitionsBuilder: (BuildContext context,
                Animation<double> animation1,
                Animation<double> animation2,
                Widget child) {
              return FadeTransition(
                opacity: Tween(begin: 0.0, end: 1.0).animate(CurvedAnimation(
                  parent: animation1,
                  curve: Curves.fastOutSlowIn, //动画曲线
                )),
                child: child,
              );
            });
}
