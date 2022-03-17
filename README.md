# push_message_register  

Language: English | [中文](https://github.com/chn-sunch/push_message_register/blob/master/README-ZH.md)

Register xiaomi、huawei、oppo、vivo、meizu push, get device brand and token(2022-02 update brand sdk).You need to pass the token and brand to your webserver,Push messages to users using the device manufacturer's SDK,Finally start the APP through deeplink.

step:
1. register and get token
2. save token on webserver
3. push notification message using bard sdk

This project uses push_message_register in Github
[flutter_mycommunity_app](https://github.com/chn-sunch/flutter_mycommunity_app)

# Installer
pubspec.yaml  

    dependencies:
        push_message_register: 0.0.3

# Apply

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
            //vivo config in AndroidManifest.xml
            Map apikey = {
               "XIAOMI_APP_ID": "xiaomi appid",
               "XIAOMI_APP_KEY": "xiaomi key",
               "HUAWEI_APP_ID": "huawei appid",
               "HUAWEI_APP_KEY": "",//don't fill
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

  
# CONFIG
1 . uses-permission 

       <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE"  /><!-- huawei icon-->
       <uses-permission android:name="com.vivo.notification.permission.BADGE_ICON" /><!-- VIVO icon -->
       <uses-permission android:name="android.permission.VIBRATE" /><!--xiaomi-->
       <uses-permission android:name="android.permission.WAKE_LOCK" /><!--Allow the program to still run the background process after the phone screen is turned off, keep pushing long links -->
       <uses-permission android:name="android.permission.READ_PHONE_STATE" /><!--Allow apps to access phone status  -->
       <uses-permission android:name="android.permission.RECEIVE_USER_PRESENT" /><!--Allows apps to receive screen-lit or unlocked broadcasts -->
       <uses-permission android:name="android.permission.RESTART_PACKAGES" /><!--Allow the program to end the task, the user closes the push service, and the push service exits  -->
       <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE " /><!--Allows programs to write to external storage for saving SDK running logs  -->
       <uses-permission android:name="android.permission.GET_TASKS" /><!--Allows programs to obtain task information  -->
       <uses-permission android:name="com.heytap.mcs.permission.RECIEVE_MCS_MESSAGE"/><!-- oppo puush-->
       <uses-permission android:name="com.coloros.mcs.permission.RECIEVE_MCS_MESSAGE"/><!-- oppo push-->
       <!-- Compatible with Flyme5 permission configuration-->
       <uses-permission android:name="com.meizu.flyme.push.permission.RECEIVE" />
       <permission android:name="com.jiandanyidian.flutter_mycommunity_app.push.permission.MESSAGE"
                   android:protectionLevel="signature"/>
       <uses-permission android:name="com.jiandanyidian.flutter_mycommunity_app.push.permission.MESSAGE" />
       <!-- Compatible with Flyme3 permission configuration-->
       <uses-permission android:name="com.meizu.c2dm.permission.RECEIVE" />
       <permission android:name="com.jiandanyidian.flutter_mycommunity_app.permission.C2D_MESSAGE" android:protectionLevel="signature"/>
       <uses-permission android:name="com.jiandanyidian.flutter_mycommunity_app.permission.C2D_MESSAGE"/>
    
       <permission
               android:name="com.mycommunity_app.flutter_mycommunity_app.permission.MIPUSH_RECEIVE"
               android:protectionLevel="signature" /><!-- xiaomi push-->
    
       <uses-permission android:name="com.mycommunity_app.flutter_mycommunity_app.permission.MIPUSH_RECEIVE" /><!-- xiaomi push-->
    
2 . huawei phone

android-app add the agconnect-services.json file downloaded from the Huawei Developer Platform

android build.gradle add

    dependencies {
        classpath 'com.huawei.agconnect:agcp:1.6.0.300'
    }
app build.gradle add
   
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

3 . xiaomi phone 

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

4 . oppo phone


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
    
5 . vivo phone


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
                <action android:name="com.vivo.pushclient.action.RECEIVE"/>
            </intent-filter>
    </receiver>
    
 6. meizu phone
 
 
    <service
                    android:name="com.meizu.cloud.pushsdk.NotificationService"
                    android:exported="true"/> 
                    
    <receiver android:name="com.jiandanyidian.push_message_register.MeizuRecevier">
            <intent-filter>
                <action android:name="com.meizu.flyme.push.intent.MESSAGE" />
                <action android:name="com.meizu.flyme.push.intent.REGISTER.FEEDBACK" />
                <action android:name="com.meizu.flyme.push.intent.UNREGISTER.FEEDBACK"/>
                <action android:name="com.meizu.c2dm.intent.REGISTRATION" />
                <action android:name="com.meizu.c2dm.intent.RECEIVE" />
                <category android:name="com.mycommunity_app.flutter_mycommunity_app" />
            </intent-filter>
    </receiver>      

# SDK
huawei：[HMS Core SDK](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/android-integrating-sdk-0000001050040084)

xiaomi：[小米 Android SDK](https://dev.mi.com/console/doc/detail?pId=41)

ViVo: [Vivo Android SDK](https://dev.vivo.com.cn/documentCenter/doc/365)

Oppo: [oppo Android SDK](https://open.oppomobile.com/wiki/doc/#id=11050)

meizu: [meizu_Android SDK](https://open-wiki.flyme.cn/doc-wiki/index?title=%E9%AD%85%E6%97%8F%E6%8E%A8%E9%80%81%E5%B9%B3%E5%8F%B0%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A3#id?129)

# IOS AND FCM
ios: flutter_apns

fcm: firebase_messaging
