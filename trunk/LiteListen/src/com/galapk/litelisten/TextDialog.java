/*
 * Copyright (C) 2011 The LiteListen Project
 * 
 * Licensed under the Mozilla Public Licence, version 1.1 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * 轻听音乐播放器项目 版权所有 2011
 * 基于 Mozilla Public Licence 1.1 发布（下称“协议”）。
 * 若非承诺遵守该协议，您将不能使用本文件。
 * 您可以从下述网站获得该协议的副本：
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 */

package com.galapk.litelisten;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class TextDialog
{
	private static PopupWindow pw;
	private static EditText edtMessage;

	public static void ShowMessage(Activity act, String LanguageIndex, boolean UsingAnimation, View WindowParent, int TitleResourceID, int MessageResourceID, float MessageFontSize, String EditorText,
			float EditorFontSize, OnClickListener onOK)
	{
		int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_text_dialog, null, false);

		if (ScreenOrientation == 1 || ScreenOrientation == 3)
			pw = new PopupWindow(view, 600, LayoutParams.WRAP_CONTENT, true);
		else
			pw = new PopupWindow(view, 440, LayoutParams.WRAP_CONTENT, true);

		pw.setBackgroundDrawable(new BitmapDrawable()); // 响应返回键必须的语句

		// 设置图标
		ImageView imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
		imgIcon.setBackgroundResource(R.drawable.icon);

		// 设置对话框标题
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtTitle.setText(TitleResourceID);

		// 设置提示信息
		TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
		if (MessageResourceID == 0)
			txtMessage.setText("");
		else
			txtMessage.setText(MessageResourceID);
		txtMessage.setTextSize(MessageFontSize);

		// 设置文本框内容
		edtMessage = (EditText) view.findViewById(R.id.edtMessage);
		edtMessage.setText(EditorText);
		edtMessage.setTextSize(EditorFontSize);

		// 设置确定按钮
		Button btnOK = (Button) view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOK);

		// 设置取消按钮
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				pw.dismiss();
			}
		});

		if (UsingAnimation)
			pw.setAnimationStyle(R.style.DialogAnimation);
		pw.showAtLocation(WindowParent, Gravity.CENTER, 0, 0); // 显示PopupWindow
	}

	public static PopupWindow getPw()
	{
		return pw;
	}

	public static void setPw(PopupWindow pw)
	{
		TextDialog.pw = pw;
	}

	public static EditText getEdtMessage()
	{
		return edtMessage;
	}

	public static void setEdtMessage(EditText edtMessage)
	{
		TextDialog.edtMessage = edtMessage;
	}
}