import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/home.dart';
import 'package:flutter_module/routers/Application.dart';
import 'package:flutter_module/routers/routers.dart';

class BlogApp extends StatelessWidget {
  BlogApp() {
    final router = new Router();
    Routers.configureRouters(router);
    Application.router = router;
  }

  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      title: 'Blog Flutter',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        textTheme: TextTheme(
          //设置Material的默认字体样式
          body1: TextStyle(
              color: Colors.black,
              fontSize: 12.0,
              fontWeight: FontWeight.normal,
              decoration: TextDecoration.none),
        ),
      ),
      home: BlogHome(title: 'Flutter Home'),
      onGenerateRoute: Application.router.generator,
    );
  }
}

void main() => runApp(BlogApp());
