import React,{Component} from 'react';
import {View,
TextInput,
StyleSheet,
TouchableNativeFeedback,
Text,
TouchableHighLight,
Navigator,
ToastAndroid
} from 'react-native';  //导入需要的包
import SplashScreen from './SplashScreen'; //引用其他JS文件
import PropTypes from 'prop-types';
import Guid from './Guid'
class LoginComponent extends Component{

constructor(props){
super(props);
this.state = {
username:'',
password:'',
};
}
// componentDidMount() {
//        SplashScreen.hide();
//    }
check(username,password){
const {navigator} = this.props; //从APP.js里面的Navigator组件获得
const self = this;
if(username.length == 0){
ToastAndroid.show('请填写用户名',ToastAndroid.LONG);
}else if(password.length == 0){
ToastAndroid.show('请填写密码',ToastAndroid.LONG);
}else{
ToastAndroid.show('在这里可以跳转',ToastAndroid.LONG);
if(navigator){
navigator.push({ //根据name和component跳转到相应的界面（组件）
name:'Guid',
component:Guid,
params:{ //传递数据
username:this.state.username,
password:this.state.password,
}
});
}
}
}
render(){
return(
<View style={{margin:10,flex:1,flexDirection:'column'}}>
<TextInput style={[styles.basic,{marginTop:30}]}
placeholderTextColor='#757575'
placeholder='请输入账号'
onChangeText={(text)=>{this.setState({username: text});}}/>
<TextInput style={styles.basic}
placeholderTextColor='#757575'
placeholder='请输入密码'
onChangeText={(text)=>{this.setState({password:text});}}
/>
<TouchableNativeFeedback
  background={TouchableNativeFeedback.SelectableBackground()}
  onPress={() => this.check(this.state.username, this.state.password)}>
  <View style={{borderRadius: 10, backgroundColor: '#0097A7', marginTop: 30}}>
  <Text style={styles.text}>登录</Text>
  </View>
</TouchableNativeFeedback>
</View>

);
}
}
const styles = StyleSheet.create({  //自定义样式
basic:{
padding:10
},
text:{
fontSize: 18,
        color: '#FFFFFF',
        margin: 10,
        textAlign: 'center'
}
});
export default LoginComponent; //将自己暴露给别人