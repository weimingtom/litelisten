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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ColorDialog
{
	private static PopupWindow pw;
	private static EditText edtMessage;
	private static DisplayMetrics dm;

	public static void ShowMessage(Activity act, View WindowParent, String Title, float MessageFontSize, int DefaultColor, int ScreenOrantation, OnClickListener onOK)
	{
		dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_color_dialog, null, false);

		if (ScreenOrientation == 1 || ScreenOrientation == 3)
		{
			if (dm.densityDpi == DisplayMetrics.DENSITY_HIGH)
				pw = new PopupWindow(view, dm.widthPixels - 200, LayoutParams.WRAP_CONTENT, true);
			else
				pw = new PopupWindow(view, dm.widthPixels - 100, LayoutParams.WRAP_CONTENT, true);
		}
		else
			pw = new PopupWindow(view, dm.widthPixels - 40, LayoutParams.WRAP_CONTENT, true);

		// ����ͼ��
		ImageView imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
		imgIcon.setBackgroundResource(R.drawable.album_normal);

		// ���öԻ������
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtTitle.setText(Title);

		// ������ɫѡȡ���򲼾�
		LinearLayout layColorSelector = (LinearLayout) view.findViewById(R.id.layColorSelector);

		// ������ʾ��Ϣ
		edtMessage = new EditText(act);
		edtMessage.setTextSize(MessageFontSize);

		ColorPicker cpv;
		if (ScreenOrantation == 1 || ScreenOrantation == 3)
		{
			if (dm.densityDpi == DisplayMetrics.DENSITY_HIGH)
				cpv = new ColorPicker(act, DefaultColor, 1, edtMessage);
			else
				cpv = new ColorPicker(act, DefaultColor, 0.5, edtMessage);
		}
		else
		{
			if (dm.densityDpi == DisplayMetrics.DENSITY_HIGH)
				cpv = new ColorPicker(act, DefaultColor, 1.5, edtMessage);
			else
				cpv = new ColorPicker(act, DefaultColor, 1, edtMessage);
		}

		layColorSelector.addView(cpv);
		layColorSelector.addView(edtMessage);

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
		ColorDialog.pw = pw;
	}

	public static EditText getEdtMessage()
	{
		return edtMessage;
	}

	public static void setEdtMessage(EditText edtMessage)
	{
		ColorDialog.edtMessage = edtMessage;
	}

	public static DisplayMetrics getDm()
	{
		return dm;
	}

	public static void setDm(DisplayMetrics dm)
	{
		ColorDialog.dm = dm;
	}
}