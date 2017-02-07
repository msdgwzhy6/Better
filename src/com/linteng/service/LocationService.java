package com.linteng.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.Trace;
import com.linteng.activity.CountActivity;
import com.linteng.better.R;
import com.linteng.tools.UtilManager;

public class LocationService extends Service {

	/**
	 * 打印测试标记
	 */
	public String TAG = "BETTER-Location";

	private static final String SERVICE_NAME = "com.baidu.trace.LBSTraceService";

	/**
	 * 轨迹服务
	 */
	public static Trace trace = null;

	/**
	 * 轨迹服务客户端
	 */
	public static LBSTraceClient client = null;

	/**
	 * entity标识
	 */
	public static String entityName = null;

	/**
	 * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
	 */
	public static long serviceId = 119354;

	/**
	 * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
	 */
	public static int traceType = 2;

	/**
	 * 位置采集周期
	 */
	public static int gatherInterval = 5;
	/**
	 * 打包周期
	 */
	public static int packInterval = 30;
	/**
	 * 设置协议类型，0为http，1为https
	 */
	public static int protocoType = 0;


	/**
	 * 开启轨迹服务监听器
	 */
	private OnStartTraceListener startTraceListener = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		// SDKInitializer.initialize(getApplicationContext());
		init();
		initOnStartTraceListener();
		client.startTrace(trace, startTraceListener); // 开启轨迹服务
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "LocationService位置服务已开启");
		dropTip();// 显示下拉菜单
		monitor();
		flags = START_STICKY;// service被kill掉后自动重写创建
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 下拉显示
	 */
	@SuppressWarnings("deprecation")
	public void dropTip() {
		Notification notification = new Notification(R.drawable.icon_logo_nobg,
				getString(R.string.app_name), System.currentTimeMillis());

		Intent notificationIntent = new Intent(this, CountActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, "love you forever", "我们在一起的第"
				+ UtilManager.countDate() + "天", pendingIntent);
		int signal = 0;
		if (UtilManager.search_drop_tip(getApplicationContext())) {
			signal = 77;
		}
		startForeground(signal, notification);// 第一个值为0不显示，service前置显示，提高优先级
	}

	/**
	 * 监控用眼服务是否运行
	 */
	public void monitor() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(30 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Log.i(TAG, "thread sleep failed");
					}

					if (!UtilManager.isServiceWork(getApplicationContext(),
							SERVICE_NAME)) {
						Log.i(TAG, "鹰眼轨迹服务已停止，重启轨迹服务");
						if (null != client && null != trace) {
							client.startTrace(trace);
						} else {
							client = null;
							client = new LBSTraceClient(getApplicationContext());
							entityName = UtilManager
									.getImei(getApplicationContext());
							trace = new Trace(getApplicationContext(),
									serviceId, entityName);
							client.startTrace(trace);
						}

					} else {
						Log.i(TAG, "鹰眼轨迹服务正在运行");
					}
				}
			}

		}.start();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "LocationService位置服务已关闭");
		stopForeground(true);// 程序前台运行
		Intent intent = new Intent("com.linteng.service.LocationSrvice.destroy");
		sendBroadcast(intent);
		super.onDestroy();
	}

	/**
	 * 参数初始化
	 */
	private void init() {
		entityName = UtilManager.getImei(getApplicationContext());
		Log.i(TAG, "标识码-->" + UtilManager.getImei(getApplicationContext()));
		// 实例化轨迹服务客户端
		client = new LBSTraceClient(getApplicationContext());

		// 实例化轨迹服务
		trace = new Trace(getApplicationContext(), serviceId, entityName,
				traceType);
		// 设置位置采集和打包周期
		client.setInterval(gatherInterval, packInterval);
		// 设置协议
		client.setProtocolType(protocoType);
		// 设置定位模式
		client.setLocationMode(LocationMode.High_Accuracy);
	}

	/**
	 * 追踪初始化
	 */
	private void initOnStartTraceListener() {

		// 实例化开启轨迹服务回调接口
		startTraceListener = new OnStartTraceListener() {
			// 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTraceCallback(int arg0, String arg1) {
				Log.i(TAG, "onTraceCallback=" + arg1 + "-----" + arg0);

			}

			// 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTracePushCallback(byte arg0, String arg1) {
				Log.i(TAG, "onTracePushCallback=" + arg1 + "-----" + arg0);
			}
		};

	}
}
