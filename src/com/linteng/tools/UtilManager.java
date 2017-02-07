package com.linteng.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.linteng.bean.Status;
import com.linteng.db.StatusImpl;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.telephony.TelephonyManager;

public class UtilManager {
	/**
	 * ������������֮����������
	 * 
	 * @param smdate
	 *            ��С��ʱ��
	 * @param bdate
	 *            �ϴ��ʱ��
	 * @return �������
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate)
			throws ParseException, java.text.ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * �ַ��������ڸ�ʽ�ļ���
	 * 
	 * @throws java.text.ParseException
	 */
	public static int daysBetween(String smdate, String bdate)
			throws ParseException, java.text.ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * ��ȡ�豸IMEI��
	 */
	public static String getImei(Context context) {
		String mImei = "NULL";
		try {
			mImei = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} catch (Exception e) {
			System.out.println("��ȡIMEI��ʧ��");
			mImei = "NULL";
		}
		return mImei;
	}

	/**
	 * �ж�ĳ�������Ƿ��������еķ���
	 * 
	 * @param mContext
	 * @param serviceName
	 *            �ǰ���+��������������磺net.loonggg.testbackstage.TestService��
	 * @return true�����������У�false�������û����������
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}


	/**
	 * ������绷��
	 */
	public static Boolean checkNet(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo gprs = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!gprs.isConnected() && !wifi.isConnected()) {
			return false;
		} else {
			return true;
		}
	}

	// ����ʱ���ڶ����죩
	@SuppressLint("SimpleDateFormat")
	public static int countDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = null;
		int data = 0;
		try {
			start = sdf.parse("2016-01-08 00:00:00");
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			data = daysBetween(start, new Date()) + 1;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	/**
	 * �޸�״̬1��ʾ0����ʾ
	 */
	public static void change_drop_tip(Context context) {
		StatusImpl si = new StatusImpl(context);
		Status s_st = si.search("dropTip");
		Status st = new Status();
		// �ж��Ƿ���ڼ�¼
		if (s_st.getId() > 0) {

			if (s_st.getStatus() > 0) {
				st.setStatus(0);
			} else {
				st.setStatus(1);
			}
			st.setName("dropTip");
			si.change(st);
		}
	}

	public static Boolean search_drop_tip(Context context) {
		StatusImpl si = new StatusImpl(context);
		Status s_st = si.search("dropTip");
		Status st = new Status();
		// �ж��Ƿ���ڼ�¼
		if (s_st.getId() > 0) {
			if (s_st.getStatus() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			st.setName("dropTip");
			st.setStatus(1);
			si.insert(st);
			return true;
		}
	}

}
