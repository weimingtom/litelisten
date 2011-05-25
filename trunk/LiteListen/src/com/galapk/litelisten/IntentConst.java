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

public class IntentConst
{
	public static String INTENT_ACTION_REFRESH_LRC = "com.littledai.intent.action.refresh.lrc"; // ˢ�¸��
	public static String INTENT_ACTION_REFRESH_TIME_N_TITLE = "com.littledai.intent.action.refresh.timentitle"; // ˢ��ʱ��
	public static String INTENT_ACTION_IS_PLAYING = "com.littledai.intent.action.is.playing"; // ���ڲ���
	public static String INTENT_ACTION_NOT_PLAYING = "com.littledai.intent.action.not.playing"; // ֹͣ����
	public static String INTENT_ACTION_FLOAT_LRC_UNLOCK = "com.littledai.intent.action.float.lrc.unlock"; // ����������
	public static String INTENT_ACTION_FLOAT_LRC_LOCK = "com.littledai.intent.action.float.lrc.lock"; // ����������
	public static String INTENT_ACTION_NOTIFICATION_NEXT = "com.littledai.intent.action.notification.next"; // ����֪ͨ��������һ��
	public static String INTENT_ACTION_FLOAT_LRC_SHOW = "com.littledai.intent.action.float.lrc.show"; // ��ʾ�������
	public static String INTENT_ACTION_FLOAT_LRC_HIDE = "com.littledai.intent.action.float.lrc.hide"; // �رո������

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