import 'dart:io';

import 'package:android_intent/android_intent.dart';
import 'package:flutter/material.dart';

class SystemUtils {
  /// 返回到桌面
  Future<bool> _dialogExitApp(BuildContext context) async {
    if (Platform.isAndroid) {
      var intent = AndroidIntent(
        action: 'android.intent.action.MAIN',
        category: 'android.intent.category.HOME',
      );
      await intent.launch();
    }
    return Future.value(false);
  }
}