package com.linteng.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.linteng.service.LocationService;
import com.linteng.tools.ActivityManager;
import com.linteng.tools.UtilManager;

public class BootReceiver extends BroadcastReceiver {

	public String TAG = "BETTER-BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "收到广播" + "--->" + intent.getAction());

		String name = intent.getAction();

		// 鹰眼服务被杀死,广播重启
		if (name.equals("com.linteng.service.LocationSrvice.destroy")) {
			Log.i(TAG, "Location服务被杀死的广播--->");
			restartLocationService(context);
		}

		// 网络环境变化
		if (name.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			Log.i(TAG, "网络环境变化广播--->");
			restartLocationService(context);
		}
		// 屏幕解锁广播
		if (name.equals("android.intent.action.USER_PRESENT")) {
			Log.i(TAG, "屏幕打开广播--->");
			restartLocationService(context);
		}

		// 开机广播
		if (name.equals("android.intent.action.BOOT_COMPLETED")) {
			Log.i(TAG, "开机广播--->");
			restartLocationService(context);
		}

		// 关机广播
		if (name.equals("android.intent.action.ACTION_SHUTDOWN")) {
			Log.i(TAG, "关机广播--->");
			ActivityManager.getInstance().exit();
		}
		// SD挂载广播
		if (name.equals("android.intent.action.MEDIA_MOUNTED")) {
			Log.i(TAG, "SD挂载广播--->");
			restartLocationService(context);
		}
	}

	// 重启鹰眼位置服务
	private void restartLocationService(Context context) {
		Boolean isrun = UtilManager.isServiceWork(context,
				"com.linteng.service.LocationService");
		if (!isrun) {
			Intent lsIntent = new Intent(context, LocationService.class);
			context.startService(lsIntent);
			Log.i(TAG, "Location服务正在由广播启动...");
		} else {
			Log.i(TAG, "Location服务正在运行中，无需启动");
		}
	}
}