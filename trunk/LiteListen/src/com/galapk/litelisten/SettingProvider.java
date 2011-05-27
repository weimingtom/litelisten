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

import android.content.Intent;

public class SettingProvider
{
	private String Language;
	private String MusicPath;
	private Boolean IncludeSubDirectory;
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

	// 非选项菜单中的选项
	private Boolean FloatLRCLocked;
	private Boolean DeskLRCStatus;
	private String LastKeyword;
	private String OrderBy;
	private Boolean KeepScreenOn;
	private Integer FloatLRCPos;
	private Boolean Started;
	private Boolean IsRunBackground;

	public SettingProvider(scrMain main)
	{
		Language = main.getSp().getString("Language", "3");

		MusicPath = main.getSp().getString("MusicPath", "/sdcard");
		IncludeSubDirectory = main.getSp().getBoolean("IncludeSubDirectory", true);
		IgnoreDirectory = main.getSp().getBoolean("IgnoreDirectory", true);
		AutoPause = main.getSp().getBoolean("AutoPause", true);
		LRCAutoDownload = main.getSp().getBoolean("LRCAutoDownload", false);

		ListSortOrder = main.getSp().getString("ListSortOrder", "1");
		AutoSwitchToLRC = main.getSp().getBoolean("AutoSwitchToLRC", true);
		PlayMode = main.getSp().getString("PlayMode", "1");
		NotifyAction = main.getSp().getString("NotifyAction", "0");
		FavoriteMax = main.getSp().getString("FavoriteMax", "30");

		ScrollMode = main.getSp().getString("ScrollMode", "0");
		BackgroundPort = main.getSp().getString("BackgroundPort", "0");
		BackgroundLand = main.getSp().getString("BackgroundLand", "0");
		BackgroundBrightness = main.getSp().getString("BackgroundBrightness", "75");
		BackgroundBlur = main.getSp().getBoolean("BackgroundBlur", true);

		UseAnimation = main.getSp().getBoolean("UseAnimation", true);

		ListFontSize = main.getSp().getString("ListFontSize", "18");
		ListFontColor = main.getSp().getString("ListFontColor", "#FFFFFF");
		ListFontShadow = main.getSp().getBoolean("ListFontShadow", true);
		ListFontShadowColor = main.getSp().getString("ListFontShadowColor", "#000000");

		LRCFontSize = main.getSp().getString("LRCFontSize", "18");
		LRCFontColorNormal = main.getSp().getString("LRCFontColorNormal", "#FFFFFF");
		LRCFontColorHighlight = main.getSp().getString("LRCFontColorHighlight", "#FFFF00");
		LRCFontShadow = main.getSp().getBoolean("LRCFontShadow", true);
		LRCFontShadowColor = main.getSp().getString("LRCFontShadowColor", "#0099FF");

		FloatLRCLocked = main.getSp().getBoolean("FloatLRCLocked", false);
		DeskLRCStatus = main.getSp().getBoolean("DeskLRCStatus", true);
		LastKeyword = main.getSp().getString("LastKeyword", "");
		OrderBy = main.getSp().getString("OrderBy", "asc");
		KeepScreenOn = main.getSp().getBoolean("KeepScreenOn", false);
		FloatLRCPos = main.getSp().getInt("FloatLRCPos", 0);
		Started = main.getSp().getBoolean("Started", true);
		IsRunBackground = main.getSp().getBoolean("IsRunBackground", false);
	}

	/* 刷新设置 */
	public void RefreshSettings(Intent intent)
	{
		Language = intent.getStringExtra("Language");
		MusicPath = intent.getStringExtra("MusicPath");
		IncludeSubDirectory = intent.getBooleanExtra("IncludeSubDirectory", true);
		IgnoreDirectory = intent.getBooleanExtra("IgnoreDirectory", true);
		AutoPause = intent.getBooleanExtra("AutoPause", true);
		LRCAutoDownload = intent.getBooleanExtra("LRCAutoDownload", true);
		ListSortOrder = intent.getStringExtra("ListSortOrder");
		AutoSwitchToLRC = intent.getBooleanExtra("AutoSwitchToLRC", true);
		PlayMode = intent.getStringExtra("PlayMode");
		NotifyAction = intent.getStringExtra("NotifyAction");
		FavoriteMax = intent.getStringExtra("FavoriteMax");
		ScrollMode = intent.getStringExtra("ScrollMode");
		BackgroundPort = intent.getStringExtra("BackgroundPort");
		BackgroundLand = intent.getStringExtra("BackgroundLand");
		BackgroundBrightness = intent.getStringExtra("BackgroundBrightness");
		BackgroundBlur = intent.getBooleanExtra("BackgroundBlur", true);
		UseAnimation = intent.getBooleanExtra("UseAnimation", true);
		ListFontSize = intent.getStringExtra("ListFontSize");
		ListFontColor = intent.getStringExtra("ListFontColor");
		ListFontShadow = intent.getBooleanExtra("ListFontShadow", true);
		ListFontShadowColor = intent.getStringExtra("ListFontShadowColor");
		LRCFontSize = intent.getStringExtra("LRCFontSize");
		LRCFontColorNormal = intent.getStringExtra("LRCFontColorNormal");
		LRCFontColorHighlight = intent.getStringExtra("LRCFontColorHighlight");
		LRCFontShadow = intent.getBooleanExtra("LRCFontShadow", true);
		LRCFontShadowColor = intent.getStringExtra("LRCFontShadowColor");
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

	public void setIncludeSubDirectory(Boolean includeSubDirectory)
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

	public Boolean getAutoPause()
	{
		return AutoPause;
	}

	public void setAutoPause(Boolean autoPause)
	{
		AutoPause = autoPause;
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

	public Boolean getFloatLRCLocked()
	{
		return FloatLRCLocked;
	}

	public void setFloatLRCLocked(Boolean floatLRCLocked)
	{
		FloatLRCLocked = floatLRCLocked;
	}

	public Boolean getDeskLRCStatus()
	{
		return DeskLRCStatus;
	}

	public void setDeskLRCStatus(Boolean deskLRCStatus)
	{
		DeskLRCStatus = deskLRCStatus;
	}

	public String getLastKeyword()
	{
		return LastKeyword;
	}

	public void setLastKeyword(String lastKeyword)
	{
		LastKeyword = lastKeyword;
	}

	public String getOrderBy()
	{
		return OrderBy;
	}

	public void setOrderBy(String orderBy)
	{
		OrderBy = orderBy;
	}

	public Boolean getKeepScreenOn()
	{
		return KeepScreenOn;
	}

	public void setKeepScreenOn(Boolean keepScreenOn)
	{
		KeepScreenOn = keepScreenOn;
	}

	public Integer getFloatLRCPos()
	{
		return FloatLRCPos;
	}

	public void setFloatLRCPos(Integer floatLRCPos)
	{
		FloatLRCPos = floatLRCPos;
	}

	public Boolean getStarted()
	{
		return Started;
	}

	public void setStarted(Boolean started)
	{
		Started = started;
	}

	public Boolean getIsRunBackground()
	{
		return IsRunBackground;
	}

	public void setIsRunBackground(Boolean isRunBackground)
	{
		IsRunBackground = isRunBackground;
	}

	public Boolean getLRCAutoDownload()
	{
		return LRCAutoDownload;
	}

	public void setLRCAutoDownload(Boolean lRCAutoDownload)
	{
		LRCAutoDownload = lRCAutoDownload;
	}
}