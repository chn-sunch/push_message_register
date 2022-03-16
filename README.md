# push_message_register  

注册小米、华为、OPPO、VIVO、魅族厂商推送返回brand和token（当前版本使用的是各厂商最新的sdk）。你需要将token和brand传到自己的服务器当长连接中断后通过厂商系统通道将通知发给用户，用户点击后启动APP。
<br/><br/>
你需要集成各厂商的服务端sdk，维护长连接。步骤：
1. 注册获取token
2. 服务器端保存token
3. 如果长连接中断通过服务端sdk推送通知消息

# 安装
在工程 pubspec.yaml 中加入 

        dependencies:
            push_message_register: 0.0.2

# 使用

        _pushMessageRegister.onReceiveMessage().listen((event) {
            print(event);
        });
        
        //vivo在AndroidManifest.xml里配置配置
        Map apikey = {"XIAOMI_APP_ID": "小米appid", "XIAOMI_APP_KEY": "小米appkey",
                "HUAWEI_APP_ID": "华为appid", "HUAWEI_APP_KEY": "", "OPPO_APP_KEY": "oppo appkey",
                "OPPO_APP_SECRET": "oppo appsecret", "MEIZU_APP_ID": "魅族appid",
                "MEIZU_APP_KEY": "魅族appkey"};
      

# 配置

华为：需要配置xml和agconnect-services.json [HMS Core SDK](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/android-integrating-sdk-0000001050040084)
<br />
小米：需要配置xml [小米 Android SDK](https://dev.mi.com/console/doc/detail?pId=41)
<br />
ViVo: 需要配置xml [Vivo Android SDK](https://dev.vivo.com.cn/documentCenter/doc/365)
<br />
Oppo: 需要配置xml [oppo Android SDK](https://open.oppomobile.com/wiki/doc/#id=11050)
<br />
魅族: 需要配置xml [meizu_Android SDK](http://open-wiki.flyme.cn/doc-wiki/index?title=%E9%AD%85%E6%97%8F%E6%8E%A8%E9%80%81%E5%B9%B3%E5%8F%B0%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A3#id?129)

# IOS和FCM推送导航
ios推送 使用flutter_apns
<br />
fcm推送 使用firebase_messaging
