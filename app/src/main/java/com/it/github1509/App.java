package com.it.github1509;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;


/**
 * Created by kkguo on 2015/11/13.
 */
public class App extends Application {
    private static App apps;

    @Override
    public void onCreate() {
        super.onCreate();
        apps = this;

        //初始化百度地图
        initBaiduMap();
    }
    /**
     * 初始化百度地图
     */
    private void initBaiduMap(){
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        //动态SDK注册
        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        registerReceiver(receiver, filter);
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)){
                Toast.makeText(getApplicationContext(), "网络出错", Toast.LENGTH_LONG).show();
            }else if(action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)){
//                Toast.makeText(getApplicationContext(), "key 验证出错!", Toast.LENGTH_LONG).show();
            }
        }
    }

}
