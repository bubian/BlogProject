import 'dart:async';
import 'package:flutter_module/utils/sql.dart';

abstract class CatInterface {
  int get id;

  //类目名称
  String get name;

  //描述
  String get desc;

  //第几级类目，默认 1
  int get depth;

  //父类目id，没有为 0
  int get parentId;
}

class Cat implements CatInterface {
  int id;
  String name;
  String desc;
  int depth;
  int parentId;

  Cat({this.id, this.name, this.desc, this.depth, this.parentId});

  Cat.fromJSON(Map json)
      : id = json['id'],
        name = json['name'],
        desc = json['desc'],
        depth = json['depth'],
        parentId = json['parentId'];

  Map toMap() {
    return {
      'id': id,
      'name': name,
      'desc': desc,
      'depth': depth,
      'parentId': parentId
    };
  }

  Map toSqlCondition() {
    Map _map = this.toMap();
    Map condition = {};
    _map.forEach((k, value) {
      if (value != null) {
        condition[k] = value;
      }
    });
    if (condition.isEmpty) {
      return {};
    }
    return condition;
  }

  @override
  String toString() {
    return '(Cat $name)';
  }
}

class CatControlModel {
  final String table = 'cat';
  Sql spl;

  CatControlModel() {
    spl = Sql.setTable(table);
  }

  /// 获取一级类目
  Future<List> mainList() async{
    List listJson =  await spl.getByCondition(conditions: {'parentId': 0});
    List<Cat> cats = listJson.map((json) {
      return new Cat.fromJSON(json);
    }).toList();
    return cats;
  }

  Future<List<Cat>> getList([Cat cat]) async {
    if (null == cat) {
      cat = new Cat(depth: 1, parentId: 0);
    }
    List listJson = await spl.getByCondition(conditions: cat.toSqlCondition());
    List<Cat> cats = listJson.map((json) {
      return new Cat.fromJSON(json);
    }).toList();
    return cats;
  }

  // 通过name获取Cat对象信息
  Future<Cat> getCatByName(String name) async {
    List json = await spl.getByCondition(conditions: {'name': name});
    if (json.isEmpty) {
      return null;
    }
    return new Cat.fromJSON(json.first);
  }
}
