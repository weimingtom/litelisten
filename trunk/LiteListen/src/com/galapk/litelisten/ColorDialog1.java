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

import android.app.AlertDialog;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColorDialog1
{
	private SettingService ss = null;
	private AlertDialog dlg = null;
	private LinearLayout layMessage = null;
	private TextView txtColor = null;
	private ColorPickerView cpv = null;
	private String strPreference = "";
	private String strOldColor = "";

	public ColorDialog1(SettingService ss)
	{
		this.ss = ss;
	}

	public void ShowDialog(String Preference, double Zoom)
	{
		strPreference = Preference;
		dlg = new AlertDialog.Builder(ss).show();
		dlg.getWindow().setContentView(R.layout.alertdialog);
		txtColor = new TextView(ss);
		txtColor.setTextColor(Color.WHITE);
		txtColor.setTextSize(18);
		strOldColor = ss.findPreference(Preference).getSummary().toString();

		if (ss.findPreference(Preference).getSummary() == null)
		{
			cpv = new ColorPickerView(ss, Color.parseColor("#FFFFFF"), Zoom, this);
			txtColor.setText("#FFFFFF");
		}
		else
		{
			cpv = new ColorPickerView(ss, Color.parseColor(strOldColor), Zoom, this);
			txtColor.setText(ss.findPreference(Preference).getSummary().toString());
		}

		layMessage = (LinearLayout) dlg.findViewById(R.id.layMessage);
		layMessage.setPadding(10, 10, 10, 10);
		layMessage.addView(cpv);
		layMessage.addView(txtColor);

		// 设置图标
		ImageView imgIcon = (ImageView) dlg.getWindow().findViewById(R.id.imgIcon);
		imgIcon.setBackgroundResource(R.drawable.album_normal);

		// 设置对话框标题
		TextView txtTitle = (TextView) dlg.getWindow().findViewById(R.id.txtTitle);
		txtTitle.setText(ss.findPreference(Preference).getTitle().toString());

		// 设置提示信息
		TextView txtMessage = (TextView) dlg.getWindow().findViewById(R.id.txtMessage);
		txtMessage.setVisibility(View.GONE);

		// 设置确定按钮
		Button btnOK = (Button) dlg.getWindow().findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Editor e = ss.getSp().edit(); // 编辑配置文件
				e.putString(strPreference, cpv.getStrColor()); // 设置颜色
				e.commit(); // 确定更改
				ss.findPreference(strPreference).setSummary(cpv.getStrColor());
				dlg.cancel();
			}
		});

		// 设置取消按钮
		Button btnCancel = (Button) dlg.getWindow().findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ss.findPreference(strPreference).setSummary(strOldColor);
				dlg.cancel();
			}
		});
	}

	public void ChangeLayout()
	{
		if (dlg != null && dlg.isShowing())
		{
			dlg.getWindow().setContentView(R.layout.alertdialog);
			txtColor = new TextView(ss);
			txtColor.setTextColor(Color.WHITE);
			txtColor.setTextSize(18);
			strOldColor = ss.findPreference(strPreference).getSummary().toString();

			if (ss.findPreference(strPreference).getSummary() == null)
			{
				cpv = new ColorPickerView(ss, Color.parseColor("#FFFFFF"), 0.5, this);
				txtColor.setText("#FFFFFF");
			}
			else
			{
				cpv = new ColorPickerView(ss, Color.parseColor(strOldColor), 0.5, this);
				txtColor.setText(ss.findPreference(strPreference).getSummary().toString());
			}

			layMessage = (LinearLayout) dlg.findViewById(R.id.layMessage);
			layMessage.setPadding(10, 10, 10, 10);
			layMessage.addView(cpv);
			layMessage.addView(txtColor);

			// 设置图标
			ImageView imgIcon = (ImageView) dlg.getWindow().findViewById(R.id.imgIcon);
			imgIcon.setBackgroundResource(R.drawable.album_normal);

			// 设置对话框标题
			TextView txtTitle = (TextView) dlg.getWindow().findViewById(R.id.txtTitle);
			txtTitle.setText(ss.findPreference(strPreference).getTitle().toString());

			// 设置提示信息
			TextView txtMessage = (TextView) dlg.getWindow().findViewById(R.id.txtMessage);
			txtMessage.setVisibility(View.GONE);

			// 设置确定按钮
			Button btnOK = (Button) dlg.getWindow().findViewById(R.id.btnOK);
			btnOK.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					Editor e = ss.getSp().edit(); // 编辑配置文件
					e.putString(strPreference, cpv.getStrColor()); // 设置颜色
					e.commit(); // 确定更改
					ss.findPreference(strPreference).setSummary(cpv.getStrColor());
					dlg.cancel();
				}
			});

			// 设置取消按钮
			Button btnCancel = (Button) dlg.getWindow().findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					ss.findPreference(strPreference).setSummary(strOldColor);
					dlg.cancel();
				}
			});
		}
	}

	public AlertDialog getDlg()
	{
		return dlg;
	}

	public void setDlg(AlertDialog dlg)
	{
		this.dlg = dlg;
	}

	public LinearLayout getLayMessage()
	{
		return layMessage;
	}

	public void setLayMessage(LinearLayout layMessage)
	{
		this.layMessage = layMessage;
	}

	public TextView getTxtColor()
	{
		return txtColor;
	}

	public void setTxtColor(TextView txtColor)
	{
		this.txtColor = txtColor;
	}

	public ColorPickerView getCpv()
	{
		return cpv;
	}

	public void setCpv(ColorPickerView cpv)
	{
		this.cpv = cpv;
	}

	public SettingService getSs()
	{
		return ss;
	}

	public void setSs(SettingService ss)
	{
		this.ss = ss;
	}

	public String getStrPreference()
	{
		return strPreference;
	}

	public void setStrPreference(String strPreference)
	{
		this.strPreference = strPreference;
	}

	public String getStrOldColor()
	{
		return strOldColor;
	}

	public void setStrOldColor(String strOldColor)
	{
		this.strOldColor = strOldColor;
	}
}