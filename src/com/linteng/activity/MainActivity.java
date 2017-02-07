package com.linteng.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linteng.bean.Status;
import com.linteng.better.R;
import com.linteng.db.StatusImpl;
import com.linteng.tools.ActivityManager;
import com.linteng.view.CusProDialog;

public class MainActivity extends Activity {

	Button bt_signIn;
	EditText et_username;
	EditText et_password;
	Dialog progressDialog;

	String username;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ����Activity����
		ActivityManager.getInstance().addActivity(this);
		if (checkLogin()) { // ͨ����֤����ת��������
			Intent caintent = new Intent(getApplication(), CountActivity.class);
			startActivity(caintent);
			ActivityManager.getInstance().exit();
		} else {
			setContentView(R.layout.activity_main);
			init();
			bt_signIn.setOnClickListener(signInListener);
		}

	}

	// ��ʼ���ؼ�
	protected void init() {
		bt_signIn = (Button) findViewById(R.id.signIn);
		et_username = (EditText) findViewById(R.id.username);
		et_password = (EditText) findViewById(R.id.password);
	}

	// ��Handler������UI
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// �ر�ProgressDialog
			progressDialog.dismiss();
			// ����UI
			if (msg.what > 0) {
				writeLogin();
				// ͨ����֤����ת��������
				Intent intent = new Intent(getApplication(),
						CountActivity.class);
				startActivity(intent);
				ActivityManager.getInstance().exit();
			} else {
				Toast toast = new Toast(getApplication());
				toast = Toast.makeText(getApplicationContext(),
						"Wrong username or password", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	};

	// signIn����
	Button.OnClickListener signInListener = new Button.OnClickListener() {
		public void onClick(View v) {

			// ��ȡ�û���������Ϣ
			username = et_username.getText().toString();
			password = et_password.getText().toString();
			// �Զ���Dialog
			progressDialog = CusProDialog.createCustomerProgressDialog(
					MainActivity.this, "logining");
			progressDialog.show();

			// ����������֤�û���Ϣ
			new Thread() {

				@Override
				public void run() {
					// ģ����������
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Integer status = 0;
					if (username.equals("wanglinteng")
							&& password.equals("677")) {
						status = 1;
					} else {
						status = 0;
					}

					Message msg = new Message();
					msg.what = status;
					handler.sendMessage(msg);
				}
			}.start();

		}
	};

	private boolean checkLogin() {
		StatusImpl si = new StatusImpl(getApplication());
		Status st = si.search("login");
		if (st.getStatus() > 0) {
			return true;
		} else {
			return false;
		}

	}

	private void writeLogin() {
		StatusImpl si = new StatusImpl(getApplication());

		Status s_st = si.search("login");
		Status st = new Status();
		// �ж��Ƿ���ڼ�¼
		if (s_st.getId() > 0) {
			st.setName("login");
			st.setStatus(1);
			si.change(st);
		} else {
			st.setName("login");
			st.setStatus(1);
			si.insert(st);
		}
	}
}
