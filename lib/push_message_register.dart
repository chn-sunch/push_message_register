
import 'dart:async';

import 'package:flutter/services.dart';

class PushMessageRegister {
  static const MethodChannel _channel = const MethodChannel('push_message_register');
  static const EventChannel _eventChannel = const EventChannel('push_message_register_stream');

  static Stream<Map<String, Object>> _onReceiveMessage = _eventChannel
      .receiveBroadcastStream()
      .map<Map<String, Object>>((element) => element.cast<String, Object>());

  StreamSubscription<Map<String, Object>>? _subscription;
  StreamController<Map<String, Object>>? _receiveStream;


  static Future<String> registerApi(Map apiKeyMap)  async {
    //vivo的appid配置在androidManifest.xml里面
    return await _channel.invokeMethod('REGISTER', apiKeyMap);
  }

  static Future<void> testApi(Map apiKeyMap)  async {
    return await _channel.invokeMethod('TEST', apiKeyMap);
  }

  Stream<Map<String, Object>> onReceiveMessage() {
    if (_receiveStream == null) {
      _receiveStream = StreamController();
      _subscription = _onReceiveMessage.listen((Map<String, Object> event) {
        if (event != null) {
          Map<String, Object> newEvent = Map<String, Object>.of(event);
          _receiveStream?.add(newEvent);
        }
      });
    }
    return _receiveStream!.stream;
  }
}
