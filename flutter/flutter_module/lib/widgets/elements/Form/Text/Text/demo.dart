

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class TextDemo extends StatefulWidget {
  _Demo createState() => _Demo();
}

class _Demo extends State<TextDemo> {
  int index = 0;
  Duration timer = new Duration(minutes: 50);

  Widget build(BuildContext context) {
    return  Text("i'm a text");
  }
}