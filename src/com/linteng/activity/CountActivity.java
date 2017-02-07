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
		
		Log.i(TAG, "CountActivity�ѽ���");
		// ����Activity����
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_count);
		initView();// ��ʼ���ؼ�
		countDate.setText("" + UtilManager.countDate());
		menu.setOnClickListener(menuListener);

		Boolean isrun = UtilManager.isServiceWork(getApplicationContext(),
				"com.linteng.service.LocationService");
		if (!isrun) {
			Intent lsIntent = new Intent(getApplicationContext(),
					LocationService.class);
			getApplicationContext().startService(lsIntent);
			Log.i(TAG, "locationserice����������CountACtivity����...");
		} else {
			Log.i(TAG, "locationserice�������������У���������");
		}

		if (checkUiUpdate()) {
			// ������
			UpdateManager manager = new UpdateManager(CountActivity.this, 0);
			// ����������
			manager.checkUpdate();
		}
	}

	protected void initView() {
		menu = (Button) findViewById(R.id.menu);
		countDate = (TextView) findViewById(R.id.countDate);
	}

	// signIn����
	Button.OnClickListener menuListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(getApplication(), MenuActivity.class);
			startActivity(intent);
		}
	};

	/**
	 * ����Ƿ������
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
