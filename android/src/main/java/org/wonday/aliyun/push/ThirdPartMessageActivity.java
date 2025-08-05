/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.wonday.aliyun.push;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;

import java.util.Map;

public class ThirdPartMessageActivity extends AndroidPopupActivity {

    public static Class<?> mainClass;
    public static ReactApplicationContext context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 实现通知打开回调方法，获取通知相关信息
     * @param title     标题
     * @param summary   内容
     * @param extMap    额外参数
     */
    @Override
    protected void onSysNoticeOpened(String title, String summary, Map<String, String> extMap) {
      try {
        if (AliyunPushMessageReceiver.instance!=null) {
          AliyunPushMessageReceiver.instance.onNotification(context, title, summary, extMap);
        }
        if (ThirdPartMessageActivity.mainClass!=null) {
          Intent itent=new Intent();
          itent.setClass(ThirdPartMessageActivity.this, mainClass);
          startActivity(itent);
          ThirdPartMessageActivity.this.finish();

          Gson gson = new Gson();
          String extraStr =  gson.toJson(extMap);
          WritableMap params = Arguments.createMap();
          params.putString("body", summary);
          params.putString("title", title);
          params.putString("extraStr", extraStr);

          params.putString("type", AliyunPushMessageReceiver.ALIYUN_PUSH_TYPE_NOTIFICATION);
          params.putString("actionIdentifier", "opened");
          if (context == null) {
            params.putString("appState", "background");
            AliyunPushMessageReceiver.initialMessage = params;
            FLog.d(ReactConstants.TAG, "reactContext==null");
          }else{
            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
              .emit("aliyunPushReceived", params);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
