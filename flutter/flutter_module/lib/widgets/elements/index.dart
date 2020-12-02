import 'package:flutter_module/model/widget.dart';

import 'Form/index.dart' as Form;

List<WidgetPoint> getWidgets() {
  List<WidgetPoint> list = [];
  list.addAll(Form.widgetPoints);
  return list;
}
