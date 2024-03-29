

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class CupertinoTabBarDemo extends StatefulWidget {
  _Demo createState() => _Demo();
}

class _Demo extends State<CupertinoTabBarDemo> {
  int index = 0;

  changeIndex(int _index) {
    this.setState(() {
      index = _index;
    });
  }
  Widget build(BuildContext context) {
    return Container(
      height: 500,
      child: Scaffold(
          appBar: AppBar(title: const Text('CupertinoTabBarDemo')),
          floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
          body: Center(
            child: Text('CupertinoTabBarDemo in bottom'),
          ),
          bottomNavigationBar: CupertinoTabBar(
            backgroundColor: Color.fromRGBO(244, 244, 244, 0.5),
            currentIndex: index,
            onTap: (i) {
              this.changeIndex(i);
            },
            items: [
              BottomNavigationBarItem(
                title: Text("1"),
                icon: Icon(Icons.add),
              ),
              BottomNavigationBarItem(
                  title: Text("2"),
                  icon: Icon(Icons.delete)
              )],
          )
      ),
    );
  }
}