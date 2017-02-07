package com.linteng.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linteng.better.R;

public class CusProDialog {
	// �Զ���progressdialog
	@SuppressWarnings("deprecation")
	public static Dialog createCustomerProgressDialog(Context context,
			String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_progress, null);// �õ�����view
		LinearLayout layout = (LinearLayout) v
				.findViewById(R.id.customer_progress_dialog);// ���ز���
		// main.xml�е�ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.dl_img);
		TextView textview = (TextView) v.findViewById(R.id.dl_tv);// ��ʾ����
		textview.setText(msg);// ���ü�����Ϣ
		// ���ض���
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.drawable.loading_animation);
		// ʹ��ImageView��ʾ����
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		Dialog myDialog = new Dialog(context, R.style.customer_progress_dialog);// �����Զ�����ʽdialog

		myDialog.setCancelable(false);// �������á����ؼ���ȡ��
		myDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));// ���ò���
		return myDialog;
	}
}
