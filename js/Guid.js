/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component} from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  AppRegistry,
  ToastAndroid,
  Button
} from 'react-native';
import PropTypes from 'prop-types';
import {StackNavigator} from 'react-navigation';  //导入需要的包
import {Navigator} from "react-native-deprecated-custom-components";
import LoginComponent from './LoginComponent' //引用其他JS文件
import Three from './Three'

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
  android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

export default class Guid extends Component {
 constructor(props){
 super(props);
 }
jumpTo() {
//  alert(this.props);
const {navigator} = this.props;
if(navigator){
navigator.push({
  component: Three,
   name: 'Three'
});
}
  }
  render() {
   let defaultName = 'LoginComponent';
        let defaultComponent = LoginComponent;
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          username:{this.props.username} //获取传过来的值
          password:{this.props.password}
        </Text>
        <Text style={styles.instructions} onPress={this.jumpTo.bind(this)}>
          To get started, edit App.js
        </Text>
        <Text style={styles.instructions}>
          {instructions}
        </Text>
        <Button title='不仅仅是喜欢' onPress={this.jumpTo.bind(this)}/>
 </View>
    );
  }
}

const styles = StyleSheet.create({
  container:{
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome:{
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions:{
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
