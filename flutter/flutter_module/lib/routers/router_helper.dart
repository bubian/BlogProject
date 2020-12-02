import 'dart:async';

import 'package:fluro/fluro.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/routers/Application.dart';

/// 跳转路由帮助类
class AnRouter {
  static Future navigateToByFade(BuildContext context, String path) {
    return Application.router
        .navigateTo(context, path, transition: TransitionType.fadeIn,
            transitionBuilder: (BuildContext context,
                Animation<double> animation,
                Animation<double> secondaryAnimation,
                Widget child) {
      return FadeTransition(
        opacity: animation,
        child: child,
      );
    });
  }

  static Future navigateToBySlide(BuildContext context, String path) {
    return Application.router
        .navigateTo(context, path, transition: TransitionType.inFromRight,
            transitionBuilder: (BuildContext context,
                Animation<double> animation,
                Animation<double> secondaryAnimation,
                Widget child) {
      return SlideTransition(
        position: Tween<Offset>(begin: const Offset(-1, 0), end: Offset.zero)
            .animate(animation),
        child: child,
      );
    });
  }

  static Future navigateToByFade2(BuildContext context, String path) {
    return Navigator.of(context)
        .push(MaterialPageRoute(builder: (BuildContext context) {
      return null;
    }));
  }
}
