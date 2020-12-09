import React, { Component, PureComponent } from "react";
import { StackNavigator } from 'react-navigation';
import lg from "../login/login";
import {
  FlatList,
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Dimensions,
  Text,
  Image,
  TouchableOpacity,
  View,
} from "react-native";


const DATA = [
  {
    icon: require("../../img/ic_widgets.png"),
    title: "React组件",
  },
  {
    icon: require("../../img/ic_login.png"),
    title: "React登录",
    router: 'Login',
  },
];


const Item = ({ item, onPress, style }) => {
  return (
    <View
      style={{
        flexDirection: "column",
        alignItems: "center",
        height: 80,
        width: (WINDOW_WIDTH - 32) / 4,
      }}
    >
      <Image style={styles.image} source={item.icon} />
      <Text Text style={styles.title}>
        {" "}
        {item.title}
      </Text>
    </View>
  );
};

WINDOW_WIDTH = Dimensions.get("screen").width;

const FastGridView = () => {
  const renderItem = ({ item }) => {
    return <Item item={item} styles={styles.item} onPress={() => {}} />;
  };

  return (
    <SafeAreaView style={styles.container}>
      <FlatList
        style={styles.fl}
        showsVerticalScrollIndicator={false}
        horizontal={false}
        columnWrapperStyle={styles.columnStyle}
        numColumns={4}
        data={DATA}
        renderItem={renderItem}
        keyExtractor={(item) => item.icon}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 16,
    // marginTop: StatusBar.currentHeight || 0,
  },
  fl: {
    flex: 1,
    alignSelf: "flex-start",
  },
  item: {
    // padding: 2,
    flex: 1,
    // marginVertical: 8,
    // marginHorizontal: 16,
  },
  title: {
    fontSize: 14,
    marginTop: 8,
    color: "#000",
  },
  image: {
    width: 40,
    height: 40,
    // borderRadius: 10,
  },
  columnStyle: {},
});

export default FastGridView;
