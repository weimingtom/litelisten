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

public class IntentConst
{
	public static String INTENT_ACTION_REFRESH_LRC = "com.littledai.intent.action.refresh.lrc"; // 刷新歌词
	public static String INTENT_ACTION_REFRESH_TIME_N_TITLE = "com.littledai.intent.action.refresh.timentitle"; // 刷新时间
	public static String INTENT_ACTION_IS_PLAYING = "com.littledai.intent.action.is.playing"; // 正在播放
	public static String INTENT_ACTION_NOT_PLAYING = "com.littledai.intent.action.not.playing"; // 停止播放
	public static String INTENT_ACTION_FLOAT_LRC_UNLOCK = "com.littledai.intent.action.float.lrc.unlock"; // 解锁桌面歌词
	public static String INTENT_ACTION_FLOAT_LRC_LOCK = "com.littledai.intent.action.float.lrc.lock"; // 锁定桌面歌词
	public static String INTENT_ACTION_NOTIFICATION_NEXT = "com.littledai.intent.action.notification.next"; // 单击通知栏播放下一首
	public static String INTENT_ACTION_FLOAT_LRC_SHOW = "com.littledai.intent.action.float.lrc.show"; // 显示浮动歌词
	public static String INTENT_ACTION_FLOAT_LRC_HIDE = "com.littledai.intent.action.float.lrc.hide"; // 关闭浮动歌词

	public static String getINTENT_ACTION_REFRESH_LRC()
	{
		return INTENT_ACTION_REFRESH_LRC;
	}

	public static void setINTENT_ACTION_REFRESH_LRC(String iNTENTACTIONREFRESHLRC)
	{
		INTENT_ACTION_REFRESH_LRC = iNTENTACTIONREFRESHLRC;
	}

	public static String getINTENT_ACTION_REFRESH_TIME_N_TITLE()
	{
		return INTENT_ACTION_REFRESH_TIME_N_TITLE;
	}

	public static void setINTENT_ACTION_REFRESH_TIME_N_TITLE(String iNTENTACTIONREFRESHTIMENTITLE)
	{
		INTENT_ACTION_REFRESH_TIME_N_TITLE = iNTENTACTIONREFRESHTIMENTITLE;
	}

	public static String getINTENT_ACTION_IS_PLAYING()
	{
		return INTENT_ACTION_IS_PLAYING;
	}

	public static void setINTENT_ACTION_IS_PLAYING(String iNTENTACTIONISPLAYING)
	{
		INTENT_ACTION_IS_PLAYING = iNTENTACTIONISPLAYING;
	}

	public static String getINTENT_ACTION_NOT_PLAYING()
	{
		return INTENT_ACTION_NOT_PLAYING;
	}

	public static void setINTENT_ACTION_NOT_PLAYING(String iNTENTACTIONNOTPLAYING)
	{
		INTENT_ACTION_NOT_PLAYING = iNTENTACTIONNOTPLAYING;
	}

	public static String getINTENT_ACTION_FLOAT_LRC_UNLOCK()
	{
		return INTENT_ACTION_FLOAT_LRC_UNLOCK;
	}

	public static void setINTENT_ACTION_FLOAT_LRC_UNLOCK(String iNTENTACTIONFLOATLRCUNLOCK)
	{
		INTENT_ACTION_FLOAT_LRC_UNLOCK = iNTENTACTIONFLOATLRCUNLOCK;
	}

	public static String getINTENT_ACTION_FLOAT_LRC_LOCK()
	{
		return INTENT_ACTION_FLOAT_LRC_LOCK;
	}

	public static void setINTENT_ACTION_FLOAT_LRC_LOCK(String iNTENTACTIONFLOATLRCLOCK)
	{
		INTENT_ACTION_FLOAT_LRC_LOCK = iNTENTACTIONFLOATLRCLOCK;
	}

	public static String getINTENT_ACTION_NOTIFICATION_NEXT()
	{
		return INTENT_ACTION_NOTIFICATION_NEXT;
	}

	public static void setINTENT_ACTION_NOTIFICATION_NEXT(String iNTENTACTIONNOTIFICATIONNEXT)
	{
		INTENT_ACTION_NOTIFICATION_NEXT = iNTENTACTIONNOTIFICATIONNEXT;
	}

	public static String getINTENT_ACTION_FLOAT_LRC_SHOW()
	{
		return INTENT_ACTION_FLOAT_LRC_SHOW;
	}

	public static void setINTENT_ACTION_FLOAT_LRC_SHOW(String iNTENTACTIONFLOATLRCSHOW)
	{
		INTENT_ACTION_FLOAT_LRC_SHOW = iNTENTACTIONFLOATLRCSHOW;
	}

	public static String getINTENT_ACTION_FLOAT_LRC_HIDE()
	{
		return INTENT_ACTION_FLOAT_LRC_HIDE;
	}

	public static void setINTENT_ACTION_FLOAT_LRC_HIDE(String iNTENTACTIONFLOATLRCHIDE)
	{
		INTENT_ACTION_FLOAT_LRC_HIDE = iNTENTACTIONFLOATLRCHIDE;
	}
}