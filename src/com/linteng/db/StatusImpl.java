package com.linteng.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.linteng.bean.Status;

public class StatusImpl {

	public String TAG = "BETTER-StatusImpl";
	private DBHelper helper;

	// 构造方法
	public StatusImpl(Context context) {
		this.helper = new DBHelper(context, "better.db", null, 1);
	}

	// 插入记录
	public void insert(Status st) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into status ( name , status ) values( ? , ? )",
				new Object[] { st.getName(), st.getStatus() });
		Log.i(TAG, "正在插入数据。。。" + st.getName() + "---" + st.getStatus());
		db.close();
	}

	// 修改记录状态
	public void change(Status st) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update status set status = ? where name = ?", new Object[] {
				st.getStatus(), st.getName() });
		db.close();
	}

	// 查询状态
	public Status search(String name) {
		Status st = new Status();
		SQLiteDatabase db = helper.getWritableDatabase();
		System.out.println("正在查询。。。");
		// 用游标Cursor接收从数据库检索到的数据
		Cursor cursor = db.rawQuery("select * from status where name = ?",
				new String[] { name });
		if (cursor.moveToNext()) {
			st.setId(cursor.getInt(cursor.getColumnIndex("id")));
			st.setName(cursor.getString(cursor.getColumnIndex("name")));
			st.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
		}
		db.close();
		Log.i(TAG, "查询出来的" + st.getName() + "-----" + st.getStatus());
		return st;
	}
}
