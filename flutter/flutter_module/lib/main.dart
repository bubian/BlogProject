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
      ),
      home: BlogHome(title: 'Flutter Home'),
      onGenerateRoute: Application.router.generator,
    );
  }
}

void main() => runApp(BlogApp());
