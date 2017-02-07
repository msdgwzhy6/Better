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

	// json ��Ҫ���� version name url note

	/* ������ */
	private static final int DOWNLOAD = 1;
	/* ���ؽ��� */
	private static final int DOWNLOAD_FINISH = 2;
	/* ��������Ҫ���� */
	private static final int CHECK_FINISH_NEED_UPDATE = 3;
	/* ����������Ҫ���� */
	private static final int CHECK_FINISH_NOT_NEED_UPDATE = 4;
	/* ���ڼ����� */
	private static final int IS_CHECKING = 5;
	/* ����δ���� */
	private static final int ERROR_NET = 6;
	/* ���������XML��Ϣ */
	HashMap<String, String> mHashMap;
	/* ���ر���·�� */
	private String mSavePath;
	/* ��¼���������� */
	private int progress;
	/* �Ƿ�ȡ������ */
	private boolean cancelUpdate = false;
	/* ģʽ 0.��Ĭ��飬ֻ�������¿� 1.������飬���ڼ����£������Ƿ������⣬���°汾��ʾ�����°汾��˾ */
	private int model;
	/* ������ */
	private Context mContext;
	/* URL */
	private String updateUrl = "http://www.linteng.wang/app/version.php";
	/* ���½����� */
	private ProgressBar mProgress;
	/* ������ʾ�� */
	private Dialog mDownloadDialog;
	/* �°汾��ʾ�� */
	private Dialog mShowDialog;
	/* �����ʾ�� */
	private Dialog mCheckDialog;
	/* volley�ж� */
	private RequestQueue requestQueue;

	public UpdateManager(Context context, int model) {
		this.mContext = context;
		this.model = model;
		this.requestQueue = Volley.newRequestQueue(mContext);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ���ڼ��
			case IS_CHECKING:
				mCheckDialog = CusProDialog.createCustomerProgressDialog(
						mContext, "���ڼ�����...");

				if (model > 0) {
					mCheckDialog.show();
				}
				break;
			// ��������
			case ERROR_NET:
				if (model > 0) {
					mCheckDialog.dismiss();
					Toast.makeText(mContext, "���������쳣...", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// ��������
			case DOWNLOAD:
				// ���ý�����λ��
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
				installApk();
				break;
			// ��ʾ��ʾ�Ի���
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
					Toast.makeText(mContext, "�Ѿ������°汾", Toast.LENGTH_LONG)
							.show();
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * ����������
	 */
	public void checkUpdate() {
		new checkUpdateThread().start();

	}

	/**
	 * ȡ������
	 */
	public void cancleUpdate() {
		if (model > 0) {
			mCheckDialog.dismiss();
			requestQueue.cancelAll(11);
		}
	}

	/**
	 * ��ȡ�������汾��Ϣ
	 */

	private class checkUpdateThread extends Thread {

		@Override
		public void run() {
			// ���ڼ����µ�����
			mHandler.sendEmptyMessage(IS_CHECKING);
			// �ж����绷��
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
									// ��ȡ��ǰ����汾
									int versionCode = getVersionCode(mContext);

									// �汾�ж�
									if (serviceCode > versionCode) {
										// ��Ҫ���·�����Ϣ
										mHandler.sendEmptyMessage(CHECK_FINISH_NEED_UPDATE);
										Log.i(TAG, "�ѷ��͸���֪ͨ");
									} else {
										// ����Ҫ����
										mHandler.sendEmptyMessage(CHECK_FINISH_NOT_NEED_UPDATE);
										Log.i(TAG, "�ѷ��Ͳ�����֪ͨ");
									}
								} else {
									Log.i(TAG, "������δ�����������ݣ�����Ϊ�գ�����");
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i(TAG, "Volley ͨ�ŷ�������");
							error.printStackTrace();
						}
					});
			jsonObjectRequest.setTag(11);
			requestQueue.add(jsonObjectRequest);
		}
	}

	/**
	 * JsonObjectתHashMap
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
		Log.i(TAG, "����ת�����...");
		return data;
	}

	/**
	 * ��ȡ����汾��
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * ��ʾ������¶Ի���
	 */
	private void showNoticeDialog() {

		mShowDialog = new AlertDialog.Builder(mContext).create();
		mShowDialog.show();

		// ���ô��ڵ�����ҳ��
		Window window = mShowDialog.getWindow();
		window.setContentView(R.layout.dialog_update);

		Button ok = (Button) window.findViewById(R.id.ud_ok);
		Button no = (Button) window.findViewById(R.id.ud_no);
		TextView title = (TextView) window.findViewById(R.id.ud_title);
		title.setText("�汾���ţ�" + "[" + mHashMap.get("name") + "]");

		TextView content = (TextView) window.findViewById(R.id.ud_content);
		content.setText(mHashMap.get("note"));

		// ȷ���¼�
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mShowDialog.dismiss();
				alive_ui_update();
				showDownloadDialog();
			}
		});
		// ȡ���¼�
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
	 * ��ʾ������ضԻ���
	 */
	private void showDownloadDialog() {
		// ����������ضԻ���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progressbar_update, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// ȡ������
		builder.setNegativeButton(R.string.soft_update_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// ����ȡ��״̬
						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// �����ļ�
		downloadApk();
	}

	/**
	 * ����apk�ļ�
	 */
	private void downloadApk() {
		// �������߳��������
		new downloadApkThread().start();
	}

	/**
	 * �����ļ��߳�
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * ��װAPK�ļ�
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

	/**
	 * ��֤UI���浯��һ�Σ��û�ȡ�����ڵ��� �û��ֶ��������Ժ��ڵ�����
	 */
	public void cancle_ui_update() {
		StatusImpl si = new StatusImpl(mContext);
		Status s_st = si.search("uiupdate");
		Status st = new Status();

		// �ж��Ƿ���ڼ�¼
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
	 * UI�������¿���Ե���
	 */
	public void alive_ui_update() {
		StatusImpl si = new StatusImpl(mContext);
		Status s_st = si.search("uiupdate");
		Status st = new Status();

		// �ж��Ƿ���ڼ�¼
		if (s_st.getId() > 0) {
			st.setName("uiupdate");
			st.setStatus(0);
			si.change(st);
		}
	}
}
