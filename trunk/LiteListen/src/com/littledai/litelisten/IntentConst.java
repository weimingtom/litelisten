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

package com.littledai.litelisten;

public class IntentConst
{
	public static String INTENT_ACTION_REFRESH_LRC = "com.littledai.intent.action.refresh.lrc"; // 刷新歌词
	public static String INTENT_ACTION_REFRESH_TIME_N_TITLE = "com.littledai.intent.action.refresh.timentitle"; // 刷新时间

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
}