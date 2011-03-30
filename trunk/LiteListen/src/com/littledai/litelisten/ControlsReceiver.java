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

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class ControlsReceiver extends BroadcastReceiver
{
	private srcMain main = null;

	public ControlsReceiver(srcMain main)
	{
		this.main = main;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) || intent.getAction().equals(Intent.ACTION_HEADSET_PLUG))
			main.getMs().Pause();
		else
		{
			abortBroadcast();

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