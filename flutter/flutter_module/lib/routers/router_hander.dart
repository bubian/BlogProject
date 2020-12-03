import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/components/category.dart';
import 'package:flutter_module/home.dart';
import 'package:flutter_module/widgets/404.dart';

var homeHandler = new Handler(
    handlerFunc: (BuildContext context, Map<String, List<String>> params) {
  return new BlogHome();
});

var widgetNotFoundHandler = new Handler(
    handlerFunc: (BuildContext context, Map<String, List<String>> params) {
  return new WidgetNotFound();
});

var categoryHandler = new Handler(
  handlerFunc: (BuildContext context, Map<String, List<String>> params) {
    String name = params["type"]?.first;

    return new CategoryHome(name);
  },
);
