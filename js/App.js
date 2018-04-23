/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  AppRegistry,
  ToastAndroid,
  Button
} from 'react-native';
import {StackNavigator} from 'react-navigation';  //导入需要的包
import {Navigator} from "react-native-deprecated-custom-components";
import LoginComponent from './LoginComponent' //引用其他JS文件
import Guid from './Guid.js'

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
  android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

export default class App extends Component<Props> {
check(){
const {navigator} = this.props;
const self = this;
ToastAndroid.show('在这里可以跳转',ToastAndroid.LONG);
if(navigator){
navigator.push({
name:'Guid',
Component:Guid,
params:{
username:this.state.username,
password:this.state.password,
}
});
}
}
  render() {
   let defaultName = 'LoginComponent';
        let defaultComponent = LoginComponent;
    return (
<Navigator //根据name和component的值进行页面跳转
initialRoute={{name:defaultName,component:defaultComponent}}
//页面切换的动画
configureScene={(route)=>{return Navigator.SceneConfigs.VerticalDownSwipeJump;}}
renderScene={(route,navigator)=>{
let Component = route.component;
return(<Component{...route.params} navigator={navigator}/>);
}}/>

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
AppRegistry.registerComponent('App', () => App);
