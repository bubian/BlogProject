import 'package:flutter/material.dart';
import 'package:flutter_module/model/widget.dart';

import 'Text/Text/demo.dart';

List<WidgetPoint> widgetPoints = [
  WidgetPoint(
      name: 'Text',
      routerName: TextDemo.routeName,
      buildRouter: (BuildContext context) => TextDemo())
];
