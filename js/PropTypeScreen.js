import React,{Component} from 'react';
import {
StyleSheet,
} from 'react-native';
import {Navigator} from 'react-native-deprecated-custom-components';
import Three from './Three'
import LoginComponent from './LoginComponent'
class PropTypeScreen extends Component{
constructor(props){
super(props);
}
render(){
return(
 <Navigator
 initialRoute = {{title: 'My Initial Scene',index:0}}
 configureScene = {(route)=>{ return {...Navigator.SceneConfigs.FadeAndroid, gestures: false};}}
 renderScene = {(route,navigator)=>{
 return <Three title={route.title}
 onForward={() => { //刷新当前页面的数据
 const newIndex = route.index + 1;
       navigator.push({
       title: '我要送你99朵玫瑰花' + newIndex,
      index:newIndex
       });
       }}
onBack={() => { //回退
if(route.index > 0){
 navigator.pop();
}
       }}
navigator={navigator}
/>
 }}
 />
);
}
}
export default PropTypeScreen;