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
	 * ��ӡ���Ա��
	 */
	public String TAG = "BETTER-Location";

	private static final String SERVICE_NAME = "com.baidu.trace.LBSTraceService";

	/**
	 * �켣����
	 */
	public static Trace trace = null;

	/**
	 * �켣����ͻ���
	 */
	public static LBSTraceClient client = null;

	/**
	 * entity��ʶ
	 */
	public static String entityName = null;

	/**
	 * ӥ�۷���ID�������ߴ�����ӥ�۷����Ӧ�ķ���ID
	 */
	public static long serviceId = 119354;

	/**
	 * �켣�������ͣ�0 : ������socket�����ӣ� 1 : ����socket�����ӵ����ϴ�λ�����ݣ�2 : ����socket�����Ӳ��ϴ�λ�����ݣ�
	 */
	public static int traceType = 2;

	/**
	 * λ�òɼ�����
	 */
	public static int gatherInterval = 5;
	/**
	 * �������
	 */
	public static int packInterval = 30;
	/**
	 * ����Э�����ͣ�0Ϊhttp��1Ϊhttps
	 */
	public static int protocoType = 0;


	/**
	 * �����켣���������
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
		client.startTrace(trace, startTraceListener); // �����켣����
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "LocationServiceλ�÷����ѿ���");
		dropTip();// ��ʾ�����˵�
		monitor();
		flags = START_STICKY;// service��kill�����Զ���д����
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * ������ʾ
	 */
	@SuppressWarnings("deprecation")
	public void dropTip() {
		Notification notification = new Notification(R.drawable.icon_logo_nobg,
				getString(R.string.app_name), System.currentTimeMillis());

		Intent notificationIntent = new Intent(this, CountActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, "love you forever", "������һ��ĵ�"
				+ UtilManager.countDate() + "��", pendingIntent);
		int signal = 0;
		if (UtilManager.search_drop_tip(getApplicationContext())) {
			signal = 77;
		}
		startForeground(signal, notification);// ��һ��ֵΪ0����ʾ��serviceǰ����ʾ��������ȼ�
	}

	/**
	 * ������۷����Ƿ�����
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
						Log.i(TAG, "ӥ�۹켣������ֹͣ�������켣����");
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
						Log.i(TAG, "ӥ�۹켣������������");
					}
				}
			}

		}.start();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "LocationServiceλ�÷����ѹر�");
		stopForeground(true);// ����ǰ̨����
		Intent intent = new Intent("com.linteng.service.LocationSrvice.destroy");
		sendBroadcast(intent);
		super.onDestroy();
	}

	/**
	 * ������ʼ��
	 */
	private void init() {
		entityName = UtilManager.getImei(getApplicationContext());
		Log.i(TAG, "��ʶ��-->" + UtilManager.getImei(getApplicationContext()));
		// ʵ�����켣����ͻ���
		client = new LBSTraceClient(getApplicationContext());

		// ʵ�����켣����
		trace = new Trace(getApplicationContext(), serviceId, entityName,
				traceType);
		// ����λ�òɼ��ʹ������
		client.setInterval(gatherInterval, packInterval);
		// ����Э��
		client.setProtocolType(protocoType);
		// ���ö�λģʽ
		client.setLocationMode(LocationMode.High_Accuracy);
	}

	/**
	 * ׷�ٳ�ʼ��
	 */
	private void initOnStartTraceListener() {

		// ʵ���������켣����ص��ӿ�
		startTraceListener = new OnStartTraceListener() {
			// �����켣����ص��ӿڣ�arg0 : ��Ϣ���룬arg1 : ��Ϣ���ݣ�����鿴��ο���
			@Override
			public void onTraceCallback(int arg0, String arg1) {
				Log.i(TAG, "onTraceCallback=" + arg1 + "-----" + arg0);

			}

			// �켣�������ͽӿڣ����ڽ��շ����������Ϣ��arg0 : ��Ϣ���ͣ�arg1 : ��Ϣ���ݣ�����鿴��ο���
			@Override
			public void onTracePushCallback(byte arg0, String arg1) {
				Log.i(TAG, "onTracePushCallback=" + arg1 + "-----" + arg0);
			}
		};

	}
}
