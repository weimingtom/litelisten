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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class OptionDialog
{
	private static PopupWindow pw;
	private static int[] CheckedID;
	private static String ret = "";

	public static void ShowDialog(Activity act, String LanguageIndex, View WindowParent, int TitleResourceID, int ContentArrayResourceID, float ListFontSize, int CheckedIndex, OnClickListener onOK)
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

		ret = String.valueOf(CheckedIndex);

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_option_dialog, null, false);

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
		RadioGroup grpOption = (RadioGroup) view.findViewById(R.id.grpOption);
		String[] strContent = act.getResources().getStringArray(ContentArrayResourceID);
		CheckedID = new int[strContent.length];
		for (int i = 0; i < strContent.length; i++)
		{
			RadioButton radOption = new RadioButton(act);
			radOption.setText(strContent[i]);
			radOption.setTextSize(ListFontSize);
			radOption.setButtonDrawable(R.layout.option_radiobutton);
			radOption.setBackgroundResource(R.layout.option_bg_list);
			grpOption.addView(radOption);

			if (i == CheckedIndex)
				radOption.setChecked(true);

			android.view.ViewGroup.LayoutParams layOption = radOption.getLayoutParams();
			layOption.width = android.view.ViewGroup.LayoutParams.FILL_PARENT;
			layOption.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			radOption.setLayoutParams(layOption);

			CheckedID[i] = radOption.getId();
		}

		grpOption.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				for (int i = 0; i < CheckedID.length; i++)
				{
					if (CheckedID[i] == checkedId)
					{
						ret = String.valueOf(i);
						break;
					}
				}
			}
		});

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
		OptionDialog.pw = pw;
	}

	public static int[] getCheckedID()
	{
		return CheckedID;
	}

	public static void setCheckedID(int[] checkedID)
	{
		CheckedID = checkedID;
	}

	public static String getRet()
	{
		return ret;
	}

	public static void setRet(String ret)
	{
		OptionDialog.ret = ret;
	}
}