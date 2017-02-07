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
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
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
	 * 字符串的日期格式的计算
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
	 * 获取设备IMEI码
	 */
	public static String getImei(Context context) {
		String mImei = "NULL";
		try {
			mImei = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} catch (Exception e) {
			System.out.println("获取IMEI码失败");
			mImei = "NULL";
		}
		return mImei;
	}

	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
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
	 * 检查网络环境
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

	// 计算时间差（第多少天）
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
	 * 修改状态1显示0不显示
	 */
	public static void change_drop_tip(Context context) {
		StatusImpl si = new StatusImpl(context);
		Status s_st = si.search("dropTip");
		Status st = new Status();
		// 判断是否存在记录
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
		// 判断是否存在记录
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
