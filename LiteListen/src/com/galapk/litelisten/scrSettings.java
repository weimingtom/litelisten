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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class scrSettings extends Activity
{
	private RelativeLayout layActivity;
	private TextView txtTitle;
	private LinearLayout layCategory;
	private Button btnGeneral;
	private Button btnDisplay;
	private Button btnOthers;
	private Button btnHelp;
	private LinearLayout layCore;
	private ScrollView scrGeneral;
	private LinearLayout layGeneral;
	private Button btnLanguage;
	private Button btnMusicPath;
	private CheckBox chkIncludeSubDirectory;
	private Button btnIgnoreSize;
	private CheckBox chkIgnoreDirectory;
	private CheckBox chkAutoPause;
	private CheckBox chkLRCAutoDownload;
	private Button btnListSortOrder;
	private CheckBox chkAutoSwitchToLRC;
	private Button btnPlayMode;
	private Button btnNotifyAction;
	private Button btnFavoriteMax;
	private ScrollView scrDisplay;
	private LinearLayout layDisplay;
	private Button btnScrollMode;
	private Button btnBackgroundPort;
	private Button btnBackgroundLand;
	private Button btnBackgroundBrightness;
	private CheckBox chkBackgroundBlur;
	private CheckBox chkUseAnimation;
	private Button btnListFontSize;
	private Button btnListFontColor;
	private CheckBox chkListFontShadow;
	private Button btnListFontShadowColor;
	private Button btnLRCFontSize;
	private Button btnLRCFontColorNormal;
	private Button btnLRCFontColorHighlight;
	private CheckBox chkLRCFontShadow;
	private Button btnLRCFontShadowColor;
	private ScrollView scrOthers;
	private LinearLayout layOthers;
	private Button btnHowToCheckForUpdate;
	private Button btnCheckForUpdate;
	private Button btnRestore;
	private ScrollView scrHelp;
	private LinearLayout layHelp;
	private Button btnGoHome;
	private Button btnAbout;
	private LinearLayout layOkCancel;
	private Button btnReturn;

	private SharedPreferences sp;
	private int IMAGE_SELECTED_PORT = 0; // 竖屏照片选择标志
	private int IMAGE_SELECTED_LAND = 1; // 横屏照片选择标志
	private int ScreenOrantation = 0;
	private HandlerService hs;

	private String Language;
	private String MusicPath;
	private Boolean IncludeSubDirectory;
	private String IgnoreSize;
	private Boolean IgnoreDirectory;
	private Boolean AutoPause;
	private Boolean LRCAutoDownload;
	private String ListSortOrder;
	private Boolean AutoSwitchToLRC;
	private String PlayMode;
	private String NotifyAction;
	private String FavoriteMax;
	private String ScrollMode;
	private String BackgroundPort;
	private String BackgroundLand;
	private String BackgroundBrightness;
	private Boolean BackgroundBlur;
	private Boolean UseAnimation;
	private String ListFontSize;
	private String ListFontColor;
	private Boolean ListFontShadow;
	private String ListFontShadowColor;
	private String LRCFontSize;
	private String LRCFontColorNormal;
	private String LRCFontColorHighlight;
	private Boolean LRCFontShadow;
	private String LRCFontShadowColor;
	private String HowToCheckForUpdate;
	private String Restore;
	private int SelectedFileIndex = 0;
	private List<Map<String, String>> lstLRCFile = new ArrayList<Map<String, String>>(); // 文件列表
	private FileAdapterForSettings fa;

	@Override
	public void onPause()
	{
		super.onPause();

		Intent intent = new Intent(IntentConst.INTENT_ACTION_FLOAT_LRC_SHOW);
		sendBroadcast(intent);
	}

	// 关闭设置界面
	public void ClosePreference()
	{
		Intent intent = new Intent(IntentConst.INTENT_ACTION_PREFERENCE_REFRESH);
		intent.setClass(scrSettings.this, scrMain.class);
		intent.putExtra("Language", Language);
		intent.putExtra("MusicPath", MusicPath);
		intent.putExtra("IncludeSubDirectory", IncludeSubDirectory);
		intent.putExtra("IgnoreSize", IgnoreSize);
		intent.putExtra("IgnoreDirectory", IgnoreDirectory);
		intent.putExtra("AutoPause", AutoPause);
		intent.putExtra("LRCAutoDownload", LRCAutoDownload);
		intent.putExtra("ListSortOrder", ListSortOrder);
		intent.putExtra("AutoSwitchToLRC", AutoSwitchToLRC);
		intent.putExtra("PlayMode", PlayMode);
		intent.putExtra("NotifyAction", NotifyAction);
		intent.putExtra("FavoriteMax", FavoriteMax);
		intent.putExtra("ScrollMode", ScrollMode);
		intent.putExtra("BackgroundPort", BackgroundPort);
		intent.putExtra("BackgroundLand", BackgroundLand);
		intent.putExtra("BackgroundBrightness", BackgroundBrightness);
		intent.putExtra("BackgroundBlur", BackgroundBlur);
		intent.putExtra("UseAnimation", UseAnimation);
		intent.putExtra("ListFontSize", ListFontSize);
		intent.putExtra("ListFontColor", ListFontColor);
		intent.putExtra("ListFontShadow", ListFontShadow);
		intent.putExtra("ListFontShadowColor", ListFontShadowColor);
		intent.putExtra("LRCFontSize", LRCFontSize);
		intent.putExtra("LRCFontColorNormal", LRCFontColorNormal);
		intent.putExtra("LRCFontColorHighlight", LRCFontColorHighlight);
		intent.putExtra("LRCFontShadow", LRCFontShadow);
		intent.putExtra("LRCFontShadowColor", LRCFontShadowColor);
		intent.putExtra("HowToCheckForUpdate", HowToCheckForUpdate);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == IMAGE_SELECTED_PORT)
			{
				Bundle extras = data.getExtras();
				if (extras != null)
				{
					try
					{// 创建 .nomedia 文件
						File f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/.nomeida");
						if (f.exists())
							f.delete();
						f.createNewFile();
					}
					catch (Exception e)
					{
						if (e.getMessage() != null)
							Log.w(Common.LOGCAT_TAG, e.getMessage());
						else
							e.printStackTrace();
					}

					ImageEffect.SaveBitmap((Bitmap) extras.get("data"), Environment.getExternalStorageDirectory() + "/LiteListen", "background_source_port.png", "png", 100, true);
					MakeBackgroundImage();
				}
			}
			else if (requestCode == IMAGE_SELECTED_LAND)
			{
				Bundle extras = data.getExtras();
				if (extras != null)
				{
					try
					{// 创建 .nomedia 文件
						File f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/.nomeida");
						if (f.exists())
							f.delete();
						f.createNewFile();
					}
					catch (Exception e)
					{
						if (e.getMessage() != null)
							Log.w(Common.LOGCAT_TAG, e.getMessage());
						else
							e.printStackTrace();
					}

					ImageEffect.SaveBitmap((Bitmap) extras.get("data"), Environment.getExternalStorageDirectory() + "/LiteListen", "background_source_land.png", "png", 100, true);
					MakeBackgroundImage();
				}
			}
		}
	}

	public void MakeBackgroundImage()
	{
		Bitmap bmpBackground = null;

		// 横屏背景
		File f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_source_land.png");
		if (f.isFile() && f.exists())
		{
			bmpBackground = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_source_land.png");

			if (sp.getBoolean("chkBackgroundBlur", true))
				bmpBackground = ImageEffect.ImageCut(ImageEffect.SetAlpha(ImageEffect.SetBlur(bmpBackground, 8), Integer.parseInt(sp.getString("txtBackgroundBrightness", "75"))), 10, 10, 10, 10);
			else
				bmpBackground = ImageEffect.ImageCut(ImageEffect.SetAlpha(bmpBackground, Integer.parseInt(sp.getString("txtBackgroundBrightness", "75"))), 10, 10, 10, 10);

			ImageEffect.SaveBitmap(bmpBackground, Environment.getExternalStorageDirectory() + "/LiteListen", "background_land.png", "png", 100, true);
		}

		// 竖屏背景
		f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_source_port.png");
		if (f.isFile() && f.exists())
		{
			bmpBackground = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_source_port.png");

			if (sp.getBoolean("chkBackgroundBlur", true))
				bmpBackground = ImageEffect.SetAlpha(ImageEffect.SetBlur(bmpBackground, 8), Integer.parseInt(sp.getString("txtBackgroundBrightness", "75")));
			else
				bmpBackground = ImageEffect.SetAlpha(bmpBackground, Integer.parseInt(sp.getString("txtBackgroundBrightness", "75")));
			ImageEffect.SaveBitmap(bmpBackground, Environment.getExternalStorageDirectory() + "/LiteListen", "background_port.png", "png", 100, true);
		}
	}

	/* 获取选项值 */
	public void GetPreferences()
	{
		sp = getSharedPreferences("com.galapk.litelisten_preferences", Context.MODE_PRIVATE); // 读取配置文件

		Language = sp.getString("Language", "3");

		MusicPath = sp.getString("MusicPath", "/sdcard");
		IncludeSubDirectory = sp.getBoolean("IncludeSubDirectory", true);
		IgnoreSize = sp.getString("IgnoreSize", "1024");
		IgnoreDirectory = sp.getBoolean("IgnoreDirectory", true);
		AutoPause = sp.getBoolean("AutoPause", true);
		LRCAutoDownload = sp.getBoolean("LRCAutoDownload", false);

		ListSortOrder = sp.getString("ListSortOrder", "1");
		AutoSwitchToLRC = sp.getBoolean("AutoSwitchToLRC", true);
		PlayMode = sp.getString("PlayMode", "1");
		NotifyAction = sp.getString("NotifyAction", "0");
		FavoriteMax = sp.getString("FavoriteMax", "30");

		ScrollMode = sp.getString("ScrollMode", "0");
		BackgroundPort = sp.getString("BackgroundPort", "0");
		BackgroundLand = sp.getString("BackgroundLand", "0");
		BackgroundBrightness = sp.getString("BackgroundBrightness", "75");
		BackgroundBlur = sp.getBoolean("BackgroundBlur", true);

		UseAnimation = sp.getBoolean("UseAnimation", true);

		ListFontSize = sp.getString("ListFontSize", "18.0");
		ListFontColor = sp.getString("ListFontColor", "#FFFFFF");
		ListFontShadow = sp.getBoolean("ListFontShadow", true);
		ListFontShadowColor = sp.getString("ListFontShadowColor", "#000000");

		LRCFontSize = sp.getString("LRCFontSize", "18.0");
		LRCFontColorNormal = sp.getString("LRCFontColorNormal", "#FFFFFF");
		LRCFontColorHighlight = sp.getString("LRCFontColorHighlight", "#FFFF00");
		LRCFontShadow = sp.getBoolean("LRCFontShadow", true);
		LRCFontShadowColor = sp.getString("LRCFontShadowColor", "#9BD7FF");

		HowToCheckForUpdate = sp.getString("HowToCheckForUpdate", "1");
		Restore = sp.getString("Restore", "");
	}

	/* 刷新文件列表 */
	public void SetFileList(String path, ListDialog ld)
	{
		SelectedFileIndex = 0;
		ld.getTxtCurrentPath().setText(path);
		File[] files = new File(path).listFiles();
		List<Map<String, String>> lstFileTemp = new ArrayList<Map<String, String>>();

		if (!path.equals("/sdcard"))
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("ShowPath", getString(R.string.scrmain_file_list));
			map.put("AbsolutePath", path.substring(0, path.lastIndexOf("/")));
			lstFileTemp.add(map);
		}

		for (int i = 0; i < files.length; i++)
		{
			// 忽略点文件
			if (files[i].getName().indexOf(".") == 0)
				continue;

			// 所有文件
			if (!files[i].isDirectory())
				continue;

			Map<String, String> map = new HashMap<String, String>();
			map.put("ShowPath", files[i].getName());
			map.put("AbsolutePath", files[i].getAbsolutePath());
			lstFileTemp.add(map);
		}

		lstLRCFile = lstFileTemp;
		fa = new FileAdapterForSettings(this, lstLRCFile);
		ld.getLstFile().setAdapter(fa);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 首先设置语言
		GetPreferences();
		if (!Language.equals("3"))
		{
			Configuration config = getResources().getConfiguration(); // 获得设置对象

			if (Language.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
			else if (Language.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // 繁体中文
			else if (Language.equals("2"))
				config.locale = Locale.US; // 美式英语

			getResources().updateConfiguration(config, getResources().getDisplayMetrics());
		}

		// 设置窗口样式，必须按照顺序
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 无标题栏
		setContentView(R.layout.scr_settings);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏

		// 关闭浮动歌词
		Intent intent = new Intent(IntentConst.INTENT_ACTION_FLOAT_LRC_HIDE);
		sendBroadcast(intent);

		FindViews();
		ListernerBinding();
		GetButtonDisplay();
		ScreenOrantation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
		hs = new HandlerService(this);

		if (ScreenOrantation == 1 || ScreenOrantation == 3)
			btnGeneral.setBackgroundResource(R.drawable.bg_setting_land_category_highlight);
		else
			btnGeneral.setBackgroundResource(R.drawable.bg_setting_port_category_highlight);

		btnGeneral.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				scrGeneral.setVisibility(View.VISIBLE);
				scrDisplay.setVisibility(View.GONE);
				scrOthers.setVisibility(View.GONE);
				scrHelp.setVisibility(View.GONE);

				if (ScreenOrantation == 1 || ScreenOrantation == 3)
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_land_category_highlight);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
				}
				else
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_port_category_highlight);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
				}
			}
		});

		btnDisplay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				scrGeneral.setVisibility(View.GONE);
				scrDisplay.setVisibility(View.VISIBLE);
				scrOthers.setVisibility(View.GONE);
				scrHelp.setVisibility(View.GONE);

				if (ScreenOrantation == 1 || ScreenOrantation == 3)
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_land_category_highlight);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
				}
				else
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_port_category_highlight);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
				}
			}
		});

		btnOthers.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				scrGeneral.setVisibility(View.GONE);
				scrDisplay.setVisibility(View.GONE);
				scrOthers.setVisibility(View.VISIBLE);
				scrHelp.setVisibility(View.GONE);

				if (ScreenOrantation == 1 || ScreenOrantation == 3)
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_land_category_highlight);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
				}
				else
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_port_category_highlight);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
				}
			}
		});

		btnHelp.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				scrGeneral.setVisibility(View.GONE);
				scrDisplay.setVisibility(View.GONE);
				scrOthers.setVisibility(View.GONE);
				scrHelp.setVisibility(View.VISIBLE);

				if (ScreenOrantation == 1 || ScreenOrantation == 3)
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_land_category_normal);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_land_category_highlight);
				}
				else
				{
					btnGeneral.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnDisplay.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnOthers.setBackgroundResource(R.drawable.bg_setting_port_category_normal);
					btnHelp.setBackgroundResource(R.drawable.bg_setting_port_category_highlight);
				}
			}
		});

		btnReturn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ClosePreference();
			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// if (!Language.equals("3"))
		// {
		// Configuration config = getResources().getConfiguration(); // 获得设置对象
		//
		// if (Language.equals("0"))
		// config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
		// else if (Language.equals("1"))
		// config.locale = Locale.TRADITIONAL_CHINESE; // 繁体中文
		// else if (Language.equals("2"))
		// config.locale = Locale.US; // 美式英语
		//
		// getResources().updateConfiguration(config,
		// getResources().getDisplayMetrics());
		// }

		Intent intent = new Intent(IntentConst.INTENT_ACTION_FLOAT_LRC_HIDE);
		sendBroadcast(intent);
	}

	/* 设置各选项按钮显示文字 */
	public void GetButtonDisplay()
	{
		btnLanguage.setText(Html.fromHtml(getString(R.string.pfrscat_general_language) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_general_language)[Integer.parseInt((String) Language)] + "</font>"));
		btnMusicPath.setText(Html.fromHtml(getString(R.string.pfrscat_general_music_path) + "<br /><font color='#FFFF00'>" + MusicPath + "</font>"));
		chkIncludeSubDirectory.setChecked(IncludeSubDirectory);
		btnIgnoreSize.setText(Html.fromHtml(getString(R.string.pfrscat_general_ignore_size) + "<br /><font color='#FFFF00'>" + IgnoreSize + "</font>"));
		chkIgnoreDirectory.setChecked(IgnoreDirectory);
		chkAutoPause.setChecked(AutoPause);
		chkLRCAutoDownload.setChecked(LRCAutoDownload);
		btnListSortOrder.setText(Html.fromHtml(getString(R.string.pfrscat_general_list_order) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_general_list_order)[Integer.parseInt((String) ListSortOrder)] + "</font>"));
		chkAutoSwitchToLRC.setChecked(AutoSwitchToLRC);
		btnPlayMode.setText(Html.fromHtml(getString(R.string.pfrscat_general_play_mode) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_general_play_mode)[Integer.parseInt((String) PlayMode)] + "</font>"));
		btnNotifyAction.setText(Html.fromHtml(getString(R.string.pfrscat_general_notify_next) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_general_notify_next)[Integer.parseInt((String) NotifyAction)] + "</font>"));
		btnFavoriteMax.setText(Html.fromHtml(getString(R.string.pfrscat_general_favourite_max) + "<br /><font color='#FFFF00'>" + FavoriteMax + "</font>"));
		btnScrollMode.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_scroll_style) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_display_lrc_scroll_style)[Integer.parseInt((String) ScrollMode)] + "</font>"));
		btnBackgroundPort.setText(Html.fromHtml(getString(R.string.pfrscat_display_background_port) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_display_background)[Integer.parseInt(BackgroundPort)] + "</font>"));
		btnBackgroundLand.setText(Html.fromHtml(getString(R.string.pfrscat_display_background_land) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_display_background)[Integer.parseInt(BackgroundLand)] + "</font>"));
		btnBackgroundBrightness.setText(Html.fromHtml(getString(R.string.pfrscat_display_background_brightness) + "<br /><font color='#FFFF00'>" + BackgroundBrightness + "</font>"));
		chkBackgroundBlur.setChecked(BackgroundBlur);
		chkUseAnimation.setChecked(UseAnimation);
		btnListFontSize.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_size) + "<br /><font color='#FFFF00'>" + ListFontSize + "</font>"));
		btnListFontColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_color) + "<br /><font color='#FFFF00'>" + ListFontColor + "</font>"));
		chkListFontShadow.setChecked(ListFontShadow);
		btnListFontShadowColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_shadow_color) + "<br /><font color='#FFFF00'>" + ListFontShadowColor + "</font>"));
		btnLRCFontSize.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_font_size) + "<br /><font color='#FFFF00'>" + LRCFontSize + "</font>"));
		btnLRCFontColorNormal.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_normal_font_color) + "<br /><font color='#FFFF00'>" + LRCFontColorNormal + "</font>"));
		btnLRCFontColorHighlight.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_highlight_font_color) + "<br /><font color='#FFFF00'>" + LRCFontColorHighlight + "</font>"));
		chkLRCFontShadow.setChecked(LRCFontShadow);
		btnLRCFontShadowColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_font_shadow_color) + "<br /><font color='#FFFF00'>" + LRCFontShadowColor + "</font>"));
		btnHowToCheckForUpdate.setText(Html.fromHtml(getString(R.string.pfrscat_others_how_to_check_for_update) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_others_check_for_update)[Integer.parseInt((String) HowToCheckForUpdate)] + "</font>"));
	}

	/* 更新选项 */
	public void UpdatePreference()
	{
		Editor edt = sp.edit();

		edt.putString("Language", Language);

		edt.putString("MusicPath", MusicPath);
		edt.putBoolean("IncludeSubDirectory", IncludeSubDirectory);
		edt.putString("IgnoreSize", IgnoreSize);
		edt.putBoolean("IgnoreDirectory", IgnoreDirectory);

		edt.putBoolean("AutoPause", AutoPause);

		edt.putBoolean("LRCAutoDownload", LRCAutoDownload);

		edt.putString("ListSortOrder", ListSortOrder);
		edt.putBoolean("AutoSwitchToLRC", AutoSwitchToLRC);
		edt.putString("PlayMode", PlayMode);
		edt.putString("NotifyAction", NotifyAction);
		edt.putString("FavoriteMax", FavoriteMax);

		edt.putString("ScrollMode", ScrollMode);
		edt.putString("BackgroundPort", BackgroundPort);
		edt.putString("BackgroundLand", BackgroundLand);
		edt.putString("BackgroundBrightness", BackgroundBrightness);
		edt.putBoolean("BackgroundBlur", BackgroundBlur);

		edt.putBoolean("UseAnimation", UseAnimation);

		edt.putString("ListFontSize", ListFontSize);
		edt.putString("ListFontColor", ListFontColor);
		edt.putBoolean("ListFontShadow", ListFontShadow);
		edt.putString("ListFontShadowColor", ListFontShadowColor);

		edt.putString("LRCFontSize", LRCFontSize);
		edt.putString("LRCFontColorNormal", LRCFontColorNormal);
		edt.putString("LRCFontColorHighlight", LRCFontColorHighlight);
		edt.putBoolean("LRCFontShadow", LRCFontShadow);
		edt.putString("LRCFontShadowColor", LRCFontShadowColor);

		edt.putString("HowToCheckForUpdate", HowToCheckForUpdate);
		edt.putString("Restore", Restore);

		edt.commit();
	}

	/* 绑定按钮事件 */
	public void ListernerBinding()
	{
		btnLanguage.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_language, R.array.item_name_pfrscat_general_language, 18, Integer
						.parseInt(Language), true, new OnClickListener()
				{
					public void onClick(View v)
					{

						Language = OptionDialog.getRet();
						GetButtonDisplay();
						UpdatePreference();
						OptionDialog.getPw().dismiss();

						// 语言修改为系统语言后需要重启的提示
						if (Language.equals("3"))
						{
							final MessageDialog md = new MessageDialog();
							md.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_language, R.string.pfrscat_general_language_reboot, 18,
									new OnClickListener()
									{
										public void onClick(View v)
										{
											md.CloseDialog();
										}
									}, null);
						}
					}
				}, null);
			}
		});

		btnMusicPath.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				final ListDialog ld = new ListDialog();
				ld.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_music_path, 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						String strMusicPathNew = ld.getTxtCurrentPath().getText().toString();
						boolean IsMusicPathChanged = false;
						if (!strMusicPathNew.equals(MusicPath))
							IsMusicPathChanged = true;
						MusicPath = strMusicPathNew;
						GetButtonDisplay();
						UpdatePreference();
						ld.getPw().dismiss();

						// 更改扫描路径需要重新启动，给出提示
						if (IsMusicPathChanged)
						{
							final MessageDialog md = new MessageDialog();
							md.SetMessage(scrSettings.this, Language, UseAnimation, layActivity, getString(R.string.pfrscat_general_music_path),
									getString(R.string.pfrscat_general_music_path_changed), 18, new OnClickListener()
									{
										public void onClick(View v)
										{
											md.CloseDialog();
										}
									}, null);
						}
					}
				}, new OnItemClickListener()
				{
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
					{
						if (arg2 == SelectedFileIndex)
						{
							Map<String, String> map = new HashMap<String, String>();
							map = lstLRCFile.get(arg2);
							String strPath = map.get("AbsolutePath");
							File f = new File(strPath);
							if (f.isDirectory()) // 进入目录
								SetFileList(strPath, ld);
						}
						else
						{
							SelectedFileIndex = arg2;

							fa.getView(arg2, null, ld.getLstFile());
							fa.notifyDataSetChanged();
						}
					}
				});
				SetFileList(MusicPath, ld);
			}
		});

		chkIncludeSubDirectory.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				IncludeSubDirectory = isChecked;
				UpdatePreference();
			}
		});

		btnIgnoreSize.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_ignore_size, R.string.pfrscat_general_ignore_size_message, 18, IgnoreSize, 18,
						new OnClickListener()
						{
							public void onClick(View v)
							{
								IgnoreSize = TextDialog.getEdtMessage().getText().toString();
								GetButtonDisplay();
								UpdatePreference();
								TextDialog.getPw().dismiss();
							}
						});
			}
		});

		chkIgnoreDirectory.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				IgnoreDirectory = isChecked;
				UpdatePreference();
			}
		});

		chkAutoPause.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				AutoPause = isChecked;
				UpdatePreference();
			}
		});

		chkLRCAutoDownload.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				LRCAutoDownload = isChecked;
				UpdatePreference();
			}
		});

		btnListSortOrder.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_list_order, R.array.item_name_pfrscat_general_list_order, 18, Integer
						.parseInt(ListSortOrder), true, new OnClickListener()
				{
					public void onClick(View v)
					{
						ListSortOrder = OptionDialog.getRet();
						GetButtonDisplay();
						UpdatePreference();
						OptionDialog.getPw().dismiss();
					}
				}, null);
			}
		});

		chkAutoSwitchToLRC.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				AutoSwitchToLRC = isChecked;
				UpdatePreference();
			}
		});

		btnPlayMode.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_play_mode, R.array.item_name_pfrscat_general_play_mode, 18, Integer
						.parseInt(PlayMode), true, new OnClickListener()
				{
					public void onClick(View v)
					{
						PlayMode = OptionDialog.getRet();
						GetButtonDisplay();
						UpdatePreference();
						OptionDialog.getPw().dismiss();
					}
				}, null);
			}
		});

		btnNotifyAction.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_notify_next, R.array.item_name_pfrscat_general_notify_next, 18, Integer
						.parseInt(NotifyAction), true, new OnClickListener()
				{
					public void onClick(View v)
					{
						NotifyAction = OptionDialog.getRet();
						GetButtonDisplay();
						UpdatePreference();
						OptionDialog.getPw().dismiss();
					}
				}, null);
			}
		});

		btnFavoriteMax.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_general_favourite_max, 0, 18, FavoriteMax, 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						FavoriteMax = TextDialog.getEdtMessage().getText().toString();
						GetButtonDisplay();
						UpdatePreference();
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnScrollMode.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_lrc_scroll_style, R.array.item_name_pfrscat_display_lrc_scroll_style, 18,
						Integer.parseInt(ScrollMode), true, new OnClickListener()
						{
							public void onClick(View v)
							{
								ScrollMode = OptionDialog.getRet();
								GetButtonDisplay();
								UpdatePreference();
								OptionDialog.getPw().dismiss();
							}
						}, null);
			}
		});

		btnBackgroundPort.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_background_port, R.array.item_name_pfrscat_display_background, 18, Integer
						.parseInt(BackgroundPort), true, new OnClickListener()
				{
					public void onClick(View v)
					{
						BackgroundPort = OptionDialog.getRet();
						GetButtonDisplay();
						UpdatePreference();
						OptionDialog.getPw().dismiss();

						// 显示图像选择界面
						if (BackgroundPort.equals("1"))
						{
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
							intent.setType("image/*");
							intent.putExtra("crop", "true"); // 出现裁剪画面
							intent.putExtra("return-data", true); // 请求返回数据
							intent.putExtra("noFaceDetection", true); // 关闭人脸识别

							// 设置裁剪比例
							intent.putExtra("aspectX", 3);
							intent.putExtra("aspectY", 5);

							startActivityForResult(Intent.createChooser(intent, getString(R.string.pfrsmain_image)), IMAGE_SELECTED_PORT);
						}
					}
				}, null);
			}
		});

		btnBackgroundLand.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_background_land, R.array.item_name_pfrscat_display_background, 18, Integer
						.parseInt(BackgroundLand), true, new OnClickListener()
				{
					public void onClick(View v)
					{
						BackgroundLand = OptionDialog.getRet();
						GetButtonDisplay();
						UpdatePreference();
						OptionDialog.getPw().dismiss();

						// 显示图像选择界面
						if (BackgroundLand.equals("1"))
						{
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
							intent.setType("image/*");
							intent.putExtra("crop", "true"); // 出现裁剪画面
							intent.putExtra("return-data", true); // 请求返回数据
							intent.putExtra("noFaceDetection", true); // 关闭人脸识别

							// 设置裁剪比例
							intent.putExtra("aspectX", 5);
							intent.putExtra("aspectY", 3);

							startActivityForResult(Intent.createChooser(intent, getString(R.string.pfrsmain_image)), IMAGE_SELECTED_LAND);
						}
					}
				}, null);
			}
		});

		btnBackgroundBrightness.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_background_brightness, 0, 18, BackgroundBrightness, 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						BackgroundBrightness = TextDialog.getEdtMessage().getText().toString();
						GetButtonDisplay();
						UpdatePreference();
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		chkBackgroundBlur.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				BackgroundBlur = isChecked;
				UpdatePreference();
			}
		});

		chkUseAnimation.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				UseAnimation = isChecked;
			}
		});

		btnListFontSize.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_list_font_size, 0, 18, ListFontSize, 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						ListFontSize = TextDialog.getEdtMessage().getText().toString();
						GetButtonDisplay();
						UpdatePreference();
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnListFontColor.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_list_font_color, 18, Color.parseColor(ListFontColor), new OnClickListener()
				{
					public void onClick(View v)
					{
						ListFontColor = ColorDialog.getEdtMessage().getText().toString();
						GetButtonDisplay();
						UpdatePreference();
						ColorDialog.getPw().dismiss();
					}
				});
			}
		});

		chkListFontShadow.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				ListFontShadow = isChecked;
				UpdatePreference();
			}
		});

		btnListFontShadowColor.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_list_font_shadow_color, 18, Color.parseColor(ListFontShadowColor),
						new OnClickListener()
						{
							public void onClick(View v)
							{
								ListFontShadowColor = ColorDialog.getEdtMessage().getText().toString();
								GetButtonDisplay();
								UpdatePreference();
								ColorDialog.getPw().dismiss();
							}
						});
			}
		});

		btnLRCFontSize.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_list_font_size, 0, 18, LRCFontSize, 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						LRCFontSize = TextDialog.getEdtMessage().getText().toString();
						GetButtonDisplay();
						UpdatePreference();
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnLRCFontColorNormal.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_lrc_normal_font_color, 18, Color.parseColor(LRCFontColorNormal),
						new OnClickListener()
						{
							public void onClick(View v)
							{
								LRCFontColorNormal = ColorDialog.getEdtMessage().getText().toString();
								GetButtonDisplay();
								UpdatePreference();
								ColorDialog.getPw().dismiss();
							}
						});
			}
		});

		btnLRCFontColorHighlight.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_lrc_highlight_font_color, 18, Color.parseColor(LRCFontColorHighlight),
						new OnClickListener()
						{
							public void onClick(View v)
							{
								LRCFontColorHighlight = ColorDialog.getEdtMessage().getText().toString();
								GetButtonDisplay();
								UpdatePreference();
								ColorDialog.getPw().dismiss();
							}
						});
			}
		});

		chkLRCFontShadow.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				LRCFontShadow = isChecked;
				UpdatePreference();
			}
		});

		btnLRCFontShadowColor.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_display_lrc_font_shadow_color, 18, Color.parseColor(LRCFontShadowColor),
						new OnClickListener()
						{
							public void onClick(View v)
							{
								LRCFontShadowColor = ColorDialog.getEdtMessage().getText().toString();
								GetButtonDisplay();
								UpdatePreference();
								ColorDialog.getPw().dismiss();
							}
						});
			}
		});

		btnHowToCheckForUpdate.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				OptionDialog.ShowDialog(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_others_how_to_check_for_update, R.array.item_name_pfrscat_others_check_for_update, 18,
						Integer.parseInt(HowToCheckForUpdate), true, new OnClickListener()
						{
							public void onClick(View v)
							{
								HowToCheckForUpdate = OptionDialog.getRet();
								GetButtonDisplay();
								UpdatePreference();
								OptionDialog.getPw().dismiss();
							}
						}, null);
			}
		});

		btnCheckForUpdate.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				new Thread()
				{
					public void run()
					{
						// 显示浮动提示
						Message msgCheck = new Message();
						msgCheck.obj = getString(R.string.pfrscat_others_check_for_update_checking);
						hs.getHdlShowToastSettings().sendMessage(msgCheck);

						// 获取版本号（VersionCode）
						int CurrentVersion = 0; // 版本号
						try
						{
							CurrentVersion = getPackageManager().getPackageInfo("com.galapk.litelisten", 0).versionCode;
						}
						catch (Exception e)
						{
							if (e.getMessage() != null)
								Log.w(Common.LOGCAT_TAG, e.getMessage());
							else
								e.printStackTrace();
						}

						// 检查更新
						String RemoteVersion = Common.CheckForUpdate(CurrentVersion);
						if (RemoteVersion != null && !RemoteVersion.equals(""))
						{
							final MessageDialog md = new MessageDialog();
							md.SetMessage(scrSettings.this, Language, UseAnimation, layActivity, getString(R.string.pfrscat_others_check_for_update_got_title),
									getString(R.string.pfrscat_others_check_for_update_got_message1) + RemoteVersion + getString(R.string.pfrscat_others_check_for_update_got_message2), 18,
									new OnClickListener()
									{
										public void onClick(View v)
										{
											new Thread()
											{
												public void run()
												{
													// 写入统计日志
													TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // 获取手机串号等信息并发送

													// 获取当前时间
													java.util.Date date = new java.util.Date();
													SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
													String strDateTime = sdf.format(date);

													int VersionCode = 0;

													try
													{
														PackageManager pkgMgr = getPackageManager();
														PackageInfo pkgInfo = pkgMgr.getPackageInfo(getPackageName(), 0);
														VersionCode = pkgInfo.versionCode;
													}
													catch (Exception e)
													{
														if (e.getMessage() != null)
															Log.w(Common.LOGCAT_TAG, e.getMessage());
														else
															e.printStackTrace();
													}

													// 生成链接
													String strURL = "http://www.littledai.com/LiteListen/SetDevInfo.php?imei={imei}&locale={locale}&sdk={sdk}&release={release}&model={model}&action={action}&install_version={install_version}&update_time={update_time}";
													strURL = strURL.replace("{imei}", java.net.URLEncoder.encode(tm.getDeviceId())).replace("{locale}",
															java.net.URLEncoder.encode(getResources().getConfiguration().locale.toString())).replace("{sdk}",
															java.net.URLEncoder.encode(Build.VERSION.SDK)).replace("{release}", java.net.URLEncoder.encode(Build.VERSION.RELEASE)).replace("{model}",
															java.net.URLEncoder.encode(Build.MODEL)).replace("{action}", java.net.URLEncoder.encode("Update")).replace("{install_version}",
															java.net.URLEncoder.encode(String.valueOf(VersionCode))).replace("{update_time}", java.net.URLEncoder.encode(strDateTime));

													Common.CallURLPost(strURL, 10000);

													// 开始下载更新文件
													File TempFile = Common.GetUpdate();
													if (TempFile != null)
													{// 文件下载完成
														Intent intent = new Intent();
														intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
														intent.setAction(android.content.Intent.ACTION_VIEW);
														intent.setDataAndType(Uri.fromFile(TempFile), Common.GetMIMEType(TempFile));
														startActivity(intent);
													}
													else
													{// 未完成，给出提示
														final MessageDialog md = new MessageDialog();
														md.SetMessage(scrSettings.this, Language, UseAnimation, layActivity, getString(R.string.pfrscat_others_check_for_update_got_title),
																getString(R.string.pfrscat_others_check_for_update_got_failed), 18, new OnClickListener()
																{
																	public void onClick(View v)
																	{
																		md.CloseDialog();
																	}
																}, null);

														// 显示对话框
														Message msg = new Message();
														msg.obj = md;
														hs.getHdlShowMessageDialog().sendMessage(msg);
													}
												}
											}.start();

											// 显示浮动提示
											Message msgDown = new Message();
											msgDown.obj = getString(R.string.pfrscat_others_check_for_update_downloading);
											hs.getHdlShowToastSettings().sendMessage(msgDown);

											md.CloseDialog();
										}
									}, new OnClickListener()
									{
										public void onClick(View v)
										{
											md.CloseDialog();
										}
									});

							// 显示对话框
							Message msg = new Message();
							msg.obj = md;
							hs.getHdlShowMessageDialog().sendMessage(msg);
						}
						else
						{
							final MessageDialog md = new MessageDialog();
							md.SetMessage(scrSettings.this, Language, UseAnimation, layActivity, getString(R.string.pfrscat_others_check_for_update_nothing_title),
									getString(R.string.pfrscat_others_check_for_update_nothing_message), 18, new OnClickListener()
									{
										public void onClick(View v)
										{
											md.CloseDialog();
										}
									}, null);

							// 显示对话框
							Message msg = new Message();
							msg.obj = md;
							hs.getHdlShowMessageDialog().sendMessage(msg);
						}
					}
				}.start();
			}
		});

		btnRestore.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_others_restore, R.string.pfrscat_others_restore_title, 18, "", 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						// 设置所有选项变量为默认值
						Restore = TextDialog.getEdtMessage().getText().toString();
						if (Restore.equals("是") || Restore.toLowerCase().equals("yes"))
						{
							Language = "3";

							MusicPath = "/sdcard";
							IncludeSubDirectory = true;
							IgnoreSize = "1024";
							IgnoreDirectory = true;

							AutoPause = true;

							LRCAutoDownload = false;

							ListSortOrder = "1";
							AutoSwitchToLRC = true;
							PlayMode = "1";
							NotifyAction = "0";
							FavoriteMax = "30";

							ScrollMode = "0";
							BackgroundPort = "0";
							BackgroundLand = "0";
							BackgroundBrightness = "75";
							BackgroundBlur = true;

							UseAnimation = true;

							ListFontSize = "18.0";
							ListFontColor = "#FFFFFF";
							ListFontShadow = true;
							ListFontShadowColor = "#000000";

							LRCFontSize = "18.0";
							LRCFontColorNormal = "#FFFFFF";
							LRCFontColorHighlight = "#FFFF00";
							LRCFontShadow = true;
							LRCFontShadowColor = "#9BD7FF";

							Restore = "";

							// 非选项菜单中的选项
							Editor edt = sp.edit();
							edt.putString("MusicControl", "3");
							edt.putBoolean("FloatLRCLocked", false);
							edt.putBoolean("DeskLRCStatus", true);
							edt.putString("LastKeyword", "");
							edt.putString("OrderBy", "asc");
							edt.putBoolean("KeepScreenOn", false);
							edt.putInt("FloatLRCPos", 0);
							edt.putBoolean("Started", true);
							edt.putBoolean("IsRunBackground", false);
							edt.putInt("ScreenOrantation", 0);
						}
						else
						{
							final MessageDialog md = new MessageDialog();
							md.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_others_restore, R.string.pfrscat_others_restore_message_wrong, 18,
									new OnClickListener()
									{
										public void onClick(View v)
										{
											md.CloseDialog();
										}
									}, null);
							Restore = "";
						}

						GetButtonDisplay();
						UpdatePreference();
						TextDialog.getPw().dismiss();

						final MessageDialog md = new MessageDialog();
						md.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_others_restore, R.string.pfrscat_others_restore_message_ok, 18, new OnClickListener()
						{
							public void onClick(View v)
							{
								md.CloseDialog();
							}
						}, null);
					}
				});
			}
		});

		btnGoHome.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				final MessageDialog md = new MessageDialog();
				md.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.global_request, R.string.pfrscat_help_visit_official_site_message, 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("http://www.littledai.com/category/litelisten"));
						startActivity(i);
						md.CloseDialog();
					}
				}, new OnClickListener()
				{
					public void onClick(View v)
					{
						md.CloseDialog();
					}
				});
			}
		});

		btnAbout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				final MessageDialog md = new MessageDialog();
				md.ShowMessage(scrSettings.this, Language, UseAnimation, layActivity, R.string.pfrscat_help_about, R.string.pfrscat_help_about_message, 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						md.CloseDialog();
					}
				}, null);
			}
		});
	}

	/* 获取控件实例 */
	public void FindViews()
	{
		layActivity = (RelativeLayout) findViewById(R.id.layActivity);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		layCategory = (LinearLayout) findViewById(R.id.layCategory);
		btnGeneral = (Button) findViewById(R.id.btnGeneral);
		btnDisplay = (Button) findViewById(R.id.btnDisplay);
		btnOthers = (Button) findViewById(R.id.btnOthers);
		btnHelp = (Button) findViewById(R.id.btnHelp);
		layCore = (LinearLayout) findViewById(R.id.layCore);
		scrGeneral = (ScrollView) findViewById(R.id.scrGeneral);
		layGeneral = (LinearLayout) findViewById(R.id.layGeneral);
		btnLanguage = (Button) findViewById(R.id.btnLanguage);
		btnMusicPath = (Button) findViewById(R.id.btnMusicPath);
		chkIncludeSubDirectory = (CheckBox) findViewById(R.id.chkIncludeSubDirectory);
		btnIgnoreSize = (Button) findViewById(R.id.btnIgnoreSize);
		chkIgnoreDirectory = (CheckBox) findViewById(R.id.chkIgnoreDirectory);
		chkAutoPause = (CheckBox) findViewById(R.id.chkAutoPause);
		chkLRCAutoDownload = (CheckBox) findViewById(R.id.chkLRCAutoDownload);
		btnListSortOrder = (Button) findViewById(R.id.btnListSortOrder);
		chkAutoSwitchToLRC = (CheckBox) findViewById(R.id.chkAutoSwitchToLRC);
		btnPlayMode = (Button) findViewById(R.id.btnPlayMode);
		btnNotifyAction = (Button) findViewById(R.id.btnNotifyAction);
		btnFavoriteMax = (Button) findViewById(R.id.btnFavoriteMax);
		scrDisplay = (ScrollView) findViewById(R.id.scrDisplay);
		layDisplay = (LinearLayout) findViewById(R.id.layDisplay);
		btnScrollMode = (Button) findViewById(R.id.btnScrollMode);
		btnBackgroundPort = (Button) findViewById(R.id.btnBackgroundPort);
		btnBackgroundLand = (Button) findViewById(R.id.btnBackgroundLand);
		btnBackgroundBrightness = (Button) findViewById(R.id.btnBackgroundBrightness);
		chkBackgroundBlur = (CheckBox) findViewById(R.id.chkBackgroundBlur);
		chkUseAnimation = (CheckBox) findViewById(R.id.chkUseAnimation);
		btnListFontSize = (Button) findViewById(R.id.btnListFontSize);
		btnListFontColor = (Button) findViewById(R.id.btnListFontColor);
		chkListFontShadow = (CheckBox) findViewById(R.id.chkListFontShadow);
		btnListFontShadowColor = (Button) findViewById(R.id.btnListFontShadowColor);
		btnLRCFontSize = (Button) findViewById(R.id.btnLRCFontSize);
		btnLRCFontColorNormal = (Button) findViewById(R.id.btnLRCFontColorNormal);
		btnLRCFontColorHighlight = (Button) findViewById(R.id.btnLRCFontColorHighlight);
		chkLRCFontShadow = (CheckBox) findViewById(R.id.chkLRCFontShadow);
		btnLRCFontShadowColor = (Button) findViewById(R.id.btnLRCFontShadowColor);
		scrOthers = (ScrollView) findViewById(R.id.scrOthers);
		layOthers = (LinearLayout) findViewById(R.id.layOthers);
		btnHowToCheckForUpdate = (Button) findViewById(R.id.btnHowToCheckForUpdate);
		btnCheckForUpdate = (Button) findViewById(R.id.btnCheckForUpdate);
		btnRestore = (Button) findViewById(R.id.btnRestore);
		scrHelp = (ScrollView) findViewById(R.id.scrHelp);
		layHelp = (LinearLayout) findViewById(R.id.layHelp);
		btnGoHome = (Button) findViewById(R.id.btnGoHome);
		btnAbout = (Button) findViewById(R.id.btnAbout);
		layOkCancel = (LinearLayout) findViewById(R.id.layOkCancel);
		btnReturn = (Button) findViewById(R.id.btnReturn);
	}

	/* 按键动作 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
			ClosePreference();
		return true;
	}

	public RelativeLayout getLayActivity()
	{
		return layActivity;
	}

	public void setLayActivity(RelativeLayout layActivity)
	{
		this.layActivity = layActivity;
	}

	public TextView getTxtTitle()
	{
		return txtTitle;
	}

	public void setTxtTitle(TextView txtTitle)
	{
		this.txtTitle = txtTitle;
	}

	public LinearLayout getLayCategory()
	{
		return layCategory;
	}

	public void setLayCategory(LinearLayout layCategory)
	{
		this.layCategory = layCategory;
	}

	public Button getBtnGeneral()
	{
		return btnGeneral;
	}

	public void setBtnGeneral(Button btnGeneral)
	{
		this.btnGeneral = btnGeneral;
	}

	public Button getBtnDisplay()
	{
		return btnDisplay;
	}

	public void setBtnDisplay(Button btnDisplay)
	{
		this.btnDisplay = btnDisplay;
	}

	public Button getBtnOthers()
	{
		return btnOthers;
	}

	public void setBtnOthers(Button btnOthers)
	{
		this.btnOthers = btnOthers;
	}

	public Button getBtnHelp()
	{
		return btnHelp;
	}

	public void setBtnHelp(Button btnHelp)
	{
		this.btnHelp = btnHelp;
	}

	public LinearLayout getLayCore()
	{
		return layCore;
	}

	public void setLayCore(LinearLayout layCore)
	{
		this.layCore = layCore;
	}

	public ScrollView getScrGeneral()
	{
		return scrGeneral;
	}

	public void setScrGeneral(ScrollView scrGeneral)
	{
		this.scrGeneral = scrGeneral;
	}

	public LinearLayout getLayGeneral()
	{
		return layGeneral;
	}

	public void setLayGeneral(LinearLayout layGeneral)
	{
		this.layGeneral = layGeneral;
	}

	public Button getBtnLanguage()
	{
		return btnLanguage;
	}

	public void setBtnLanguage(Button btnLanguage)
	{
		this.btnLanguage = btnLanguage;
	}

	public Button getBtnMusicPath()
	{
		return btnMusicPath;
	}

	public void setBtnMusicPath(Button btnMusicPath)
	{
		this.btnMusicPath = btnMusicPath;
	}

	public CheckBox getChkIncludeSubDirectory()
	{
		return chkIncludeSubDirectory;
	}

	public void setChkIncludeSubDirectory(CheckBox chkIncludeSubDirectory)
	{
		this.chkIncludeSubDirectory = chkIncludeSubDirectory;
	}

	public CheckBox getChkIgnoreDirectory()
	{
		return chkIgnoreDirectory;
	}

	public void setChkIgnoreDirectory(CheckBox chkIgnoreDirectory)
	{
		this.chkIgnoreDirectory = chkIgnoreDirectory;
	}

	public Button getBtnListSortOrder()
	{
		return btnListSortOrder;
	}

	public void setBtnListSortOrder(Button btnListSortOrder)
	{
		this.btnListSortOrder = btnListSortOrder;
	}

	public CheckBox getChkAutoSwitchToLRC()
	{
		return chkAutoSwitchToLRC;
	}

	public void setChkAutoSwitchToLRC(CheckBox chkAutoSwitchToLRC)
	{
		this.chkAutoSwitchToLRC = chkAutoSwitchToLRC;
	}

	public Button getBtnPlayMode()
	{
		return btnPlayMode;
	}

	public void setBtnPlayMode(Button btnPlayMode)
	{
		this.btnPlayMode = btnPlayMode;
	}

	public Button getBtnNotifyAction()
	{
		return btnNotifyAction;
	}

	public void setBtnNotifyAction(Button btnNotifyAction)
	{
		this.btnNotifyAction = btnNotifyAction;
	}

	public Button getBtnFavoriteMax()
	{
		return btnFavoriteMax;
	}

	public void setBtnFavoriteMax(Button btnFavoriteMax)
	{
		this.btnFavoriteMax = btnFavoriteMax;
	}

	public ScrollView getScrDisplay()
	{
		return scrDisplay;
	}

	public void setScrDisplay(ScrollView scrDisplay)
	{
		this.scrDisplay = scrDisplay;
	}

	public LinearLayout getLayDisplay()
	{
		return layDisplay;
	}

	public void setLayDisplay(LinearLayout layDisplay)
	{
		this.layDisplay = layDisplay;
	}

	public Button getBtnScrollMode()
	{
		return btnScrollMode;
	}

	public void setBtnScrollMode(Button btnScrollMode)
	{
		this.btnScrollMode = btnScrollMode;
	}

	public Button getBtnBackgroundPort()
	{
		return btnBackgroundPort;
	}

	public void setBtnBackgroundPort(Button btnBackgroundPort)
	{
		this.btnBackgroundPort = btnBackgroundPort;
	}

	public Button getBtnBackgroundLand()
	{
		return btnBackgroundLand;
	}

	public void setBtnBackgroundLand(Button btnBackgroundLand)
	{
		this.btnBackgroundLand = btnBackgroundLand;
	}

	public Button getBtnBackgroundBrightness()
	{
		return btnBackgroundBrightness;
	}

	public void setBtnBackgroundBrightness(Button btnBackgroundBrightness)
	{
		this.btnBackgroundBrightness = btnBackgroundBrightness;
	}

	public CheckBox getChkBackgroundBlur()
	{
		return chkBackgroundBlur;
	}

	public void setChkBackgroundBlur(CheckBox chkBackgroundBlur)
	{
		this.chkBackgroundBlur = chkBackgroundBlur;
	}

	public CheckBox getChkUseAnimation()
	{
		return chkUseAnimation;
	}

	public void setChkUseAnimation(CheckBox chkUseAnimation)
	{
		this.chkUseAnimation = chkUseAnimation;
	}

	public Button getBtnListFontSize()
	{
		return btnListFontSize;
	}

	public void setBtnListFontSize(Button btnListFontSize)
	{
		this.btnListFontSize = btnListFontSize;
	}

	public Button getBtnListFontColor()
	{
		return btnListFontColor;
	}

	public void setBtnListFontColor(Button btnListFontColor)
	{
		this.btnListFontColor = btnListFontColor;
	}

	public CheckBox getChkListFontShadow()
	{
		return chkListFontShadow;
	}

	public void setChkListFontShadow(CheckBox chkListFontShadow)
	{
		this.chkListFontShadow = chkListFontShadow;
	}

	public Button getBtnListFontShadowColor()
	{
		return btnListFontShadowColor;
	}

	public void setBtnListFontShadowColor(Button btnListFontShadowColor)
	{
		this.btnListFontShadowColor = btnListFontShadowColor;
	}

	public Button getBtnLRCFontSize()
	{
		return btnLRCFontSize;
	}

	public void setBtnLRCFontSize(Button btnLRCFontSize)
	{
		this.btnLRCFontSize = btnLRCFontSize;
	}

	public Button getBtnLRCFontColorNormal()
	{
		return btnLRCFontColorNormal;
	}

	public void setBtnLRCFontColorNormal(Button btnLRCFontColorNormal)
	{
		this.btnLRCFontColorNormal = btnLRCFontColorNormal;
	}

	public Button getBtnLRCFontColorHighlight()
	{
		return btnLRCFontColorHighlight;
	}

	public void setBtnLRCFontColorHighlight(Button btnLRCFontColorHighlight)
	{
		this.btnLRCFontColorHighlight = btnLRCFontColorHighlight;
	}

	public CheckBox getChkLRCFontShadow()
	{
		return chkLRCFontShadow;
	}

	public void setChkLRCFontShadow(CheckBox chkLRCFontShadow)
	{
		this.chkLRCFontShadow = chkLRCFontShadow;
	}

	public Button getBtnLRCFontShadowColor()
	{
		return btnLRCFontShadowColor;
	}

	public void setBtnLRCFontShadowColor(Button btnLRCFontShadowColor)
	{
		this.btnLRCFontShadowColor = btnLRCFontShadowColor;
	}

	public ScrollView getScrOthers()
	{
		return scrOthers;
	}

	public void setScrOthers(ScrollView scrOthers)
	{
		this.scrOthers = scrOthers;
	}

	public LinearLayout getLayOthers()
	{
		return layOthers;
	}

	public void setLayOthers(LinearLayout layOthers)
	{
		this.layOthers = layOthers;
	}

	public Button getBtnRestore()
	{
		return btnRestore;
	}

	public void setBtnRestore(Button btnRestore)
	{
		this.btnRestore = btnRestore;
	}

	public ScrollView getScrHelp()
	{
		return scrHelp;
	}

	public void setScrHelp(ScrollView scrHelp)
	{
		this.scrHelp = scrHelp;
	}

	public LinearLayout getLayHelp()
	{
		return layHelp;
	}

	public void setLayHelp(LinearLayout layHelp)
	{
		this.layHelp = layHelp;
	}

	public Button getBtnGoHome()
	{
		return btnGoHome;
	}

	public void setBtnGoHome(Button btnGoHome)
	{
		this.btnGoHome = btnGoHome;
	}

	public Button getBtnAbout()
	{
		return btnAbout;
	}

	public void setBtnAbout(Button btnAbout)
	{
		this.btnAbout = btnAbout;
	}

	public LinearLayout getLayOkCancel()
	{
		return layOkCancel;
	}

	public void setLayOkCancel(LinearLayout layOkCancel)
	{
		this.layOkCancel = layOkCancel;
	}

	public SharedPreferences getSp()
	{
		return sp;
	}

	public void setSp(SharedPreferences sp)
	{
		this.sp = sp;
	}

	public String getLanguage()
	{
		return Language;
	}

	public void setLanguage(String language)
	{
		Language = language;
	}

	public String getMusicPath()
	{
		return MusicPath;
	}

	public void setMusicPath(String musicPath)
	{
		MusicPath = musicPath;
	}

	public Boolean getIncludeSubDirectory()
	{
		return IncludeSubDirectory;
	}

	public void setIncludeSubDirectories(Boolean includeSubDirectory)
	{
		IncludeSubDirectory = includeSubDirectory;
	}

	public Boolean getIgnoreDirectory()
	{
		return IgnoreDirectory;
	}

	public void setIgnoreDirectory(Boolean ignoreDirectory)
	{
		IgnoreDirectory = ignoreDirectory;
	}

	public String getListSortOrder()
	{
		return ListSortOrder;
	}

	public void setListSortOrder(String listSortOrder)
	{
		ListSortOrder = listSortOrder;
	}

	public Boolean getAutoSwitchToLRC()
	{
		return AutoSwitchToLRC;
	}

	public void setAutoSwitchToLRC(Boolean autoSwitchToLRC)
	{
		AutoSwitchToLRC = autoSwitchToLRC;
	}

	public String getPlayMode()
	{
		return PlayMode;
	}

	public void setPlayMode(String playMode)
	{
		PlayMode = playMode;
	}

	public String getNotifyAction()
	{
		return NotifyAction;
	}

	public void setNotifyAction(String notifyAction)
	{
		NotifyAction = notifyAction;
	}

	public String getFavoriteMax()
	{
		return FavoriteMax;
	}

	public void setFavoriteMax(String favoriteMax)
	{
		FavoriteMax = favoriteMax;
	}

	public String getScrollMode()
	{
		return ScrollMode;
	}

	public void setScrollMode(String scrollMode)
	{
		ScrollMode = scrollMode;
	}

	public String getBackgroundPort()
	{
		return BackgroundPort;
	}

	public void setBackgroundPort(String backgroundPort)
	{
		BackgroundPort = backgroundPort;
	}

	public String getBackgroundLand()
	{
		return BackgroundLand;
	}

	public void setBackgroundLand(String backgroundLand)
	{
		BackgroundLand = backgroundLand;
	}

	public String getBackgroundBrightness()
	{
		return BackgroundBrightness;
	}

	public void setBackgroundBrightness(String backgroundBrightness)
	{
		BackgroundBrightness = backgroundBrightness;
	}

	public Boolean getBackgroundBlur()
	{
		return BackgroundBlur;
	}

	public void setBackgroundBlur(Boolean backgroundBlur)
	{
		BackgroundBlur = backgroundBlur;
	}

	public Boolean getUseAnimation()
	{
		return UseAnimation;
	}

	public void setUseAnimation(Boolean useAnimation)
	{
		UseAnimation = useAnimation;
	}

	public String getListFontSize()
	{
		return ListFontSize;
	}

	public void setListFontSize(String listFontSize)
	{
		ListFontSize = listFontSize;
	}

	public String getListFontColor()
	{
		return ListFontColor;
	}

	public void setListFontColor(String listFontColor)
	{
		ListFontColor = listFontColor;
	}

	public Boolean getListFontShadow()
	{
		return ListFontShadow;
	}

	public void setListFontShadow(Boolean listFontShadow)
	{
		ListFontShadow = listFontShadow;
	}

	public String getListFontShadowColor()
	{
		return ListFontShadowColor;
	}

	public void setListFontShadowColor(String listFontShadowColor)
	{
		ListFontShadowColor = listFontShadowColor;
	}

	public String getLRCFontSize()
	{
		return LRCFontSize;
	}

	public void setLRCFontSize(String lRCFontSize)
	{
		LRCFontSize = lRCFontSize;
	}

	public String getLRCFontColorNormal()
	{
		return LRCFontColorNormal;
	}

	public void setLRCFontColorNormal(String lRCFontColorNormal)
	{
		LRCFontColorNormal = lRCFontColorNormal;
	}

	public String getLRCFontColorHighlight()
	{
		return LRCFontColorHighlight;
	}

	public void setLRCFontColorHighlight(String lRCFontColorHighlight)
	{
		LRCFontColorHighlight = lRCFontColorHighlight;
	}

	public Boolean getLRCFontShadow()
	{
		return LRCFontShadow;
	}

	public void setLRCFontShadow(Boolean lRCFontShadow)
	{
		LRCFontShadow = lRCFontShadow;
	}

	public String getLRCFontShadowColor()
	{
		return LRCFontShadowColor;
	}

	public void setLRCFontShadowColor(String lRCFontShadowColor)
	{
		LRCFontShadowColor = lRCFontShadowColor;
	}

	public String getRestore()
	{
		return Restore;
	}

	public void setRestore(String restore)
	{
		Restore = restore;
	}

	public CheckBox getChkAutoPause()
	{
		return chkAutoPause;
	}

	public void setChkAutoPause(CheckBox chkAutoPause)
	{
		this.chkAutoPause = chkAutoPause;
	}

	public Boolean getAutoPause()
	{
		return AutoPause;
	}

	public void setAutoPause(Boolean autoPause)
	{
		AutoPause = autoPause;
	}

	public void setIncludeSubDirectory(Boolean includeSubDirectory)
	{
		IncludeSubDirectory = includeSubDirectory;
	}

	public Button getBtnReturn()
	{
		return btnReturn;
	}

	public void setBtnReturn(Button btnReturn)
	{
		this.btnReturn = btnReturn;
	}

	public int getIMAGE_SELECTED_PORT()
	{
		return IMAGE_SELECTED_PORT;
	}

	public void setIMAGE_SELECTED_PORT(int iMAGESELECTEDPORT)
	{
		IMAGE_SELECTED_PORT = iMAGESELECTEDPORT;
	}

	public int getIMAGE_SELECTED_LAND()
	{
		return IMAGE_SELECTED_LAND;
	}

	public void setIMAGE_SELECTED_LAND(int iMAGESELECTEDLAND)
	{
		IMAGE_SELECTED_LAND = iMAGESELECTEDLAND;
	}

	public int getScreenOrantation()
	{
		return ScreenOrantation;
	}

	public void setScreenOrantation(int screenOrantation)
	{
		ScreenOrantation = screenOrantation;
	}

	public CheckBox getChkLRCAutoDownload()
	{
		return chkLRCAutoDownload;
	}

	public void setChkLRCAutoDownload(CheckBox chkLRCAutoDownload)
	{
		this.chkLRCAutoDownload = chkLRCAutoDownload;
	}

	public Boolean getLRCAutoDownload()
	{
		return LRCAutoDownload;
	}

	public void setLRCAutoDownload(Boolean lRCAutoDownload)
	{
		LRCAutoDownload = lRCAutoDownload;
	}

	public Button getBtnHowToCheckForUpdate()
	{
		return btnHowToCheckForUpdate;
	}

	public void setBtnHowToCheckForUpdate(Button btnHowToCheckForUpdate)
	{
		this.btnHowToCheckForUpdate = btnHowToCheckForUpdate;
	}

	public Button getBtnCheckForUpdate()
	{
		return btnCheckForUpdate;
	}

	public void setBtnCheckForUpdate(Button btnCheckForUpdate)
	{
		this.btnCheckForUpdate = btnCheckForUpdate;
	}

	public String getHowToCheckForUpdate()
	{
		return HowToCheckForUpdate;
	}

	public void setHowToCheckForUpdate(String howToCheckForUpdate)
	{
		HowToCheckForUpdate = howToCheckForUpdate;
	}

	public HandlerService getHs()
	{
		return hs;
	}

	public void setHs(HandlerService hs)
	{
		this.hs = hs;
	}

	public int getSelectedFileIndex()
	{
		return SelectedFileIndex;
	}

	public void setSelectedFileIndex(int selectedFileIndex)
	{
		SelectedFileIndex = selectedFileIndex;
	}

	public List<Map<String, String>> getLstLRCFile()
	{
		return lstLRCFile;
	}

	public void setLstLRCFile(List<Map<String, String>> lstLRCFile)
	{
		this.lstLRCFile = lstLRCFile;
	}

	public Button getBtnIgnoreSize()
	{
		return btnIgnoreSize;
	}

	public void setBtnIgnoreSize(Button btnIgnoreSize)
	{
		this.btnIgnoreSize = btnIgnoreSize;
	}

	public String getIgnoreSize()
	{
		return IgnoreSize;
	}

	public void setIgnoreSize(String ignoreSize)
	{
		IgnoreSize = ignoreSize;
	}

	public FileAdapterForSettings getFa()
	{
		return fa;
	}

	public void setFa(FileAdapterForSettings fa)
	{
		this.fa = fa;
	}
}