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

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MessageDialog
{
	private PopupWindow pw;
	private View WindowParent;

	public void ShowMessage(Activity act, String LanguageIndex, View WindowParent, int TitleResourceID, int MessageResourceID, float MessageSize, OnClickListener onOK, OnClickListener onCancel)
	{
		int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();

		if (!LanguageIndex.equals("3"))
		{
			Configuration config = act.getResources().getConfiguration(); // ������ö���

			if (LanguageIndex.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // ��������
			else if (LanguageIndex.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // ��������
			else if (LanguageIndex.equals("2"))
				config.locale = Locale.US; // ��ʽӢ��

			act.getResources().updateConfiguration(config, act.getResources().getDisplayMetrics());
		}

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_message_dialog, null, false);

		if (ScreenOrientation == 1 || ScreenOrientation == 3)
			pw = new PopupWindow(view, 600, LayoutParams.WRAP_CONTENT, true);
		else
			pw = new PopupWindow(view, 440, LayoutParams.WRAP_CONTENT, true);

		// ����ͼ��
		ImageView imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
		imgIcon.setBackgroundResource(R.drawable.icon);

		// ���öԻ������
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtTitle.setText(TitleResourceID);

		// ������ʾ��Ϣ
		TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
		txtMessage.setText(MessageResourceID);
		txtMessage.setTextSize(MessageSize);

		// ����ȷ����ť
		Button btnOK = (Button) view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOK);

		// ����ȡ����ť
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		if (onCancel != null)
			btnCancel.setOnClickListener(onCancel);
		else
			btnCancel.setVisibility(View.GONE); // ���ɼ�

		pw.showAtLocation(WindowParent, Gravity.CENTER, 0, 0); // ��ʾPopupWindow
	}

	/* ��ShowMessage��������������������ã�������ʾ */
	public void SetMessage(Activity act, String LanguageIndex, View WindowParent, String Title, String Message, float MessageSize, OnClickListener onOK, OnClickListener onCancel)
	{
		int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();
		this.WindowParent = WindowParent;

		if (!LanguageIndex.equals("3"))
		{
			Configuration config = act.getResources().getConfiguration(); // ������ö���

			if (LanguageIndex.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // ��������
			else if (LanguageIndex.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // ��������
			else if (LanguageIndex.equals("2"))
				config.locale = Locale.US; // ��ʽӢ��

			act.getResources().updateConfiguration(config, act.getResources().getDisplayMetrics());
		}

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_message_dialog, null, false);

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
		TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
		txtMessage.setText(Message);
		txtMessage.setTextSize(MessageSize);

		// ����ȷ����ť
		Button btnOK = (Button) view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOK);

		// ����ȡ����ť
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		if (onCancel != null)
			btnCancel.setOnClickListener(onCancel);
		else
			btnCancel.setVisibility(View.GONE); // ���ɼ�
	}

	public void CloseDialog()
	{
		pw.dismiss();
	}

	public PopupWindow getPw()
	{
		return pw;
	}

	public void setPw(PopupWindow pw)
	{
		this.pw = pw;
	}

	public View getWindowParent()
	{
		return WindowParent;
	}

	public void setWindowParent(View windowParent)
	{
		WindowParent = windowParent;
	}
}