import React,{Component} from 'react';
import {
View,
TextInput,
StyleSheet,
Text,
Image,
TouchableHighlight
} from 'react-native';
import PropTypes from 'prop-types';
class Three extends Component{
constructor(props){
super(props);
this.state={
theme:''
};
}
//检查类型
static propTypes = {
    title: PropTypes.string.isRequired,
    onForward: PropTypes.func.isRequired,
    onBack: PropTypes.func.isRequired,
    navigation:PropTypes.object
  }
render(){
return(
  <View style={{margin:10,flex:1,flexDirection:'column'}}>
  <TextInput style={[styles.basic,{marginTop:30}]}
  placeholder='小猪佩奇'
  placeholderTextColor='#757575'
  onChangeText={(text)=>{this.setState({theme:text});}}/>
  <Text style={{textAlign: 'center',color: '#333333',marginBottom: 5}}>
    {this.props.title}
  </Text>
    <TouchableHighlight onPress={this.props.onForward}>
            <Text>点我进入下一场景</Text>
          </TouchableHighlight>
          <TouchableHighlight onPress={this.props.onBack}>
            <Text>点我返回上一场景</Text>
          </TouchableHighlight>
  <Image source={require('./../image/kaitwo.jpg')} style={styles.imageBackground} />
  </View>
);
}
}
const styles = StyleSheet.create({
basic:{
padding:10
},
imageBackground:{
flex:1,
alignItems:'center',
justifyContent:'center',
width:null,
height:null,
resizeMode:Image.resizeMode.contain,
backgroundColor:'rgba(0,0,0,0)'
},
});
export default Three;