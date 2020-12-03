import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/routers/router_helper.dart';

class ItemGvTb extends StatelessWidget {
  final ItemModel data;

  ItemGvTb({Key key, this.data}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      child: getItemView(),
      onTap: () {
        AnRouter.navigateToByFade3(context, data.router);
      },
    );
  }

  Widget getItemView() {
    return Container(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          Image(
            image: AssetImage(data.icon),
            width: 42,
            height: 42,
          ),
          Padding(
            padding: EdgeInsets.only(top: 8),
            child: Text(
              data.title,
              textAlign: TextAlign.center,
              style: TextStyle(
                  color: Colors.black,
                  fontSize: 12,
                  fontWeight: FontWeight.normal,
                  decoration: TextDecoration.none),
            ),
          )
        ],
      ),
    );
  }
}

class ItemModel {
  final String icon;
  final String title;
  final String router;

  ItemModel(this.icon, this.title, this.router);
}
