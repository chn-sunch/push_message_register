# push_message_register  


注册小米、华为、OPPO、VIVO、魅族推送，得到设备品牌和token（2022-02 更新各厂商sdk）。你需要将token和brand传到你的服务器，使用设备厂商的SDK将消息推送给用户，最后通过deeplink启动app。

步骤：
1. 注册获取token
2. 服务器端保存token
3. 通过厂商服务端sdk推送通知消息

使用此插件的开源项目
[flutter_mycommunity_app](https://github.com/chn-sunch/flutter_mycommunity_app)

# 安装
在工程 pubspec.yaml 中加入 

    dependencies:
        push_message_register: 0.0.3

# 使用

    if(Platform.isAndroid) {
        _pushMessageRegister.onReceiveMessage().listen((event) {
            if (event != null) {
                if (event["result"] == "success") {
                    Global.devicetoken = event["token"].toString();
                    Global.brand = event["brand"].toString();
                    if (Global.profile.user != null) {
                        _userService.updatePushToken(
                        Global.profile.user!.uid, Global.profile.user!.token!,
                            Global.brand, Global.devicetoken, (error, msg) {
                            ShowMessage.showToast(msg);
                        });
                      }
                    }
                }
            });
            //vivo配置在AndroidManifest.xml
            Map apikey = {
               "XIAOMI_APP_ID": "小米appid",
               "XIAOMI_APP_KEY": "小米key",
               "HUAWEI_APP_ID": "华为appid",
               "HUAWEI_APP_KEY": "",//不用填
               "OPPO_APP_KEY": "oppo key",
               "OPPO_APP_SECRET": "oppo secret",
               "MEIZU_APP_ID": "meizu appid",
               "MEIZU_APP_KEY": "meizu appkey"
            };
            _brand = await PushMessageRegister.registerApi(apikey);
    }
        
    if(Platform.isIOS || _brand == "other"){
        //_registerFcmOrApns(connector); 
    } 

  
# 配置
1 . 权限申请

       <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE" /><!-- 华为角标 -->
       <uses-permission android:name="com.vivo.notification.permission.BADGE_ICON" /><!-- VIVO角标权限 -->
       <uses-permission android:name="android.permission.VIBRATE" /><!--振动器权限，小米推送必须-->
       <uses-permission android:name="android.permission.WAKE_LOCK" /><!--允许程序在手机屏幕关闭后，后台进程仍然运行，保持推送长链接  -->
       <uses-permission android:name="android.permission.READ_PHONE_STATE" /><!--允许应用访问手机状态  -->
       <uses-permission android:name="android.permission.RECEIVE_USER_PRESENT" /><!--允许应用可以接收点亮屏幕或解锁广播 -->
       <uses-permission android:name="android.permission.RESTART_PACKAGES" /><!--允许程序结束任务，用户关闭推送服务，推送服务退出  -->
       <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE " /><!--允许程序写入外部存储，用于保存SDK运行日志  -->
       <uses-permission android:name="android.permission.GET_TASKS" /><!--允许程序获取任务信息  -->
       <uses-permission android:name="com.heytap.mcs.permission.RECIEVE_MCS_MESSAGE"/><!-- oppo推送-->
       <uses-permission android:name="com.coloros.mcs.permission.RECIEVE_MCS_MESSAGE"/><!-- oppo推送-->
       <!-- 兼容 Flyme5 的权限配置-->
       <uses-permission android:name="com.meizu.flyme.push.permission.RECEIVE" />
       <permission android:name="com.jiandanyidian.flutter_mycommunity_app.push.permission.MESSAGE"
                   android:protectionLevel="signature"/>
       <uses-permission android:name="com.jiandanyidian.flutter_mycommunity_app.push.permission.MESSAGE" />
       <!-- 兼容 Flyme3 的权限配置-->
       <uses-permission android:name="com.meizu.c2dm.permission.RECEIVE" />
       <permission android:name="com.jiandanyidian.flutter_mycommunity_app.permission.C2D_MESSAGE" android:protectionLevel="signature"/>
       <uses-permission android:name="com.jiandanyidian.flutter_mycommunity_app.permission.C2D_MESSAGE"/>
    
       <permission
               android:name="com.mycommunity_app.flutter_mycommunity_app.permission.MIPUSH_RECEIVE"
               android:protectionLevel="signature" /><!-- 小米推送-->
    
       <uses-permission android:name="com.mycommunity_app.flutter_mycommunity_app.permission.MIPUSH_RECEIVE" /><!-- 小米推送-->
    
2 . 华为手机

android-app 里添加从华为开者平台下载的agconnect-services.json 文件

项目下的build.gradle添加

    dependencies {
        classpath 'com.huawei.agconnect:agcp:1.6.0.300'
    }
app下的build.gradle添加
   
    dependencies {
        implementation 'com.huawei.hms:push:6.1.0.300'
    }
    
    apply plugin: 'com.android.application'
    apply plugin: 'com.huawei.agconnect'
    
AndroidManifest.xml    

    <service
        android:name="com.jiandanyidian.push_message_register.HuaWeiReceiver" android:exported="false">
        <intent-filter>
           <action android:name="com.huawei.push.action.MESSAGING_EVENT" />
        </intent-filter>
    </service>

3 . 小米手机

AndroidManifest.xml

    <service
                android:name="com.xiaomi.push.service.XMJobService"
                android:enabled="true"
                android:exported="false"
                android:permission="android.permission.BIND_JOB_SERVICE"
                android:process=":pushservice" />
    <service
                android:name="com.xiaomi.push.service.XMPushService"
                android:enabled="true"
                android:process=":pushservice" />
    <service
                android:name="com.xiaomi.mipush.sdk.PushMessageHandler"
                android:enabled="true"
                android:exported="true" />
    <service
                android:name="com.xiaomi.mipush.sdk.MessageHandleService"
                android:enabled="true" />  
                
    <receiver android:name="com.xiaomi.push.service.receivers.NetworkStatusReceiver" android:exported="true">
                <intent-filter>
                   <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
                   <category android:name="android.intent.category.DEFAULT" />
                </intent-filter>
    </receiver>
    
    <receiver android:name="com.xiaomi.push.service.receivers.PingReceiver"
                     android:exported="false"
                     android:process=":pushservice">
                 <intent-filter>
                     <action android:name="com.xiaomi.push.PING_TIMER" />
                 </intent-filter>
    </receiver>
    
    <receiver android:name="com.jiandanyidian.push_message_register.XiaoMiReceiver" android:exported="true">
        <intent-filter>
            <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
                 </intent-filter>
                 <intent-filter>
                     <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
                 </intent-filter>
                 <intent-filter>
                     <action android:name="com.xiaomi.mipush.ERROR" />
                 </intent-filter>
    </receiver>                 

4 . oppo手机


    <service android:name="com.jiandanyidian.push_message_register.OppoCompatibleDataMessageCallbackService" android:permission="com.coloros.mcs.permission.SEND_MCS_MESSAGE">
          <intent-filter>
                <action android:name="com.coloros.mcs.action.RECEIVE_MCS_MESSAGE"/>
            </intent-filter>
    </service>
    
    <service android:name="com.jiandanyidian.push_message_register.OppoDataMessageCallbackService" android:permission="com.heytap.mcs.permission.SEND_PUSH_MESSAGE">
            <intent-filter>
                <action android:name="com.heytap.mcs.action.RECEIVE_MCS_MESSAGE"/>
                <action android:name="com.heytap.msp.push.RECEIVE_MCS_MESSAGE"/>
            </intent-filter>
    </service>
    
5 . vivo手机


    <!--Vivo Push SDK的版本信息-->
    <meta-data android:name="sdk_version_vivo"
                    android:value="484"/>
    <meta-data
                    android:name="com.vivo.push.api_key"
                    android:value="api_key"/>
    <meta-data
                    android:name="com.vivo.push.app_id"
                    android:value="app_id"/>
    <receiver android:name="com.jiandanyidian.push_message_register.VivoReceiver"
                android:exported="false">
            <intent-filter>
                <!--接收push消息-->
                <action android:name="com.vivo.pushclient.action.RECEIVE"/>
            </intent-filter>
    </receiver>
    
 6. 魅族手机
 
 
    <service
                    android:name="com.meizu.cloud.pushsdk.NotificationService"
                    android:exported="true"/> 
                    
    <receiver android:name="com.jiandanyidian.push_message_register.MeizuRecevier">
            <intent-filter>
                <!-- 接收 push 消息 -->
                <action android:name="com.meizu.flyme.push.intent.MESSAGE" />
                <!-- 接收 register 消息 -->
                <action android:name="com.meizu.flyme.push.intent.REGISTER.FEEDBACK" />
                <!-- 接收 unregister 消息-->
                <action android:name="com.meizu.flyme.push.intent.UNREGISTER.FEEDBACK"/>
                <!-- 兼容低版本 Flyme3 推送服务配置 -->
                <action android:name="com.meizu.c2dm.intent.REGISTRATION" />
                <action android:name="com.meizu.c2dm.intent.RECEIVE" />
                <category android:name="com.mycommunity_app.flutter_mycommunity_app" />
            </intent-filter>
    </receiver>      

# SDK
华为：[HMS Core SDK](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/android-integrating-sdk-0000001050040084)

小米：[小米 Android SDK](https://dev.mi.com/console/doc/detail?pId=41)

ViVo: [Vivo Android SDK](https://dev.vivo.com.cn/documentCenter/doc/365)

Oppo: [oppo Android SDK](https://open.oppomobile.com/wiki/doc/#id=11050)

魅族: [meizu_Android SDK](https://open-wiki.flyme.cn/doc-wiki/index?title=%E9%AD%85%E6%97%8F%E6%8E%A8%E9%80%81%E5%B9%B3%E5%8F%B0%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A3#id?129)

# IOS和FCM推送
ios推送 flutter_apns

fcm推送 firebase_messaging
