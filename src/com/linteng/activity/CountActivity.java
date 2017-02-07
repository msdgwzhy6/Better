package com.linteng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.linteng.bean.Status;
import com.linteng.better.R;
import com.linteng.db.StatusImpl;
import com.linteng.service.LocationService;
import com.linteng.tools.ActivityManager;
import com.linteng.tools.UpdateManager;
import com.linteng.tools.UtilManager;

public class CountActivity extends Activity {

	public String TAG = "BETTER-CountActivity";

	Button menu;
	TextView countDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "CountActivity已进入");
		// 加入Activity链表
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_count);
		initView();// 初始化控件
		countDate.setText("" + UtilManager.countDate());
		menu.setOnClickListener(menuListener);

		Boolean isrun = UtilManager.isServiceWork(getApplicationContext(),
				"com.linteng.service.LocationService");
		if (!isrun) {
			Intent lsIntent = new Intent(getApplicationContext(),
					LocationService.class);
			getApplicationContext().startService(lsIntent);
			Log.i(TAG, "locationserice服务正在由CountACtivity启动...");
		} else {
			Log.i(TAG, "locationserice服务正在运行中，无需启动");
		}

		if (checkUiUpdate()) {
			// 检查更新
			UpdateManager manager = new UpdateManager(CountActivity.this, 0);
			// 检查软件更新
			manager.checkUpdate();
		}
	}

	protected void initView() {
		menu = (Button) findViewById(R.id.menu);
		countDate = (TextView) findViewById(R.id.countDate);
	}

	// signIn监听
	Button.OnClickListener menuListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(getApplication(), MenuActivity.class);
			startActivity(intent);
		}
	};

	/**
	 * 检查是否检查更新
	 */
	private boolean checkUiUpdate() {
		StatusImpl si = new StatusImpl(getApplication());
		Status st = si.search("uiupdate");
		if (st.getStatus() > 0) {
			return false;
		} else {
			return true;
		}
	}
}
