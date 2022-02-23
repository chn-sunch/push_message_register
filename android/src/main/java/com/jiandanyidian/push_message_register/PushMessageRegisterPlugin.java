package com.jiandanyidian.push_message_register;



import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.util.VivoPushException;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** PushMessageCnPlugin */
public  class PushMessageRegisterPlugin implements FlutterPlugin, MethodCallHandler,EventChannel.StreamHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private EventChannel eventChannel;
    public static EventChannel.EventSink mEventSink = null;

    private Context mContext = null;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        if (null == mContext) {
            mContext = flutterPluginBinding.getApplicationContext();
            //方法调用通道
            channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "push_message_register");
            channel.setMethodCallHandler(this);

            eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "push_message_register_stream");
            eventChannel.setStreamHandler((EventChannel.StreamHandler) this);
            //回调监听通道
        }
    }

    @Override
    public void onMethodCall( MethodCall call, Result result) {
        String brand = Build.BRAND;

        if (call.method.equals("REGISTER")) {
            if(RomUtil.isMiui() && shouldInit()){
                registerXiaoMiApiKey((Map) call.arguments);
                brand = "xiaomi";
            }

            if(RomUtil.isEmui()){
                registerHuaWeiApiKey((Map) call.arguments);
                brand = "huawei";
            }

            if(RomUtil.isVivo()){
                registerViVoApiKey();
                brand = "vivo";
            }

            if(RomUtil.isOppo()){
                registerOppoApiKey((Map) call.arguments);
                brand = "oppo";
            }

            if(RomUtil.isFlyme()){
                registerMeizuApiKey((Map) call.arguments);
                brand = "meizu";
            }
            result.success(brand);
        }

        if(call.method.equals("TEST")){
            registerHuaWeiApiKey((Map) call.arguments);
            result.success(brand);
        }
//        else {
//            result.notImplemented();
//        }
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    //注册小米推送
    private void registerXiaoMiApiKey(Map apiKeyMap) {
        if (null != apiKeyMap) {
            MiPushClient.registerPush(this.mContext, (String) apiKeyMap.get("XIAOMI_APP_ID"),
                    (String) apiKeyMap.get("XIAOMI_APP_KEY"));
        }
    }

    //注册华为推送
    private void registerHuaWeiApiKey(Map apiKeyMap) {
        if (null != apiKeyMap) {
            //华为注册不能放在主线程中
            new Thread() {
                @Override
                public void run() {
                    Map<String, Object> result = new HashMap<String, Object>();
                    try {
                        String appId = (String) apiKeyMap.get("HUAWEI_APP_ID");
                        String token = HmsInstanceId.getInstance(mContext).getToken(appId, "HCM");

                        if (!TextUtils.isEmpty(token)) {
                            result.put("result", "success");
                            result.put("brand", "huawei");
                            result.put("token", token);
                            MainThreadUtil.runMainThread(
                                    ()-> PushMessageRegisterPlugin.mEventSink.success(result)
                            );
                        }
                    }
                    catch (ApiException e) {
                        result.put("result","fail");
                        result.put("msg", e.getMessage());
                        MainThreadUtil.runMainThread(
                                ()-> PushMessageRegisterPlugin.mEventSink.success(result)
                        );
                    }
                }
            }.start();
        }
    }

    //注册vivo推送
    private void registerViVoApiKey(){
        try {
            PushClient.getInstance(mContext).initialize();
        } catch (VivoPushException e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<String, Object>();

            result.put("result","fail");
            result.put("msg", e.getMessage());
            mEventSink.success(result);
        }

        // 打开push开关, 关闭为turnOffPush，详见api接入文档
        PushClient.getInstance(mContext).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                // TODO: 开关状态处理， 0代表成功
                Map<String, Object> result = new HashMap<String, Object>();
                if (state == 0) {
                    String regId = PushClient.getInstance(mContext).getRegId();
                    result.put("result","success");
                    result.put("brand", "vivo");
                    result.put("token", regId);
                    MainThreadUtil.runMainThread(
                            ()-> PushMessageRegisterPlugin.mEventSink.success(result)
                    );
                }
                else{
                    result.put("result","fail");
                    result.put("msg", "vivo turnoffpush error");
                    MainThreadUtil.runMainThread(
                            ()-> PushMessageRegisterPlugin.mEventSink.success(result)
                    );
                }
            }
        });
    }

    //注册oppo推送
    private void registerOppoApiKey(Map apiKeyMap){
        String appKey = (String) apiKeyMap.get("OPPO_APP_KEY");
        String appSecret = (String) apiKeyMap.get("OPPO_APP_SECRET");


        HeytapPushManager.register(mContext, appKey, appSecret, mPushCallback);
        HeytapPushManager.requestNotificationPermission();
    }

    //注册meizu推送
    private void registerMeizuApiKey(Map apiKeyMap){
        String appId = (String) apiKeyMap.get("MEIZU_APP_ID");
        String appKey = (String) apiKeyMap.get("MEIZU_APP_KEY");
        com.meizu.cloud.pushsdk.PushManager.register(mContext, appId, appKey);
    }


    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        mEventSink = eventSink;
    }

    @Override
    public void onCancel(Object o) {

    }

    private ICallBackResultService mPushCallback = new ICallBackResultService() {
        Map<String, Object> result = new HashMap<String, Object>();

        @Override
        public void onRegister(int code, String s) {
            if (code == 0) {
                result.put("result","success");
                result.put("brand","oppo");
                result.put("token", s);
            } else {
                result.put("result","fail");
                if(s != null) {
                    result.put("msg", s);
                }
                else{
                    result.put("msg", String.valueOf(code));

                }
            }
            mEventSink.success(result);
        }

        @Override
        public void onUnRegister(int code) {
            if (code == 0) {
                //showResult("注销成功", "code=" + code);
            } else {
                //showResult("注销失败", "code=" + code);
            }
        }

        @Override
        public void onGetPushStatus(final int code, int status) {
            if (code == 0 && status == 0) {
                //showResult("Push状态正常", "code=" + code + ",status=" + status);
            } else {
                //showResult("Push状态错误", "code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onGetNotificationStatus(final int code, final int status) {
            if (code == 0 && status == 0) {
                //showResult("通知状态正常", "code=" + code + ",status=" + status);
            } else {
                //showResult("通知状态错误", "code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onError(int i, String s) {
            //showResult("onError", "onError code : " + i + "   message : " + s);
        }

        @Override
        public void onSetPushTime(final int code, final String s) {
            //showResult("SetPushTime", "code=" + code + ",result:" + s);
        }

    };


    //因为推送服务XMPushService在AndroidManifest.xml中设置为运行在另外一个进程，
    //这导致本Application会被实例化两次，所以我们需要让应用的主进程初始化
    private boolean shouldInit() {
        //通过ActivityManager我们可以获得系统里正在运行的activities
        //包括进程(Process)等、应用程序/包、服务(Service)、任务(Task)信息。
        ActivityManager am = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            processInfos = am.getRunningAppProcesses();
        }
        String mainProcessName = mContext.getApplicationInfo().processName;

        int myPid =  android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            //通过比较进程的唯一标识和包名判断进程里是否存在该App
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
