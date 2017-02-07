package com.linteng.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.linteng.bean.Status;
import com.linteng.better.R;
import com.linteng.db.StatusImpl;
import com.linteng.view.CusProDialog;

public class UpdateManager {

	/* TAG */
	private String TAG = "BETTER-UpdateManager";

	// json 需要包含 version name url note

	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 检查结束需要更新 */
	private static final int CHECK_FINISH_NEED_UPDATE = 3;
	/* 检查结束不需要更新 */
	private static final int CHECK_FINISH_NOT_NEED_UPDATE = 4;
	/* 正在检查更新 */
	private static final int IS_CHECKING = 5;
	/* 网络未连接 */
	private static final int ERROR_NET = 6;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
	/* 模式 0.静默检查，只弹出更新框 1.主动检查，正在检查更新，网络是否有问题，有新版本提示，无新版本吐司 */
	private int model;
	/* 上下文 */
	private Context mContext;
	/* URL */
	private String updateUrl = "http://www.linteng.wang/app/version.php";
	/* 更新进度条 */
	private ProgressBar mProgress;
	/* 下载提示框 */
	private Dialog mDownloadDialog;
	/* 新版本提示框 */
	private Dialog mShowDialog;
	/* 检查提示框 */
	private Dialog mCheckDialog;
	/* volley列队 */
	private RequestQueue requestQueue;

	public UpdateManager(Context context, int model) {
		this.mContext = context;
		this.model = model;
		this.requestQueue = Volley.newRequestQueue(mContext);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在检查
			case IS_CHECKING:
				mCheckDialog = CusProDialog.createCustomerProgressDialog(
						mContext, "正在检查更新...");

				if (model > 0) {
					mCheckDialog.show();
				}
				break;
			// 网络问题
			case ERROR_NET:
				if (model > 0) {
					mCheckDialog.dismiss();
					Toast.makeText(mContext, "网络连接异常...", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			// 显示提示对话框
			case CHECK_FINISH_NEED_UPDATE:
				if (mCheckDialog.isShowing()) {
					mCheckDialog.dismiss();
				}
				showNoticeDialog();
				break;
			case CHECK_FINISH_NOT_NEED_UPDATE:
				if (model > 0) {
					if (mCheckDialog.isShowing()) {
						mCheckDialog.dismiss();
					}
					Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_LONG)
							.show();
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		new checkUpdateThread().start();

	}

	/**
	 * 取消更新
	 */
	public void cancleUpdate() {
		if (model > 0) {
			mCheckDialog.dismiss();
			requestQueue.cancelAll(11);
		}
	}

	/**
	 * 获取服务器版本信息
	 */

	private class checkUpdateThread extends Thread {

		@Override
		public void run() {
			// 正在检查更新弹出框
			mHandler.sendEmptyMessage(IS_CHECKING);
			// 判断网络环境
			if (!UtilManager.checkNet(mContext)) {
				mHandler.sendEmptyMessage(ERROR_NET);
			}
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
					Request.Method.GET, updateUrl, null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							Log.i(TAG, "json data===" + response);

							try {
								mHashMap = JSONObject2Map(response);
								if (null != mHashMap) {
									int serviceCode = Integer.valueOf(mHashMap
											.get("version"));
									// 获取当前软件版本
									int versionCode = getVersionCode(mContext);

									// 版本判断
									if (serviceCode > versionCode) {
										// 需要更新发送消息
										mHandler.sendEmptyMessage(CHECK_FINISH_NEED_UPDATE);
										Log.i(TAG, "已发送更新通知");
									} else {
										// 不需要更新
										mHandler.sendEmptyMessage(CHECK_FINISH_NOT_NEED_UPDATE);
										Log.i(TAG, "已发送不更新通知");
									}
								} else {
									Log.i(TAG, "服务器未正常返回数据，数据为空！！！");
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i(TAG, "Volley 通信发生错误");
							error.printStackTrace();
						}
					});
			jsonObjectRequest.setTag(11);
			requestQueue.add(jsonObjectRequest);
		}
	}

	/**
	 * JsonObject转HashMap
	 * 
	 * @throws JSONException
	 */
	private HashMap<String, String> JSONObject2Map(JSONObject jsonObject)
			throws JSONException {

		Iterator<String> keyIter = jsonObject.keys();
		String key;
		String value;
		HashMap<String, String> data = new HashMap<String, String>();
		while (keyIter.hasNext()) {
			key = keyIter.next();
			value = jsonObject.get(key).toString();
			data.put(key, value);
		}
		Log.i(TAG, "数据转换完成...");
		return data;
	}

	/**
	 * 获取软件版本号
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {

		mShowDialog = new AlertDialog.Builder(mContext).create();
		mShowDialog.show();

		// 设置窗口的内容页面
		Window window = mShowDialog.getWindow();
		window.setContentView(R.layout.dialog_update);

		Button ok = (Button) window.findViewById(R.id.ud_ok);
		Button no = (Button) window.findViewById(R.id.ud_no);
		TextView title = (TextView) window.findViewById(R.id.ud_title);
		title.setText("版本代号：" + "[" + mHashMap.get("name") + "]");

		TextView content = (TextView) window.findViewById(R.id.ud_content);
		content.setText(mHashMap.get("note"));

		// 确认事件
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mShowDialog.dismiss();
				alive_ui_update();
				showDownloadDialog();
			}
		});
		// 取消事件
		no.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (model == 0) {
					cancle_ui_update();

				}
				mShowDialog.dismiss();
			}
		});
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progressbar_update, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置取消状态
						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

	/**
	 * 保证UI界面弹出一次，用户取消后不在弹出 用户手动检查更新以后在弹出来
	 */
	public void cancle_ui_update() {
		StatusImpl si = new StatusImpl(mContext);
		Status s_st = si.search("uiupdate");
		Status st = new Status();

		// 判断是否存在记录
		if (s_st.getId() > 0) {
			st.setName("uiupdate");
			st.setStatus(1);
			si.change(st);
		} else {
			st.setName("uiupdate");
			st.setStatus(1);
			si.insert(st);
		}
	}

	/**
	 * UI弹出更新框可以弹出
	 */
	public void alive_ui_update() {
		StatusImpl si = new StatusImpl(mContext);
		Status s_st = si.search("uiupdate");
		Status st = new Status();

		// 判断是否存在记录
		if (s_st.getId() > 0) {
			st.setName("uiupdate");
			st.setStatus(0);
			si.change(st);
		}
	}
}
