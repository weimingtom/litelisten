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
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ListDialog
{
	private PopupWindow pw;
	private String ret = "";
	private TextView txtCurrentPath;
	private ListView lstFile;

	public void ShowDialog(Activity act, String LanguageIndex, boolean UsingAnimation, View WindowParent, int TitleResourceID, float ListFontSize, OnClickListener onOK, OnItemClickListener onList)
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
		View view = inflater.inflate(R.layout.popup_list_dialog, null, false);

		if (ScreenOrientation == 1 || ScreenOrientation == 3)
			pw = new PopupWindow(view, 600, LayoutParams.WRAP_CONTENT, true);
		else
			pw = new PopupWindow(view, 440, LayoutParams.WRAP_CONTENT, true);

		pw.setBackgroundDrawable(new BitmapDrawable()); // ��Ӧ���ؼ���������

		// ����ͼ��
		ImageView imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
		imgIcon.setBackgroundResource(R.drawable.icon);

		// ���öԻ������
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtTitle.setText(TitleResourceID);

		txtCurrentPath = (TextView) view.findViewById(R.id.txtCurrentPath);
		lstFile = (ListView) view.findViewById(R.id.lstFile);
		lstFile.setOnItemClickListener(onList);

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

		if (UsingAnimation)
			pw.setAnimationStyle(R.style.DialogAnimation);
		pw.showAtLocation(WindowParent, Gravity.CENTER, 0, 0); // ��ʾPopupWindow
	}

	public PopupWindow getPw()
	{
		return pw;
	}

	public void setPw(PopupWindow pw)
	{
		this.pw = pw;
	}

	public String getRet()
	{
		return ret;
	}

	public void setRet(String ret)
	{
		this.ret = ret;
	}

	public TextView getTxtCurrentPath()
	{
		return txtCurrentPath;
	}

	public void setTxtCurrentPath(TextView txtCurrentPath)
	{
		this.txtCurrentPath = txtCurrentPath;
	}

	public ListView getLstFile()
	{
		return lstFile;
	}

	public void setLstFile(ListView lstFile)
	{
		this.lstFile = lstFile;
	}
}