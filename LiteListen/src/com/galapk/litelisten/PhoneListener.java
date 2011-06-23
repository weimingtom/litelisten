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

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneListener extends PhoneStateListener
{
	private scrMain main = null;
	private int LastStatus = -1;

	public PhoneListener(scrMain main)
	{
		this.main = main;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber)
	{
		if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING)
		{
			if (main.getMs().getPlayerStatus() == MusicService.STATUS_PLAY)
			{
				LastStatus = MusicService.STATUS_PLAY;
				main.getMs().Pause();
			}

			main.setIsForceHideFloatLRC(true);
		}
		else if (state == TelephonyManager.CALL_STATE_IDLE)
		{
			if (LastStatus == MusicService.STATUS_PLAY)
				main.getMs().Play(main.getMs().getCurrIndex());

			LastStatus = -1; // ��ԭ��־λ
		}
	}

	public scrMain getMain()
	{
		return main;
	}

	public void setMain(scrMain main)
	{
		this.main = main;
	}

	public int getLastStatus()
	{
		return LastStatus;
	}

	public void setLastStatus(int lastStatus)
	{
		LastStatus = lastStatus;
	}
}