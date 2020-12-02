import 'package:flutter_module/model/widget.dart';

import 'elements/index.dart' as elements;

class WidgetList {
  WidgetList();

  List<WidgetPoint> getWidget() {
    List<WidgetPoint> list = [];
    list.addAll(elements.getWidgets());
    return list;
  }
}
