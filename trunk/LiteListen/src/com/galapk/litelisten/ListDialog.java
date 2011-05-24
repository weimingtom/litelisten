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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ListDialog
{
	private static PopupWindow pw;
	private static int[] CheckedID;
	private static String ret = "";
	private static DisplayMetrics dm;

	public static void ShowDialog(Activity act, View WindowParent, String Title, String[] Content, float ListFontSize, int CheckedIndex, OnClickListener onOK)
	{
		dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();
		ret = String.valueOf(CheckedIndex);

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_list_dialog, null, false);

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

		// ������ʾ��Ϣ
		RadioGroup grpOption = (RadioGroup) view.findViewById(R.id.grpOption);
		CheckedID = new int[Content.length];
		for (int i = 0; i < Content.length; i++)
		{
			RadioButton radOption = new RadioButton(act);
			radOption.setText(Content[i]);
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
		ListDialog.pw = pw;
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
		ListDialog.ret = ret;
	}

	public static DisplayMetrics getDm()
	{
		return dm;
	}

	public static void setDm(DisplayMetrics dm)
	{
		ListDialog.dm = dm;
	}
}