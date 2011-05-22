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
import android.os.Bundle;
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

		btnGeneral.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				scrGeneral.setVisibility(View.VISIBLE);
				scrDisplay.setVisibility(View.GONE);
				scrOthers.setVisibility(View.GONE);
				scrHelp.setVisibility(View.GONE);
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
			}
		});
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
								btnLanguage.setText(getString(R.string.pfrscat_general_language_title) + "\n" + ListDialog.getRet());
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
						btnMusicPath.setText(getString(R.string.pfrscat_general_music_path) + "\n" + TextDialog.getEdtMessage().getText().toString());
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
								btnListSortOrder.setText(getString(R.string.pfrscat_general_list_order) + "\n" + ListDialog.getRet());
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
								btnPlayMode.setText(getString(R.string.pfrscat_general_play_mode) + "\n" + ListDialog.getRet());
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
								btnNotifyAction.setText(getString(R.string.pfrscat_general_notify_next) + "\n" + ListDialog.getRet());
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
						btnFavoriteMax.setText(getString(R.string.pfrscat_general_favourite_max) + "\n" + TextDialog.getEdtMessage().getText().toString());
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
						btnScrollMode.setText(getString(R.string.pfrscat_display_lrc_scroll_style) + "\n" + ListDialog.getRet());
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
								btnBackgroundPort.setText(getString(R.string.pfrscat_display_background_port) + "\n" + ListDialog.getRet());
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
								btnBackgroundLand.setText(getString(R.string.pfrscat_display_background_land) + "\n" + ListDialog.getRet());
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
						btnBackgroundBrightness.setText(getString(R.string.pfrscat_display_background_brightness) + "\n" + TextDialog.getEdtMessage().getText().toString());
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
						btnListFontSize.setText(getString(R.string.pfrscat_display_list_font_size) + "\n" + TextDialog.getEdtMessage().getText().toString());
						TextDialog.getPw().dismiss();
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
						btnLRCFontSize.setText(getString(R.string.pfrscat_display_list_font_size) + "\n" + TextDialog.getEdtMessage().getText().toString());
						TextDialog.getPw().dismiss();
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
								MessageDialog.CloseDialog();
							}
						}, null);
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
}