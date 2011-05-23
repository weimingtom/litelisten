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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
	private CheckBox chkIgnoreDirectory;
	private CheckBox chkAutoStart;
	private CheckBox chkRememberLast;
	private Button btnChineseConvert;
	private Button btnScreenOrientation;
	private Button btnListSortOrder;
	private CheckBox chkAutoSwitchToLRC;
	private Button btnReadingPriority;
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
	private Button btnRestore;
	private ScrollView scrHelp;
	private LinearLayout layHelp;
	private Button btnGoHome;
	private Button btnAbout;
	private LinearLayout layOkCancel;
	private Button btnOK;
	private Button btnCancel;

	private SharedPreferences sp;

	private String Language;
	private String MusicPath;
	private Boolean IncludeSubDirectories;
	private Boolean IgnoreDirectory;
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
	private String Restore;

	/* 获取选项值 */
	private void GetPreferences()
	{
		sp = getSharedPreferences("com.galapk.litelisten_preferences", Context.MODE_PRIVATE); // 读取配置文件

		Language = sp.getString("lstLanguage", "2");

		MusicPath = sp.getString("txtMusicPath", "/sdcard");
		IncludeSubDirectories = sp.getBoolean("chkIncludeSubDirectories", true);
		IgnoreDirectory = sp.getBoolean("chkIgnoreDirectory", true);

		ListSortOrder = sp.getString("lstListSortOrder", "1");
		AutoSwitchToLRC = sp.getBoolean("chkAutoSwitchToLRC", true);
		PlayMode = sp.getString("lstPlayMode", "1");
		NotifyAction = sp.getString("lstNotifyAction", "0");
		FavoriteMax = sp.getString("txtFavoriteMax", "30");

		ScrollMode = sp.getString("lstScrollMode", "0");
		BackgroundPort = sp.getString("lstBackgroundPort", "0");
		BackgroundLand = sp.getString("lstBackgroundLand", "0");
		BackgroundBrightness = sp.getString("txtBackgroundBrightness", "75");
		BackgroundBlur = sp.getBoolean("chkBackgroundBlur", true);

		UseAnimation = sp.getBoolean("chkUseAnimation", true);

		ListFontSize = sp.getString("txtListFontSize", "18");
		ListFontColor = sp.getString("btnListFontColor", "#FFFFFF");
		ListFontShadow = sp.getBoolean("chkListFontShadow", true);
		ListFontShadowColor = sp.getString("btnListFontShadowColor", "#0099FF");

		LRCFontSize = sp.getString("txtLRCFontSize", "18");
		LRCFontColorNormal = sp.getString("btnLRCFontColorNormal", "#FFFFFF");
		LRCFontColorHighlight = sp.getString("btnLRCFontColorHighlight", "#FFFF00");
		LRCFontShadow = sp.getBoolean("chkLRCFontShadow", true);
		LRCFontShadowColor = sp.getString("btnLRCFontShadowColor", "#0099FF");

		Restore = sp.getString("txtRestore", "");

		/*
		 * sp.getString("lstFitScreenOrientation", "2");
		 * sp.getString("lstPropertyReadPriority", "0");
		 * sp.getString("lstConvertChineseLRC", "2");
		 * sp.getBoolean("chkRemeberLastPlayed", true);
		 */
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 设置窗口样式，必须按照顺序
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 无标题栏
		setContentView(R.layout.scr_settings);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
		FindViews();
		ListernerBinding();
		GetPreferences();
		ButtonInitialization();

		btnGeneral.setBackgroundResource(R.drawable.bg_setting_category_highlight);

		btnGeneral.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				scrGeneral.setVisibility(View.VISIBLE);
				scrDisplay.setVisibility(View.GONE);
				scrOthers.setVisibility(View.GONE);
				scrHelp.setVisibility(View.GONE);
				btnGeneral.setBackgroundResource(R.drawable.bg_setting_category_highlight);
				btnDisplay.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnOthers.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnHelp.setBackgroundResource(R.drawable.bg_setting_category_normal);
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
				btnGeneral.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnDisplay.setBackgroundResource(R.drawable.bg_setting_category_highlight);
				btnOthers.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnHelp.setBackgroundResource(R.drawable.bg_setting_category_normal);
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
				btnGeneral.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnDisplay.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnOthers.setBackgroundResource(R.drawable.bg_setting_category_highlight);
				btnHelp.setBackgroundResource(R.drawable.bg_setting_category_normal);
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
				btnGeneral.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnDisplay.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnOthers.setBackgroundResource(R.drawable.bg_setting_category_normal);
				btnHelp.setBackgroundResource(R.drawable.bg_setting_category_highlight);
			}
		});
	}

	/* 设置各选项按钮初值 */
	private void ButtonInitialization()
	{
		btnLanguage.setText(Html.fromHtml(getString(R.string.pfrscat_general_language) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_general_language)[Integer.parseInt((String) Language)] + "</font>"));
		btnMusicPath.setText(Html.fromHtml(getString(R.string.pfrscat_general_music_path) + "<br /><font color='#FFFF00'>" + MusicPath + "</font>"));
		btnListSortOrder.setText(Html.fromHtml(getString(R.string.pfrscat_general_list_order) + "<br /><font color='#FFFF00'>"
				+ getResources().getStringArray(R.array.item_name_pfrscat_general_list_order)[Integer.parseInt((String) ListSortOrder)] + "</font>"));
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
		btnListFontSize.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_size) + "<br /><font color='#FFFF00'>" + ListFontSize + "</font>"));
		btnListFontColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_size) + "<br /><font color='#FFFF00'>" + ListFontColor + "</font>"));
		btnListFontShadowColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_shadow_color) + "<br /><font color='#FFFF00'>" + ListFontShadowColor + "</font>"));
		btnLRCFontSize.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_size) + "<br /><font color='#FFFF00'>" + LRCFontSize + "</font>"));
		btnLRCFontColorNormal.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_normal_font_color) + "<br /><font color='#FFFF00'>" + LRCFontColorNormal + "</font>"));
		btnLRCFontColorHighlight.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_highlight_font_color) + "<br /><font color='#FFFF00'>" + LRCFontColorHighlight + "</font>"));
		btnLRCFontShadowColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_font_shadow_color) + "<br /><font color='#FFFF00'>" + LRCFontShadowColor + "</font>"));
	}

	/* 绑定按钮事件 */
	public void ListernerBinding()
	{
		btnLanguage.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ListDialog.ShowDialog(scrSettings.this, layActivity, getString(R.string.pfrscat_general_language_title), getResources().getStringArray(R.array.item_name_pfrscat_general_language), 18,
						new OnClickListener()
						{
							public void onClick(View v)
							{

								btnLanguage.setText(Html.fromHtml(getString(R.string.pfrscat_general_language) + "<br /><font color='#FFFF00'>" + ListDialog.getRet() + "</font>"));
								ListDialog.getPw().dismiss();
							}
						});
			}
		});

		btnMusicPath.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_general_music_path), "", 18, "", 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						btnMusicPath.setText(Html.fromHtml(getString(R.string.pfrscat_general_music_path) + "<br /><font color='#FFFF00'>" + TextDialog.getEdtMessage().getText().toString()
								+ "</font>"));
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnListSortOrder.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ListDialog.ShowDialog(scrSettings.this, layActivity, getString(R.string.pfrscat_general_list_order), getResources().getStringArray(R.array.item_name_pfrscat_general_list_order), 18,
						new OnClickListener()
						{
							public void onClick(View v)
							{
								btnListSortOrder.setText(Html.fromHtml(getString(R.string.pfrscat_general_list_order) + "<br /><font color='#FFFF00'>" + ListDialog.getRet() + "</font>"));
								ListDialog.getPw().dismiss();
							}
						});
			}
		});

		btnPlayMode.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ListDialog.ShowDialog(scrSettings.this, layActivity, getString(R.string.pfrscat_general_play_mode), getResources().getStringArray(R.array.item_name_pfrscat_general_play_mode), 18,
						new OnClickListener()
						{
							public void onClick(View v)
							{
								btnPlayMode.setText(Html.fromHtml(getString(R.string.pfrscat_general_play_mode) + "<br /><font color='#FFFF00'>" + ListDialog.getRet() + "</font>"));
								ListDialog.getPw().dismiss();
							}
						});
			}
		});

		btnNotifyAction.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ListDialog.ShowDialog(scrSettings.this, layActivity, getString(R.string.pfrscat_general_notify_next), getResources().getStringArray(R.array.item_name_pfrscat_general_notify_next), 18,
						new OnClickListener()
						{
							public void onClick(View v)
							{
								btnNotifyAction.setText(Html.fromHtml(getString(R.string.pfrscat_general_notify_next) + "<br /><font color='#FFFF00'>" + ListDialog.getRet() + "</font>"));
								ListDialog.getPw().dismiss();
							}
						});
			}
		});

		btnFavoriteMax.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_general_favourite_max), "", 18, "", 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						btnFavoriteMax.setText(Html.fromHtml(getString(R.string.pfrscat_general_favourite_max) + "<br /><font color='#FFFF00'>" + TextDialog.getEdtMessage().getText().toString()
								+ "</font>"));
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnScrollMode.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ListDialog.ShowDialog(scrSettings.this, layActivity, getString(R.string.pfrscat_display_lrc_scroll_style), getResources().getStringArray(
						R.array.item_name_pfrscat_display_lrc_scroll_style), 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						btnScrollMode.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_scroll_style) + "<br /><font color='#FFFF00'>" + ListDialog.getRet() + "</font>"));
						ListDialog.getPw().dismiss();
					}
				});
			}
		});

		btnBackgroundPort.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ListDialog.ShowDialog(scrSettings.this, layActivity, getString(R.string.pfrscat_display_background_port), getResources().getStringArray(R.array.item_name_pfrscat_display_background),
						18, new OnClickListener()
						{
							public void onClick(View v)
							{
								btnBackgroundPort.setText(Html.fromHtml(getString(R.string.pfrscat_display_background_port) + "<br /><font color='#FFFF00'>" + ListDialog.getRet() + "</font>"));
								ListDialog.getPw().dismiss();
							}
						});
			}
		});

		btnBackgroundLand.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ListDialog.ShowDialog(scrSettings.this, layActivity, getString(R.string.pfrscat_display_background_land), getResources().getStringArray(R.array.item_name_pfrscat_display_background),
						18, new OnClickListener()
						{
							public void onClick(View v)
							{
								btnBackgroundLand.setText(Html.fromHtml(getString(R.string.pfrscat_display_background_land) + "<br /><font color='#FFFF00'>" + ListDialog.getRet() + "</font>"));
								ListDialog.getPw().dismiss();
							}
						});
			}
		});

		btnBackgroundBrightness.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_background_brightness), "", 18, "", 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						btnBackgroundBrightness.setText(Html.fromHtml(getString(R.string.pfrscat_display_background_brightness) + "<br /><font color='#FFFF00'>"
								+ TextDialog.getEdtMessage().getText().toString() + "</font>"));
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnListFontSize.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_list_font_size), "", 18, "", 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						btnListFontSize.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_size) + "<br /><font color='#FFFF00'>" + TextDialog.getEdtMessage().getText().toString()
								+ "</font>"));
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnListFontColor.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_list_font_color), 18, Color.parseColor("#FF6600"), new OnClickListener()
				{
					public void onClick(View v)
					{
						btnListFontColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_size) + "<br /><font color='#FFFF00'>" + ColorDialog.getEdtMessage().getText().toString()
								+ "</font>"));
						ColorDialog.getPw().dismiss();
					}
				});
			}
		});

		btnListFontShadowColor.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_list_font_shadow_color), 18, Color.parseColor("#FF6600"), new OnClickListener()
				{
					public void onClick(View v)
					{
						btnListFontShadowColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_shadow_color) + "<br /><font color='#FFFF00'>"
								+ ColorDialog.getEdtMessage().getText().toString() + "</font>"));
						ColorDialog.getPw().dismiss();
					}
				});
			}
		});

		btnLRCFontSize.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_list_font_size), "", 18, "", 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						btnLRCFontSize.setText(Html.fromHtml(getString(R.string.pfrscat_display_list_font_size) + "<br /><font color='#FFFF00'>" + TextDialog.getEdtMessage().getText().toString()
								+ "</font>"));
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnLRCFontColorNormal.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_lrc_normal_font_color), 18, Color.parseColor("#FF6600"), new OnClickListener()
				{
					public void onClick(View v)
					{
						btnLRCFontColorNormal.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_normal_font_color) + "<br /><font color='#FFFF00'>"
								+ ColorDialog.getEdtMessage().getText().toString() + "</font>"));
						ColorDialog.getPw().dismiss();
					}
				});
			}
		});

		btnLRCFontColorHighlight.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_lrc_highlight_font_color), 18, Color.parseColor("#FF6600"), new OnClickListener()
				{
					public void onClick(View v)
					{
						btnLRCFontColorHighlight.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_highlight_font_color) + "<br /><font color='#FFFF00'>"
								+ ColorDialog.getEdtMessage().getText().toString() + "</font>"));
						ColorDialog.getPw().dismiss();
					}
				});
			}
		});

		btnLRCFontShadowColor.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ColorDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_display_lrc_font_shadow_color), 18, Color.parseColor("#FF6600"), new OnClickListener()
				{
					public void onClick(View v)
					{
						btnLRCFontShadowColor.setText(Html.fromHtml(getString(R.string.pfrscat_display_lrc_font_shadow_color) + "<br /><font color='#FFFF00'>"
								+ ColorDialog.getEdtMessage().getText().toString() + "</font>"));
						ColorDialog.getPw().dismiss();
					}
				});
			}
		});

		btnRestore.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TextDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_others_restore), getString(R.string.pfrscat_others_restore_title), 18, "", 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						TextDialog.getPw().dismiss();
					}
				});
			}
		});

		btnGoHome.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				MessageDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_help_visit_official_site), getString(R.string.pfrscat_help_visit_official_site_message), 18,
						new OnClickListener()
						{
							public void onClick(View v)
							{
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(getString(R.string.pfrscat_help_visit_official_site_summary)));
								startActivity(i);
								MessageDialog.CloseDialog();
							}
						}, new OnClickListener()
						{
							public void onClick(View v)
							{
								MessageDialog.CloseDialog();
							}
						});
			}
		});

		btnAbout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				MessageDialog.ShowMessage(scrSettings.this, layActivity, getString(R.string.pfrscat_help_about), getString(R.string.pfrscat_help_about_message), 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						MessageDialog.CloseDialog();
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
		chkIgnoreDirectory = (CheckBox) findViewById(R.id.chkIgnoreDirectory);
		chkAutoStart = (CheckBox) findViewById(R.id.chkAutoStart);
		chkRememberLast = (CheckBox) findViewById(R.id.chkRememberLast);
		btnChineseConvert = (Button) findViewById(R.id.btnChineseConvert);
		btnScreenOrientation = (Button) findViewById(R.id.btnScreenOrientation);
		btnListSortOrder = (Button) findViewById(R.id.btnListSortOrder);
		chkAutoSwitchToLRC = (CheckBox) findViewById(R.id.chkAutoSwitchToLRC);
		btnReadingPriority = (Button) findViewById(R.id.btnReadingPriority);
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
		btnRestore = (Button) findViewById(R.id.btnRestore);
		scrHelp = (ScrollView) findViewById(R.id.scrHelp);
		layHelp = (LinearLayout) findViewById(R.id.layHelp);
		btnGoHome = (Button) findViewById(R.id.btnGoHome);
		btnAbout = (Button) findViewById(R.id.btnAbout);
		layOkCancel = (LinearLayout) findViewById(R.id.layOkCancel);
		btnOK = (Button) findViewById(R.id.btnOK);
		btnCancel = (Button) findViewById(R.id.btnCancel);
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

	public CheckBox getChkAutoStart()
	{
		return chkAutoStart;
	}

	public void setChkAutoStart(CheckBox chkAutoStart)
	{
		this.chkAutoStart = chkAutoStart;
	}

	public CheckBox getChkRememberLast()
	{
		return chkRememberLast;
	}

	public void setChkRememberLast(CheckBox chkRememberLast)
	{
		this.chkRememberLast = chkRememberLast;
	}

	public Button getBtnChineseConvert()
	{
		return btnChineseConvert;
	}

	public void setBtnChineseConvert(Button btnChineseConvert)
	{
		this.btnChineseConvert = btnChineseConvert;
	}

	public Button getBtnScreenOrientation()
	{
		return btnScreenOrientation;
	}

	public void setBtnScreenOrientation(Button btnScreenOrientation)
	{
		this.btnScreenOrientation = btnScreenOrientation;
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

	public Button getBtnReadingPriority()
	{
		return btnReadingPriority;
	}

	public void setBtnReadingPriority(Button btnReadingPriority)
	{
		this.btnReadingPriority = btnReadingPriority;
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

	public Button getBtnOK()
	{
		return btnOK;
	}

	public void setBtnOK(Button btnOK)
	{
		this.btnOK = btnOK;
	}

	public Button getBtnCancel()
	{
		return btnCancel;
	}

	public void setBtnCancel(Button btnCancel)
	{
		this.btnCancel = btnCancel;
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

	public Boolean getIncludeSubDirectories()
	{
		return IncludeSubDirectories;
	}

	public void setIncludeSubDirectories(Boolean includeSubDirectories)
	{
		IncludeSubDirectories = includeSubDirectories;
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
}