import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:push_message_register/push_message_register.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  PushMessageRegister _pushMessageRegister = PushMessageRegister();
  String _brand = "other";

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    _pushMessageRegister.destroy();
  }

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      _pushMessageRegister.onReceiveMessage().listen((event) {
        print(event);
      });
      //vivo配置在AndroidManifest.xml
      Map apikey = {"XIAOMI_APP_ID": "xiaomi appid", "XIAOMI_APP_KEY": "xiaomi appkey",
        "HUAWEI_APP_ID": "huawei appid", "HUAWEI_APP_KEY": "", "OPPO_APP_KEY": "oppo appkey",
        "OPPO_APP_SECRET": "oppo appsecret", "MEIZU_APP_ID": "meizu appid",
        "MEIZU_APP_KEY": "meizu appkey"
      };
      _brand = await PushMessageRegister.registerApi(apikey);

      if(_brand == "other"){

        //非以上品牌手机使用fcm或apns
      }
    } on PlatformException {
      _brand = 'Failed ';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_brand\n'),
        ),
      ),
    );
  }
}
