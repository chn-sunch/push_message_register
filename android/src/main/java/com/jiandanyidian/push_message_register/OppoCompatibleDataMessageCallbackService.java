package com.jiandanyidian.push_message_register;

import android.content.Context;

import com.heytap.msp.push.mode.DataMessage;
import com.heytap.msp.push.service.CompatibleDataMessageCallbackService;


/**
 * <p>Title:${Title} </p>
 * <p>Description: PushMessageService</p>
 * <p>Copyright (c) 2016 www.oppo.com Inc. All rights reserved.</p>
 * <p>Company: OPPO</p>
 *
 * @author QuWanxin
 * @version 1.0
 * @date 2017/7/28
 */

/**
 * 如果应用需要解析和处理Push消息（如透传消息），则继承PushService来处理，并在Manifest文件中申明Service
 * 如果不需要处理Push消息，则不需要继承PushService，直接在Manifest文件申明PushService即可
 */
public class OppoCompatibleDataMessageCallbackService extends CompatibleDataMessageCallbackService {
    /**
     * 透传消息处理，应用可以打开页面或者执行命令,如果应用不需要处理透传消息，则不需要重写此方法
     *
     * @param context
     * @param message
     */
    @Override
    public void processMessage(Context context, DataMessage message) {
        super.processMessage(context.getApplicationContext(), message);
//        String content = message.getContent();
//        TestModeUtil.addLogString(PushMessageService.class.getSimpleName(), "Receive SptDataMessage:" + message.toString());
//        MessageDispatcher.dispatch(context, content);//统一处理
//        LogUtil.d("processMessage  message" +message.toString());
//        NotificationUtil.showNotification(context,message.getTitle(),message.getContent(),message.getNotifyID(), ConfigManager.getInstant().isRedBadge());
    }
}
