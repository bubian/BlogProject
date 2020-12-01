import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/home.dart';

var homeHandler = new Handler(
    handlerFunc: (BuildContext context,Map<String,List<String>> params){
      return new BlogHome();
    }
);