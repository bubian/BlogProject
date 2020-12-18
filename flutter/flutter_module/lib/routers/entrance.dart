import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_module/routers/router_helper.dart';
import 'package:uni_links/uni_links.dart';

/// 外部启动该页面，比如浏览器启动到对应的RN页面

/// eum 类型
enum UniLinksType {
  /// string link
  string,
}

class Entrance extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _Entrance();
}

class _Entrance extends State<Entrance>{

  final UniLinksType _type = UniLinksType.string;
  StreamSubscription _sub;

  @override
  void initState() {
    super.initState();
    // scheme初始化，保证有上下文，需要跳转页面
    initPlatformState();
  }
  @override
  Widget build(BuildContext context) {
    return Material(
      child: Column(
        children: <Widget>[
          Expanded(
            child: Text('Hello Flutter scaffold'),
          ),
        ],
      ),
    );
  }

  Future<void> initPlatformState() async {
    if (_type == UniLinksType.string) {
      await initPlatformStateForStringUniLinks();
    }else {
      await initPlatformStateForUriUniLinks();
    }
  }

  Future<void> initPlatformStateForStringUniLinks() async{
    String initialLink;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      initialLink = await getInitialLink();
      if (initialLink != null) {
        //  跳转到指定页面
        await AnRouter.navigateToByFade(context, initialLink);
      }
    } on PlatformException {
      initialLink = 'Failed to get initial link.';
    } on FormatException {
      initialLink = 'Failed to parse the initial link as Uri.';
    }
    // Attach a listener to the links stream
    _sub = getLinksStream().listen((String link) {
      if (!mounted || link == null) return;
      //  跳转到指定页面
      AnRouter.navigateToByFade(context, link);
    }, onError: (Object err) {
      if (!mounted) return;
    });
  }

  Future<void> initPlatformStateForUriUniLinks() async {
    // Get the latest Uri
    Uri initialUri;
    String initialLink;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      initialUri = await getInitialUri();
      print('initial uri: ${initialUri?.path}${initialUri?.queryParametersAll}');
      initialLink = initialUri?.toString();
    } on PlatformException {
      initialUri = null;
      initialLink = 'Failed to get initial uri.';
    } on FormatException {
      initialUri = null;
      initialLink = 'Bad parse the initial link as Uri.';
    }
    // Attach a listener to the links stream
    _sub = getUriLinksStream().listen((Uri link) {
      if (!mounted || link == null) return;
      // 跳转到指定页面
      AnRouter.navigateToByFade(context, link.path);
    }, onError: (Object err) {
      if (!mounted) return;
    });
  }

  @override
  void dispose() {
    super.dispose();
    if (_sub != null) _sub.cancel();
  }
}
