//解决java.lang.UnsatisfiedLinkError: dlopen failed: "/data/data/com.example.coolanimation/lib-main/libgnustl_shared.so" is 32-bit instead of 64-bit
defaultConfig {
        //解决报错问题
        //java.lang.UnsatisfiedLinkError: dlopen failed: "/data/data/com.example.coolanimation/lib-main/libgnustl_shared.so" is 32-bit instead of 64-bit
        ndk {
            abiFilters "armeabi-v7a", "x86"
        }
        packagingOptions {
            exclude "lib/arm64-v8a/libgnustl_shared.so"
        }
    }


//解决unable to load script from assets 'index.android.bundle'
1、在app/src/main目录下创建一个assets目录
2、在AS的terminal输入命令：
react-native bundle --platform android --dev false --entry-file index.android.js --bundle-output app/src/main/assets/index.android.bundle --assets-dest app/src/main/res/
3、根据提示修改，如果要删除node_modules文件夹，可以手动直接删除，然后在AS的terminal执行npm install
总结：
index.android.bundle是用来调用原生控件的js脚本，
每次当改变了 index.android.js，都需要使用上面的代码片段，来及时的更新index.android.bundle，
然后打包才可以把新的index.android.js应用上，所以当没有index.android.bundle文件时，React-Native 项目是无法运行的



//Android接入react-native步骤
1、将JS文件，用到的图片（image文件）直接拷贝到Android工程中
2、用npm init生成package.json文件
3、用nmp install --save react-native-deprecated-custom-components安装需要的包
npm install –save react
npm install –save react-native
4、https://raw.githubusercontent.com/facebook/react-native/master/.flowconfig创建.flowconfig文件，我是直接从别的RN项目中拷贝过来的
5、创建index.android.js文件
6、工程的build.gradle文件：
allprojects {
    repositories {
        jcenter()
        maven{
            url "$rootDir/node_modules/react-native/android"
        }
    }
}
7、app的build.gradle文件：
compile 'com.facebook.react:react-native:+'
8、检查Android工程的External Libraries里面的react-native的版本要大于0.20.0，不然找不到ReactApplication
如果你的react-native版本比较低，可能是你没有执行npm install –save react-native，或者检查一下
maven{
            url "$rootDir/node_modules/react-native/android"
        }
9、每次当改变了 index.android.js，都需要执行react-native bundle --platform android --dev false --entry-file index.android.js --bundle-output app/src/main/assets/index.android.bundle --assets-dest app/src/main/res/

10、生成bundle文件
前因：react项目生成app时，所有js文件都被压缩到index.android.bundle文件中，该文件和图片资源都位于assets目录下，app启动时，MainActivity首先加载index.android.bundle，转换为对应的原生试图显示到rootView

后果：react热更新就是在app启动时从服务器下载新的index.android.bundle和图片资源，然后加载新的index.android.bundle文件
生成index.android.bundle文件的步骤：
1、执行命令react-native bundle --platform android --dev false --entry-file index.android.js --bundle-output app/src/main/assets/index.android.bundle --assets-dest app/src/main/res/
生成index.android.bundle文件
2、将index.android.bundle文件和图片资源压缩并上传至服务器供app启动时更新使用

