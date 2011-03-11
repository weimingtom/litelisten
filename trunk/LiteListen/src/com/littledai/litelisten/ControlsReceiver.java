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
		setMain(main);
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
				{
					main.getMs().Next(false);
				}
				else if (keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
				{
					main.getMs().Last();
				}
				else if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
				{
					main.getMs().PlayPause();
				}
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