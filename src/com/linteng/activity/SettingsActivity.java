package com.linteng.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linteng.better.R;
import com.linteng.tools.UpdateManager;
import com.linteng.tools.UtilManager;

public class SettingsActivity extends Activity {

	private String TAG = "BETTER-Settings";

	private TextView update;
	private Button dropTip, st_menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		init();
		setListener();
		Log.i(TAG, "settingactivity已初始化完毕");
	}

	private void init() {
		update = (TextView) findViewById(R.id.st_update);
		dropTip = (Button) findViewById(R.id.st_dropTip);
		if (UtilManager.search_drop_tip(getApplicationContext())) {
			dropTip.setBackgroundResource(R.drawable.btn_open);
		} else {
			dropTip.setBackgroundResource(R.drawable.btn_close);
		}
		st_menu = (Button) findViewById(R.id.st_menu);

	}

	private void setListener() {
		update.setOnClickListener(updateListener);
		dropTip.setOnClickListener(dropTipListener);
		st_menu.setOnClickListener(menuListener);
	}

	LinearLayout.OnClickListener updateListener = new View.OnClickListener() {

		public void onClick(View v) {
			UpdateManager manager = new UpdateManager(SettingsActivity.this, 1);
			// 检查软件更新
			manager.checkUpdate();

		}
	};
	LinearLayout.OnClickListener dropTipListener = new View.OnClickListener() {
		public void onClick(View v) {
			// 查询数据库状态
			if (UtilManager.search_drop_tip(getApplicationContext())) {
				dropTip.setBackgroundResource(R.drawable.btn_close);
			} else {
				dropTip.setBackgroundResource(R.drawable.btn_open);
			}
			UtilManager.change_drop_tip(getApplicationContext());
		}
	};
	LinearLayout.OnClickListener menuListener = new View.OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
}
