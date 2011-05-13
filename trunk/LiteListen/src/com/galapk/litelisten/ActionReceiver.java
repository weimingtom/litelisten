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

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

public class ActionReceiver extends BroadcastReceiver
{
	private srcMain main = null;
	private boolean IsFirstActionHeadsetPlug = true; // �Ƿ��һ���յ�ACTION_HEADSET_PLUG��Ϣ
	private int MusicStatus = -1; // ����״̬

	public ActionReceiver(srcMain main)
	{
		this.main = main;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		abortBroadcast();

		if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) || intent.getAction().equals(Intent.ACTION_HEADSET_PLUG) || intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
		{// ��һ���յ�ACTION_HEADSET_PLUG��Ϣʱ���ԣ�ϵͳ�Զ����ͣ�
			if (!IsFirstActionHeadsetPlug)
				main.getMs().Pause();
			else
				IsFirstActionHeadsetPlug = false;
		}
		else if (intent.getAction().equals(IntentConst.INTENT_ACTION_FLOAT_LRC_LOCK))
			main.LockFloatLRC(true);
		else if (intent.getAction().equals(IntentConst.INTENT_ACTION_FLOAT_LRC_UNLOCK))
			main.LockFloatLRC(false);
		else if (intent.getAction().equals(IntentConst.INTENT_ACTION_NOTIFICATION_NEXT))
			main.getMs().Next(false);
		else
		{
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tm.getCallState())
			{
				case TelephonyManager.CALL_STATE_RINGING: // ����
					MusicStatus = main.getMs().getPlayerStatus();
					main.getMs().Pause();
					return;
				case TelephonyManager.CALL_STATE_OFFHOOK: // �Ҷ�
					if (MusicStatus == MusicService.STATUS_PLAY)
						main.getMs().PlayPause();
					return;
			}

			KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (key.getAction() == KeyEvent.ACTION_UP)
			{
				int keycode = key.getKeyCode();
				if (keycode == KeyEvent.KEYCODE_MEDIA_NEXT)
					main.getMs().Next(false);
				else if (keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
					main.getMs().Last();
				else if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
					main.getMs().PlayPause();
			}
		}
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}
}