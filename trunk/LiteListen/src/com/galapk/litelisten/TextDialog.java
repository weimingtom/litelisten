/*
 * Copyright (C) 2011 The LiteListen Project
 * 
 * Licensed under the Mozilla Public Licence, version 1.1 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * �������ֲ�������Ŀ ��Ȩ���� 2011
 * ���� Mozilla Public Licence 1.1 �������³ơ�Э�顱����
 * ���ǳ�ŵ���ظ�Э�飬��������ʹ�ñ��ļ���
 * �����Դ�������վ��ø�Э��ĸ�����
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 */

package com.galapk.litelisten;

import android.app.Activity;
import android.content.Context;
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
	private static String ret;
	private static EditText txtMessage;

	public static void ShowMessage(Activity act, View WindowParent, String Title, String DefaultText, float MessageSize, OnClickListener onOK)
	{
		int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_text_dialog, null, false);

		if (ScreenOrientation == 1 || ScreenOrientation == 3)
			pw = new PopupWindow(view, 600, LayoutParams.WRAP_CONTENT, true);
		else
			pw = new PopupWindow(view, 440, LayoutParams.WRAP_CONTENT, true);

		// ����ͼ��
		ImageView imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
		imgIcon.setBackgroundResource(R.drawable.album_normal);

		// ���öԻ������
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtTitle.setText(Title);

		// ������ʾ��Ϣ
		txtMessage = (EditText) view.findViewById(R.id.txtMessage);
		txtMessage.setText(DefaultText);
		txtMessage.setTextSize(MessageSize);

		// ����ȷ����ť
		Button btnOK = (Button) view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOK);

		// ����ȡ����ť
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				pw.dismiss();
			}
		});

		pw.showAtLocation(WindowParent, Gravity.CENTER, 0, 0); // ��ʾPopupWindow
	}

	public static PopupWindow getPw()
	{
		return pw;
	}

	public static void setPw(PopupWindow pw)
	{
		TextDialog.pw = pw;
	}

	public static String getRet()
	{
		return ret;
	}

	public static void setRet(String ret)
	{
		TextDialog.ret = ret;
	}

	public static EditText getTxtMessage()
	{
		return txtMessage;
	}

	public static void setTxtMessage(EditText txtMessage)
	{
		TextDialog.txtMessage = txtMessage;
	}
}