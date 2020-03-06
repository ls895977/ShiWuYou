package com.lykj.aextreme.afinal.common;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.lykj.aextreme.R;
import com.lykj.aextreme.afinal.utils.Constant;
import com.lykj.aextreme.afinal.utils.Debug;
import com.lykj.aextreme.afinal.utils.MyShare;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseApplication extends Application {
    /**
     * Activity栈
     */
    private List<Activity> acts;
    /**
     * 全局数据
     */
    private HashMap<String, Object> hashMap;
    private IntentFilter filter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Debug.e(getClass(), "action:" + action);
            if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                int result = -1;
//                NetworkInfo netInfo = manager.getActiveNetworkInfo();// 当前网络信息
//                if (netInfo != null && netInfo.isConnected()) {
//                    result = netInfo.getType();
//                }
                Debug.e(getClass(), "NetState:" + result);
                new MyShare(getApplicationContext()).putInt(Constant.NET_STATUS, result);
                sendBroadcast(new Intent(Constant.ACTION_RECEIVE_NET_CHANGE));
            }
        }
    };
    public Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Debug.e(getClass(), "Application start!");
        acts = new CopyOnWriteArrayList<>();
        hashMap = new HashMap<>();
        initNetListener();
      //  initPermissify();
        context = this;
    }

    public Context getContext() {
        return context;
    }

    ;

    /**
     * 初始化网络监听
     */
    private void initNetListener() {
        filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
        registerReceiver(receiver, filter);
        receiver.onReceive(this, new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
    }
/*

    private void initPermissify() {

        permissifyConfig = new PermissifyConfig.Builder()
                .withDialogRationaleDialogFactory(new PermissifyConfig.AlertDialogFactory() {
                    @Override
                    public AlertDialog createDialog(Context context, String dialogMsg, DialogInterface.OnClickListener onClickListener) {
                        return new AlertDialog.Builder(context)
                                .setTitle(R.string.default_rationale_dialog_text)
                                .setMessage(dialogMsg)
                                .setPositiveButton(android.R.string.ok, onClickListener)
                                .create();
                    }
                })
                .withDefaultTextForPermissions(new HashMap<String, DialogText>() {{
                    put(Manifest.permission_group.LOCATION, new DialogText(R.string.location_rationale, R.string.location_deny_dialog));
                    put(Manifest.permission_group.CAMERA, new DialogText(R.string.camera_rationale, R.string.camera_deny_dialog));
                    put(Manifest.permission_group.STORAGE, new DialogText(R.string.storage_rationale, R.string.storage_deny_dialog));
                    put(Manifest.permission_group.MICROPHONE, new DialogText(R.string.mic_rationale, R.string.mic_deny_dialog));
                    put(Manifest.permission_group.PHONE, new DialogText(R.string.phone_rationale, R.string.phone_deny_dialog));
                    put(Manifest.permission_group.CONTACTS, new DialogText(R.string.contacts_rationale, R.string.contacts_deny_dialog));
                }})
                .build();

        PermissifyConfig.initDefault(permissifyConfig);
    }
 */

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (receiver != null)
            unregisterReceiver(receiver);
        Debug.e(getClass(), "Application closed!");
    }

    /**
     * 添加Activity到容器中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        //防止重复添加
        remove(activity);
        acts.add(activity);
    }

    /**
     * 清理activity栈 并退出
     */
    public void exit() {
        clear();
        System.exit(0);
    }

    /**
     * 清理activity栈
     */
    public void remove(Activity activity) {
        if (acts != null && !acts.isEmpty()) {
            acts.remove(activity);
        }
    }

    /**
     * 清理activity栈
     */
    public void removeFinish(Class cls) {
        if (acts != null && !acts.isEmpty()) {
            Activity act = null;
            for (Activity item : acts) {
                if (TextUtils.equals(item.getClass().getName(), cls.getName())) {
                    act = item;
                    break;
                }
            }
            if (act != null) {
                act.finish();
                acts.remove(act);
            }
        }
    }

    /**
     * 清理activity栈
     */
    public void clear() {
        if (acts != null && !acts.isEmpty()) {
            for (Activity activity : acts) {
                activity.finish();
            }
            acts.clear();
        }
    }

    /**
     * 存数据
     *
     * @param key
     * @param value
     */
    public void putValue(String key, Object value) {
        if (hashMap == null) {
            hashMap = new HashMap<String, Object>();
        }
        hashMap.put(key, value);
    }

    /**
     * 取数据
     *
     * @param key
     * @return
     */
    public Object getValue(String key) {
        if (hashMap != null) {
            return hashMap.get(key);
        }
        return null;
    }

}
