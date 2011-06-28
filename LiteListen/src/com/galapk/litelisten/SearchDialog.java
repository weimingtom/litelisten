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

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

public class SearchDialog
{
	private static PopupWindow pw;
	private static View view;
	private static ImageButton btnSearch;
	private static EditText txtKeyword;

	public static void ShowDialog(final scrMain main, String LanguageIndex, boolean UsingAnimation, View WindowParent)
	{
		if (!LanguageIndex.equals("3"))
		{
			Configuration config = main.getResources().getConfiguration(); // 获得设置对象

			if (LanguageIndex.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
			else if (LanguageIndex.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // 繁体中文
			else if (LanguageIndex.equals("2"))
				config.locale = Locale.US; // 美式英语

			main.getResources().updateConfiguration(config, main.getResources().getDisplayMetrics());
		}

		LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.popup_search_dialog, null, false);
		btnSearch = (ImageButton) view.findViewById(R.id.btnSearch);
		txtKeyword = (EditText) view.findViewById(R.id.txtKeyword);

		// 响应文本改变
		txtKeyword.addTextChangedListener(new TextWatcher()
		{
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			public void afterTextChanged(Editable s)
			{
				Editor edt = main.getSp().edit();
				edt.putString("LastKeyword", txtKeyword.getText().toString());
				main.getSt().setLastKeyword(txtKeyword.getText().toString());
				edt.commit();
				main.SetMusicListByDB();
			}
		});

		btnSearch.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Editor edt = main.getSp().edit();
				edt.putString("LastKeyword", txtKeyword.getText().toString());
				main.getSt().setLastKeyword(txtKeyword.getText().toString());
				edt.commit();
				main.SetMusicListByDB();
				pw.dismiss();
			}
		});

		pw = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		pw.setBackgroundDrawable(new BitmapDrawable()); // 响应返回键必须的语句

		if (main.getSt().getUseAnimation())
			pw.setAnimationStyle(R.style.DialogAnimation);
		pw.showAtLocation(main.getLayActivity(), Gravity.TOP + Gravity.CENTER_HORIZONTAL, 0, 0); // 显示在底部
	}

	public static PopupWindow getPw()
	{
		return pw;
	}

	public static void setPw(PopupWindow pw)
	{
		SearchDialog.pw = pw;
	}

	public static View getView()
	{
		return view;
	}

	public static void setView(View view)
	{
		SearchDialog.view = view;
	}

	public static EditText getTxtKeyword()
	{
		return txtKeyword;
	}

	public static void setTxtKeyword(EditText txtKeyword)
	{
		SearchDialog.txtKeyword = txtKeyword;
	}

	public static ImageButton getBtnSearch()
	{
		return btnSearch;
	}

	public static void setBtnSearch(ImageButton btnSearch)
	{
		SearchDialog.btnSearch = btnSearch;
	}
}