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
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class SettingService extends PreferenceActivity
{
	private ColorDialog dlgColor = null;
	private TextDialog dlgText = null;
	private MessageDialog dlgAlert = null;
	private SharedPreferences sp = null;
	private int ScreenOrientation = 0;
	private int IMAGE_SELECTED_PORT = 0; // 竖屏照片选择标志
	private int IMAGE_SELECTED_LAND = 1; // 横屏照片选择标志

	/* 定义选项列表控件 */
	private ListPreference lstLanguage;
	private EditTextPreference txtMusicPath;
	private ListPreference lstConvertChineseLRC;
	private ListPreference lstFitScreenOrientation;
	private ListPreference lstLRCScrollStyle;
	private ListPreference lstListOrder;
	private ListPreference lstPropertyReadPriority;
	private ListPreference lstPlayMode;
	private ListPreference lstBackgroundPort;
	private ListPreference lstBackgroundLand;
	private ListPreference lstNotifyNext;
	private EditTextPreference txtBackgroundBrightness;
	private EditTextPreference txtFavouriteMax;
	private CheckBoxPreference chkBackgroundBlur;
	private CheckBoxPreference chkUseAnimation;
	private EditTextPreference txtListFontSize;
	private Preference btnListFontColor;
	private CheckBoxPreference chkListFontShadow;
	private Preference btnListFontShadowColor;
	private EditTextPreference txtLRCFontSize;
	private Preference btnLRCNormalFontColor;
	private Preference btnLRCHighlightlFontColor;
	private CheckBoxPreference chkLRCFontShadow;
	private Preference btnLRCFontShadowColor;
	private EditTextPreference txtRestore;
	private Preference btnVisitOfficialSite;
	private Preference btnAbout;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		dlgColor = new ColorDialog(this);
		dlgText = new TextDialog(this);
		dlgAlert = new MessageDialog(this);
		sp = getPreferences(Context.MODE_PRIVATE); // 读取配置文件
		ScreenOrientation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation();

		String index = sp.getString("lstLanguage", "0");
		Configuration config = getResources().getConfiguration(); // 获得设置对象

		if (index.equals("0"))
			config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
		else if (index.equals("1"))
			config.locale = Locale.TRADITIONAL_CHINESE; // 繁体中文
		else if (index.equals("2"))
			config.locale = Locale.US; // 美式英语

		getResources().updateConfiguration(config, null);

		FindPreference();
		ListenerBinding();
		RefreshSummary(null, null);
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

	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		ScreenOrientation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
		dlgAlert.ChangeLayout();
		dlgColor.ChangeLayout();
		dlgText.ChangeLayout();
	}

	/* 绑定控件实体 */
	private void FindPreference()
	{
		lstLanguage = (ListPreference) findPreference("lstLanguage");
		txtMusicPath = (EditTextPreference) findPreference("txtMusicPath");
		lstConvertChineseLRC = (ListPreference) findPreference("lstConvertChineseLRC");
		lstFitScreenOrientation = (ListPreference) findPreference("lstFitScreenOrientation");
		lstLRCScrollStyle = (ListPreference) findPreference("lstLRCScrollStyle");
		lstListOrder = (ListPreference) findPreference("lstListOrder");
		lstPropertyReadPriority = (ListPreference) findPreference("lstPropertyReadPriority");
		lstPlayMode = (ListPreference) findPreference("lstPlayMode");
		lstBackgroundPort = (ListPreference) findPreference("lstBackgroundPort");
		lstBackgroundLand = (ListPreference) findPreference("lstBackgroundLand");
		lstNotifyNext = (ListPreference) findPreference("lstNotifyNext");
		txtBackgroundBrightness = (EditTextPreference) findPreference("txtBackgroundBrightness");
		txtFavouriteMax = (EditTextPreference) findPreference("txtFavouriteMax");
		chkBackgroundBlur = (CheckBoxPreference) findPreference("chkBackgroundBlur");
		chkUseAnimation = (CheckBoxPreference) findPreference("chkUseAnimation");
		txtListFontSize = (EditTextPreference) findPreference("txtListFontSize");
		btnListFontColor = (Preference) findPreference("btnListFontColor");
		chkListFontShadow = (CheckBoxPreference) findPreference("chkListFontShadow");
		btnListFontShadowColor = (Preference) findPreference("btnListFontShadowColor");
		txtLRCFontSize = (EditTextPreference) findPreference("txtLRCFontSize");
		btnLRCNormalFontColor = (Preference) findPreference("btnLRCNormalFontColor");
		btnLRCHighlightlFontColor = (Preference) findPreference("btnLRCHighlightlFontColor");
		chkLRCFontShadow = (CheckBoxPreference) findPreference("chkLRCFontShadow");
		btnLRCFontShadowColor = (Preference) findPreference("btnLRCFontShadowColor");
		txtRestore = (EditTextPreference) findPreference("txtRestore");
		btnVisitOfficialSite = (Preference) findPreference("btnVisitOfficialSite");
		btnAbout = (Preference) findPreference("btnAbout");
	}

	/* 绑定控件事件 */
	private void ListenerBinding()
	{

		btnListFontColor.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				if (ScreenOrientation == 1 || ScreenOrientation == 3)
					dlgColor.ShowDialog("btnListFontColor", 0.65);
				else
					dlgColor.ShowDialog("btnListFontColor", 1);

				return false;
			}
		});

		btnListFontShadowColor.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				if (ScreenOrientation == 1 || ScreenOrientation == 3)
					dlgColor.ShowDialog("btnListFontShadowColor", 0.65);
				else
					dlgColor.ShowDialog("btnListFontShadowColor", 1);

				return false;
			}
		});

		btnLRCNormalFontColor.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				if (ScreenOrientation == 1 || ScreenOrientation == 3)
					dlgColor.ShowDialog("btnLRCNormalFontColor", 0.65);
				else
					dlgColor.ShowDialog("btnLRCNormalFontColor", 1);

				return false;
			}
		});

		btnLRCHighlightlFontColor.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				if (ScreenOrientation == 1 || ScreenOrientation == 3)
					dlgColor.ShowDialog("btnLRCHighlightlFontColor", 0.65);
				else
					dlgColor.ShowDialog("btnLRCHighlightlFontColor", 1);

				return false;
			}
		});

		btnLRCFontShadowColor.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				if (ScreenOrientation == 1 || ScreenOrientation == 3)
					dlgColor.ShowDialog("btnLRCFontShadowColor", 0.65);
				else
					dlgColor.ShowDialog("btnLRCFontShadowColor", 1);

				return false;
			}
		});

		btnVisitOfficialSite.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				dlgAlert.ShowDialog(getString(R.string.global_request), getString(R.string.pfrscat_help_visit_official_site_message), new OnClickListener()
				{
					public void onClick(View v)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(getString(R.string.pfrscat_help_visit_official_site_summary)));
						startActivity(i);
						dlgAlert.CloseDialog();
					}
				}, new OnClickListener()
				{
					public void onClick(View v)
					{
						dlgAlert.CloseDialog();
					}
				});

				return false;
			}
		});

		btnAbout.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				dlgAlert.ShowDialog(getString(R.string.global_app_name_no_version), getString(R.string.pfrscat_help_about_message), new OnClickListener()
				{
					public void onClick(View v)
					{
						dlgAlert.CloseDialog();
					}
				}, null);

				return false;
			}
		});
	}

	/* 生成指定参数的背景图像 */
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

	/* 更新选项菜单提示信息 */
	private void RefreshSummary(Preference preference, Object Value)
	{
		if (preference != null)
		{
			// 语言选项
			if (preference.getKey().equals("lstLanguage"))
			{
				lstLanguage.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_language)[Integer.parseInt((String) Value)]);

				dlgAlert.ShowDialog(getString(R.string.pfrscat_general_language_title), getString(R.string.pfrscat_general_language_message), new OnClickListener()
				{
					public void onClick(View v)
					{
						System.exit(0);
						dlgAlert.CloseDialog();
					}
				}, new OnClickListener()
				{
					public void onClick(View v)
					{
						dlgAlert.CloseDialog();
					}
				});
			}

			// 竖屏背景
			if (preference.getKey().equals("lstBackgroundPort"))
			{
				lstBackgroundPort.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_display_background)[Integer.parseInt((String) Value)]);

				if (((String) Value).equals("1"))
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

			// 横屏背景
			if (preference.getKey().equals("lstBackgroundLand"))
			{
				lstBackgroundLand.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_display_background)[Integer.parseInt((String) Value)]);

				if (((String) Value).equals("1"))
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

			// 背景亮度
			if (preference.getKey().equals("txtBackgroundBrightness"))
			{
				int Brightness = Integer.parseInt((String) Value);

				if (Brightness > 100)
					Brightness = 100;
				if (Brightness < 0)
					Brightness = 0;

				Editor edt = sp.edit();
				edt.putString("txtBackgroundBrightness", String.valueOf(Brightness));
				edt.commit();

				MakeBackgroundImage();
				txtBackgroundBrightness.setSummary(String.valueOf(Brightness) + " %");
			}

			// 还原默认设置
			if (preference.getKey().equals("txtRestore"))
			{
				if ((((String) Value).equals("是") || ((String) Value).toLowerCase().equals("yes")))
				{
					Editor edt = sp.edit();
					edt.putString("lstNotifyNext", "0");
					edt.putString("txtFavouriteMax", "30");
					edt.putString("lstLRCScrollStyle", "0");
					edt.putString("lstFitScreenOrientation", "2");
					edt.putBoolean("chkIgnoreDirectory", true);
					edt.putString("lstPropertyReadPriority", "0");
					edt.putString("txtMusicPath", "/sdcard");
					edt.putString("lstBackgroundPort", "0");
					edt.putBoolean("chkListFontShadow", true);
					edt.putString("txtListFontSize", "18");
					edt.putBoolean("chkIncludeSubDirectories", true);
					edt.putString("lstConvertChineseLRC", "2");
					edt.putString("lstLanguage", "2");
					edt.putBoolean("chkBackgroundBlur", true);
					edt.putBoolean("chkLRCFontShadow", true);
					edt.putString("txtBackgroundBrightness", "75");
					edt.putBoolean("chkUseAnimation", true);
					edt.putString("txtLRCFontSize", "18");
					edt.putBoolean("chkRemeberLastPlayed", true);
					edt.putString("lstLanguage", "3");
					edt.putString("lstPlayMode", "1");
					edt.putString("lstBackgroundLand", "0");
					edt.putString("lstBackgroundPort", "0");
					edt.putString("lstListOrdder", "1");
					edt.putString("txtRestore", "");
					edt.commit();

					dlgAlert.ShowDialog(getString(R.string.pfrscat_others_restore), getString(R.string.pfrscat_others_restore_message_ok), new OnClickListener()
					{
						public void onClick(View v)
						{
							System.exit(0);
							dlgAlert.CloseDialog();
						}
					}, null);
				}
				else
				{
					dlgAlert.ShowDialog(getString(R.string.pfrscat_others_restore), getString(R.string.pfrscat_others_restore_message_wrong), new OnClickListener()
					{
						public void onClick(View v)
						{
							dlgAlert.CloseDialog();
						}
					}, null);
				}
			}

			if (preference.getKey().equals("txtMusicPath"))
				txtMusicPath.setSummary((String) Value);
			if (preference.getKey().equals("txtFavouriteMax"))
				txtFavouriteMax.setSummary((String) Value);
			if (preference.getKey().equals("lstConvertChineseLRC"))
				lstConvertChineseLRC.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_convert_chinese_lrc)[Integer.parseInt((String) Value)]);
			if (preference.getKey().equals("lstLRCScrollStyle"))
				lstLRCScrollStyle.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_display_lrc_scroll_style)[Integer.parseInt((String) Value)]);
			if (preference.getKey().equals("lstFitScreenOrientation"))
				lstFitScreenOrientation.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_fit_screen_orientation)[Integer.parseInt((String) Value)]);
			if (preference.getKey().equals("lstListOrder"))
				lstListOrder.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_list_order)[Integer.parseInt((String) Value)]);
			if (preference.getKey().equals("lstPropertyReadPriority"))
				lstPropertyReadPriority.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_property_read_priority)[Integer.parseInt((String) Value)]);
			if (preference.getKey().equals("lstNotifyNext"))
				lstNotifyNext.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_notify_next)[Integer.parseInt((String) Value)]);
			if (preference.getKey().equals("lstPlayMode"))
				lstPlayMode.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_play_mode)[Integer.parseInt((String) Value)]);
			if (preference.getKey().equals("txtListFontSize"))
				txtListFontSize.setSummary((String) Value + " dip");
			if (preference.getKey().equals("txtLRCFontSize"))
				txtLRCFontSize.setSummary((String) Value + " dip");
		}
		else
		{
			lstBackgroundPort.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_display_background)[Integer.parseInt(lstBackgroundPort.getValue())]);
			lstBackgroundLand.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_display_background)[Integer.parseInt(lstBackgroundLand.getValue())]);
			lstLanguage.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_language)[Integer.parseInt(lstLanguage.getValue())]);
			txtMusicPath.setSummary(txtMusicPath.getText());
			txtFavouriteMax.setSummary(txtFavouriteMax.getText());
			lstConvertChineseLRC.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_convert_chinese_lrc)[Integer.parseInt(lstConvertChineseLRC.getValue())]);
			lstLRCScrollStyle.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_display_lrc_scroll_style)[Integer.parseInt(lstLRCScrollStyle.getValue())]);
			lstFitScreenOrientation.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_fit_screen_orientation)[Integer.parseInt(lstFitScreenOrientation
					.getValue())]);
			lstListOrder.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_list_order)[Integer.parseInt(lstListOrder.getValue())]);
			lstPropertyReadPriority.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_property_read_priority)[Integer.parseInt(lstPropertyReadPriority
					.getValue())]);
			lstNotifyNext.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_notify_next)[Integer.parseInt(lstPropertyReadPriority.getValue())]);
			lstPlayMode.setSummary(getBaseContext().getResources().getStringArray(R.array.item_name_pfrscat_general_play_mode)[Integer.parseInt(lstPlayMode.getValue())]);
			txtListFontSize.setSummary(txtListFontSize.getText() + " dip");
			txtLRCFontSize.setSummary(txtLRCFontSize.getText() + " dip");
			txtBackgroundBrightness.setSummary(txtBackgroundBrightness.getText() + " %");

			// 获取按钮的值
			btnListFontColor.setSummary(sp.getString("btnListFontColor", "#FFFFFF"));
			btnListFontShadowColor.setSummary(sp.getString("btnListFontShadowColor", "#0099FF"));
			btnLRCNormalFontColor.setSummary(sp.getString("btnLRCNormalFontColor", "#FFFFFF"));
			btnLRCHighlightlFontColor.setSummary(sp.getString("btnLRCHighlightlFontColor", "#FFFF00"));
			btnLRCFontShadowColor.setSummary(sp.getString("btnLRCFontShadowColor", "#0099FF"));

			if (chkListFontShadow.isChecked())
				btnListFontShadowColor.setEnabled(true);
			else
				btnListFontShadowColor.setEnabled(false);

			if (chkLRCFontShadow.isChecked())
				btnLRCFontShadowColor.setEnabled(true);
			else
				btnLRCFontShadowColor.setEnabled(false);
		}
	}

	/* 设置更改时更新界面显示 */
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				RefreshSummary(preference, newValue);

				return true;
			}
		});

		if (preference.getKey().equals("chkBackgroundBlur"))
			MakeBackgroundImage();

		if (preference.getKey().equals("chkListFontShadow"))
		{
			if (chkListFontShadow.isChecked())
				btnListFontShadowColor.setEnabled(true);
			else
				btnListFontShadowColor.setEnabled(false);
		}

		if (preference.getKey().equals("chkLRCFontShadow"))
		{
			if (chkLRCFontShadow.isChecked())
				btnLRCFontShadowColor.setEnabled(true);
			else
				btnLRCFontShadowColor.setEnabled(false);
		}

		if (preference.getKey().equals("txtRestore"))
			txtRestore.setText("");

		return false;
	}

	public ColorDialog getDlgColor()
	{
		return dlgColor;
	}

	public void setDlgColor(ColorDialog dlgColor)
	{
		this.dlgColor = dlgColor;
	}

	public TextDialog getDlgText()
	{
		return dlgText;
	}

	public void setDlgText(TextDialog dlgText)
	{
		this.dlgText = dlgText;
	}

	public MessageDialog getDlgAlert()
	{
		return dlgAlert;
	}

	public void setDlgAlert(MessageDialog dlgAlert)
	{
		this.dlgAlert = dlgAlert;
	}

	public SharedPreferences getSp()
	{
		return sp;
	}

	public void setSp(SharedPreferences sp)
	{
		this.sp = sp;
	}

	public int getScreenOrientation()
	{
		return ScreenOrientation;
	}

	public void setScreenOrientation(int screenOrientation)
	{
		ScreenOrientation = screenOrientation;
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

	public ListPreference getLstLanguage()
	{
		return lstLanguage;
	}

	public void setLstLanguage(ListPreference lstLanguage)
	{
		this.lstLanguage = lstLanguage;
	}

	public EditTextPreference getTxtMusicPath()
	{
		return txtMusicPath;
	}

	public void setTxtMusicPath(EditTextPreference txtMusicPath)
	{
		this.txtMusicPath = txtMusicPath;
	}

	public ListPreference getLstConvertChineseLRC()
	{
		return lstConvertChineseLRC;
	}

	public void setLstConvertChineseLRC(ListPreference lstConvertChineseLRC)
	{
		this.lstConvertChineseLRC = lstConvertChineseLRC;
	}

	public ListPreference getLstFitScreenOrientation()
	{
		return lstFitScreenOrientation;
	}

	public void setLstFitScreenOrientation(ListPreference lstFitScreenOrientation)
	{
		this.lstFitScreenOrientation = lstFitScreenOrientation;
	}

	public ListPreference getLstListOrder()
	{
		return lstListOrder;
	}

	public void setLstListOrder(ListPreference lstListOrder)
	{
		this.lstListOrder = lstListOrder;
	}

	public ListPreference getLstPropertyReadPriority()
	{
		return lstPropertyReadPriority;
	}

	public void setLstPropertyReadPriority(ListPreference lstPropertyReadPriority)
	{
		this.lstPropertyReadPriority = lstPropertyReadPriority;
	}

	public ListPreference getLstPlayMode()
	{
		return lstPlayMode;
	}

	public void setLstPlayMode(ListPreference lstPlayMode)
	{
		this.lstPlayMode = lstPlayMode;
	}

	public ListPreference getLstBackgroundPort()
	{
		return lstBackgroundPort;
	}

	public void setLstBackgroundPort(ListPreference lstBackgroundPort)
	{
		this.lstBackgroundPort = lstBackgroundPort;
	}

	public ListPreference getLstBackgroundLand()
	{
		return lstBackgroundLand;
	}

	public void setLstBackgroundLand(ListPreference lstBackgroundLand)
	{
		this.lstBackgroundLand = lstBackgroundLand;
	}

	public EditTextPreference getTxtBackgroundBrightness()
	{
		return txtBackgroundBrightness;
	}

	public void setTxtBackgroundBrightness(EditTextPreference txtBackgroundBrightness)
	{
		this.txtBackgroundBrightness = txtBackgroundBrightness;
	}

	public CheckBoxPreference getChkBackgroundBlur()
	{
		return chkBackgroundBlur;
	}

	public void setChkBackgroundBlur(CheckBoxPreference chkBackgroundBlur)
	{
		this.chkBackgroundBlur = chkBackgroundBlur;
	}

	public CheckBoxPreference getChkUseAnimation()
	{
		return chkUseAnimation;
	}

	public void setChkUseAnimation(CheckBoxPreference chkUseAnimation)
	{
		this.chkUseAnimation = chkUseAnimation;
	}

	public EditTextPreference getTxtListFontSize()
	{
		return txtListFontSize;
	}

	public void setTxtListFontSize(EditTextPreference txtListFontSize)
	{
		this.txtListFontSize = txtListFontSize;
	}

	public Preference getBtnListFontColor()
	{
		return btnListFontColor;
	}

	public void setBtnListFontColor(Preference btnListFontColor)
	{
		this.btnListFontColor = btnListFontColor;
	}

	public CheckBoxPreference getChkListFontShadow()
	{
		return chkListFontShadow;
	}

	public void setChkListFontShadow(CheckBoxPreference chkListFontShadow)
	{
		this.chkListFontShadow = chkListFontShadow;
	}

	public Preference getBtnListFontShadowColor()
	{
		return btnListFontShadowColor;
	}

	public void setBtnListFontShadowColor(Preference btnListFontShadowColor)
	{
		this.btnListFontShadowColor = btnListFontShadowColor;
	}

	public EditTextPreference getTxtLRCFontSize()
	{
		return txtLRCFontSize;
	}

	public void setTxtLRCFontSize(EditTextPreference txtLRCFontSize)
	{
		this.txtLRCFontSize = txtLRCFontSize;
	}

	public Preference getBtnLRCNormalFontColor()
	{
		return btnLRCNormalFontColor;
	}

	public void setBtnLRCNormalFontColor(Preference btnLRCNormalFontColor)
	{
		this.btnLRCNormalFontColor = btnLRCNormalFontColor;
	}

	public Preference getBtnLRCHighlightlFontColor()
	{
		return btnLRCHighlightlFontColor;
	}

	public void setBtnLRCHighlightlFontColor(Preference btnLRCHighlightlFontColor)
	{
		this.btnLRCHighlightlFontColor = btnLRCHighlightlFontColor;
	}

	public CheckBoxPreference getChkLRCFontShadow()
	{
		return chkLRCFontShadow;
	}

	public void setChkLRCFontShadow(CheckBoxPreference chkLRCFontShadow)
	{
		this.chkLRCFontShadow = chkLRCFontShadow;
	}

	public Preference getBtnLRCFontShadowColor()
	{
		return btnLRCFontShadowColor;
	}

	public void setBtnLRCFontShadowColor(Preference btnLRCFontShadowColor)
	{
		this.btnLRCFontShadowColor = btnLRCFontShadowColor;
	}

	public EditTextPreference getTxtRestore()
	{
		return txtRestore;
	}

	public void setTxtRestore(EditTextPreference txtRestore)
	{
		this.txtRestore = txtRestore;
	}

	public Preference getBtnVisitOfficialSite()
	{
		return btnVisitOfficialSite;
	}

	public void setBtnVisitOfficialSite(Preference btnVisitOfficialSite)
	{
		this.btnVisitOfficialSite = btnVisitOfficialSite;
	}

	public Preference getBtnAbout()
	{
		return btnAbout;
	}

	public void setBtnAbout(Preference btnAbout)
	{
		this.btnAbout = btnAbout;
	}

	public ListPreference getLstLRCScrollStyle()
	{
		return lstLRCScrollStyle;
	}

	public void setLstLRCScrollStyle(ListPreference lstLRCScrollStyle)
	{
		this.lstLRCScrollStyle = lstLRCScrollStyle;
	}

	public ListPreference getLstNotifyNext()
	{
		return lstNotifyNext;
	}

	public void setLstNotifyNext(ListPreference lstNotifyNext)
	{
		this.lstNotifyNext = lstNotifyNext;
	}

	public EditTextPreference getTxtFavouriteMax()
	{
		return txtFavouriteMax;
	}

	public void setTxtFavouriteMax(EditTextPreference txtFavouriteMax)
	{
		this.txtFavouriteMax = txtFavouriteMax;
	}
}