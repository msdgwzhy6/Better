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

		Log.i(TAG, "�յ��㲥" + "--->" + intent.getAction());

		String name = intent.getAction();

		// ӥ�۷���ɱ��,�㲥����
		if (name.equals("com.linteng.service.LocationSrvice.destroy")) {
			Log.i(TAG, "Location����ɱ���Ĺ㲥--->");
			restartLocationService(context);
		}

		// ���绷���仯
		if (name.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			Log.i(TAG, "���绷���仯�㲥--->");
			restartLocationService(context);
		}
		// ��Ļ�����㲥
		if (name.equals("android.intent.action.USER_PRESENT")) {
			Log.i(TAG, "��Ļ�򿪹㲥--->");
			restartLocationService(context);
		}

		// �����㲥
		if (name.equals("android.intent.action.BOOT_COMPLETED")) {
			Log.i(TAG, "�����㲥--->");
			restartLocationService(context);
		}

		// �ػ��㲥
		if (name.equals("android.intent.action.ACTION_SHUTDOWN")) {
			Log.i(TAG, "�ػ��㲥--->");
			ActivityManager.getInstance().exit();
		}
		// SD���ع㲥
		if (name.equals("android.intent.action.MEDIA_MOUNTED")) {
			Log.i(TAG, "SD���ع㲥--->");
			restartLocationService(context);
		}
	}

	// ����ӥ��λ�÷���
	private void restartLocationService(Context context) {
		Boolean isrun = UtilManager.isServiceWork(context,
				"com.linteng.service.LocationService");
		if (!isrun) {
			Intent lsIntent = new Intent(context, LocationService.class);
			context.startService(lsIntent);
			Log.i(TAG, "Location���������ɹ㲥����...");
		} else {
			Log.i(TAG, "Location�������������У���������");
		}
	}
}