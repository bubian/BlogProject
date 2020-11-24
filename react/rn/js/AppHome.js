import React, { Component,PureComponent } from "react";
import {
  Platform,  
  StyleSheet,  
  Text,  
  View,  
  Button,  
  Image} from "react-native";

import GridList from 'react-native-grid-list';
import FastGridView from "./widgets/FastGridView";

class ItemData {
  icon;
  name;
  constructor(icon, name) {
    this.icon = icon;
    this.name = name;
  }
}

const items = [
  new ItemData(require("../img/ic_widgets.png"), "React组件"),
  new ItemData(require("../img/ic_widgets.png"), "React组件"),
  new ItemData(require("../img/ic_widgets.png"), "React组件")
]

export default class AppHome extends PureComponent {
  renderItem = ({ item, index }) => (
    <View>
      <Image style={styles.image} source={item.icon} />
    </View>

  );
  render() {
    return (
      <View style={styles.container}>
        <FastGridView/>

        {/* <GridList
          showSeparator
          data={items}
          numColumns={3}
          renderItem={this.renderItem}/> */}
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  image: {
    width: '100%',
    height: '100%',
    borderRadius: 10,
  },
});