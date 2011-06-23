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

			LastStatus = -1; // 还原标志位
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