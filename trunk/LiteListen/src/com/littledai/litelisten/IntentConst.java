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

package com.littledai.litelisten;

public class IntentConst
{
	public static String INTENT_ACTION_REFRESH_LRC = "com.littledai.intent.action.refresh.lrc"; // ˢ�¸��
	public static String INTENT_ACTION_REFRESH_TIME_N_TITLE = "com.littledai.intent.action.refresh.timentitle"; // ˢ��ʱ��

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