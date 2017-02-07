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
	// 自定义progressdialog
	@SuppressWarnings("deprecation")
	public static Dialog createCustomerProgressDialog(Context context,
			String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_progress, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v
				.findViewById(R.id.customer_progress_dialog);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.dl_img);
		TextView textview = (TextView) v.findViewById(R.id.dl_tv);// 提示文字
		textview.setText(msg);// 设置加载信息
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.drawable.loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		Dialog myDialog = new Dialog(context, R.style.customer_progress_dialog);// 创建自定义样式dialog

		myDialog.setCancelable(false);// 不可以用“返回键”取消
		myDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		return myDialog;
	}
}
