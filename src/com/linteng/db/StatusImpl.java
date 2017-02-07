package com.linteng.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.linteng.bean.Status;

public class StatusImpl {

	public String TAG = "BETTER-StatusImpl";
	private DBHelper helper;

	// ���췽��
	public StatusImpl(Context context) {
		this.helper = new DBHelper(context, "better.db", null, 1);
	}

	// �����¼
	public void insert(Status st) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into status ( name , status ) values( ? , ? )",
				new Object[] { st.getName(), st.getStatus() });
		Log.i(TAG, "���ڲ������ݡ�����" + st.getName() + "---" + st.getStatus());
		db.close();
	}

	// �޸ļ�¼״̬
	public void change(Status st) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update status set status = ? where name = ?", new Object[] {
				st.getStatus(), st.getName() });
		db.close();
	}

	// ��ѯ״̬
	public Status search(String name) {
		Status st = new Status();
		SQLiteDatabase db = helper.getWritableDatabase();
		System.out.println("���ڲ�ѯ������");
		// ���α�Cursor���մ����ݿ������������
		Cursor cursor = db.rawQuery("select * from status where name = ?",
				new String[] { name });
		if (cursor.moveToNext()) {
			st.setId(cursor.getInt(cursor.getColumnIndex("id")));
			st.setName(cursor.getString(cursor.getColumnIndex("name")));
			st.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
		}
		db.close();
		Log.i(TAG, "��ѯ������" + st.getName() + "-----" + st.getStatus());
		return st;
	}
}
