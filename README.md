### 安装开发环境
1.Android sdk -> <https://developer.android.com/studio/index.html>

2.Xcode -> App store
### 安装Appium
**首先，应当是检查自己是否已经安装homebrew**

在终端中输入brew -v 如果出现版本信息则说明已经安装

brew -v

Homebrew 0.9.5

如果未安装则先安装，执行命令

ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install6)"

**其次，安装Appium**

***命令行安装***

安装node -> brew install node

安装完成后在终端输入node -v命令检查是否安装成功，出现版本信息说明安装成功

安装appium -> npm install -g appium 

推荐taobao的npm镜像，下载速度较快

npm --registry http://registry.npm.taobao.org install -g appium

安装好Appium之后，运行appium-doctor，检查自己的各种环境变量是否配置成功

安装appium client -> npm install wd

启动 -> appium &

***dmg安装***

Appium国内下载地址 <http://pan.baidu.com/s/1jGvAISu>

最新更新的是： appium-1.4.13.dmg & AppiumForWindows-1.4.16.1.zip

TesterHome官方百度网盘 <http://pan.baidu.com/s/1jGvAISu>

Appium各版本更新日志 <https://github.com/appium/appium/releases/> 以及 <https://discuss.appium.io/>

Appium官方下载地址 <https://bitbucket.org/appium/appium.app/downloads/>

**最后，设置环境变量**

修改配置文件 .bash_profile

export ANDROID_HOME=/Applications/android-sdk-macosx

export APPIUM_HOME=/Applications/Appium.app/Contents/Resources/node_modules/

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home

export AAPT_HOME=/Applications/android-sdk-macosx/build-tools/

export PATH=${PATH}:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$APPIUM_HOME/.bin:/usr/local/ruby-2.3.0/bin:$AAPT_HOME/23.0.3/

注意：环境变量的具体路径以实际路径为主

### 安装idevices套件
brew install libimobiledevice

源码地址 <https://github.com/benvium/libimobiledevice-macosx>

### AutoTraveler配置说明
详情参考默认配置文件里面的注释，有详细说明

ios.xml

android.xml

### AutoTraveler启动命令
java -jar AutoTraveler.jar ｛系统类型｝ ｛配置文件｝

启动Android的遍历测试

java -jar AutoTraveler.jar android confige/android.xml

启动iOS的遍历测试

java -jar AutoTraveler.jar ios confige/ios.xml

注意：日志会在同目录的output下，按时间、udid分类输出
