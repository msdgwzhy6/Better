package com.linteng.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linteng.bean.Status;
import com.linteng.better.R;
import com.linteng.db.StatusImpl;
import com.linteng.tools.ActivityManager;

public class MenuActivity extends Activity {

	LinearLayout logout, settings;
	Button back;
	TextView home, note, bigday, other;
	AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 加入Activity链表
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_menu);
		init();
		logout.setOnClickListener(confirmlogoutListener);
		settings.setOnClickListener(settingListener);
		back.setOnClickListener(backListener);
		home.setOnClickListener(unWriteListener);
		note.setOnClickListener(unWriteListener);
		bigday.setOnClickListener(unWriteListener);
		other.setOnClickListener(unWriteListener);
	}

	// 初始化控件
	protected void init() {
		logout = (LinearLayout) findViewById(R.id.logout);
		settings = (LinearLayout) findViewById(R.id.settings);
		back = (Button) findViewById(R.id.back);
		home = (TextView) findViewById(R.id.home);
		note = (TextView) findViewById(R.id.note);
		bigday = (TextView) findViewById(R.id.bigday);
		other = (TextView) findViewById(R.id.other);
	}

	LinearLayout.OnClickListener confirmlogoutListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog = new AlertDialog.Builder(MenuActivity.this).create();
			dialog.show();

			// 设置窗口的内容页面
			Window window = dialog.getWindow();
			window.setContentView(R.layout.dialog_exit);

			Button ok = (Button) window.findViewById(R.id.ed_ok);
			Button no = (Button) window.findViewById(R.id.ed_no);
			TextView msg = (TextView) window.findViewById(R.id.ed_tv);
			msg.setText("DO YOU WANT EXIT?");

			// 确认事件
			ok.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();

					StatusImpl si = new StatusImpl(getApplication());
					Status st = new Status();
					st.setName("login");
					st.setStatus(0);
					si.change(st);

					Intent intent = new Intent(getApplication(),
							MainActivity.class);
					startActivity(intent);
					ActivityManager.getInstance().exit();
				}
			});
			// 取消事件
			no.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
	};

	LinearLayout.OnClickListener backListener = new View.OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};

	LinearLayout.OnClickListener unWriteListener = new View.OnClickListener() {
		public void onClick(View v) {
			Toast toast = new Toast(getApplication());
			toast = Toast.makeText(getApplicationContext(),
					"In the development, do not worry", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	};

	LinearLayout.OnClickListener settingListener = new View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(getApplication(), SettingsActivity.class);
			startActivity(intent);
		}
	};
}