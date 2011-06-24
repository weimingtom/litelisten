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

import java.util.Locale;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VolumeDialog
{
	private PopupWindow pw;
	private View view;
	private TextView txtTitle;
	private SeekBar skbVolume;
	private AudioManager am;

	private boolean IsShown = false;
	private int CountDown = 2;

	public VolumeDialog(final Activity act)
	{
		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.popup_volume_dialog, null, false);
		txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		skbVolume = (SeekBar) view.findViewById(R.id.skbVolume);
		am = (AudioManager) act.getSystemService(Service.AUDIO_SERVICE);

		// ��������
		txtTitle.setKeyListener(new KeyListener()
		{
			public void clearMetaKeyState(View view, Editable content, int states)
			{

			}

			public int getInputType()
			{
				return 0;
			}

			public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
				{
					skbVolume.setProgress(skbVolume.getProgress() + 1);
					CountDown = 2;
				}
				else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
				{
					skbVolume.setProgress(skbVolume.getProgress() - 1);
					CountDown = 2;
				}

				return true;
			}

			public boolean onKeyOther(View view, Editable text, KeyEvent event)
			{
				return false;
			}

			public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event)
			{
				return false;
			}
		});

		/* �������� */
		skbVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				CountDown = 2;
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{
				CountDown = 60;
			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

				if (progress == 0)
					txtTitle.setText(act.getString(R.string.scrmain_volume_mute)); // ��ʾ����
				else
					txtTitle.setText(act.getString(R.string.scrmain_volume) + progress); // ��ʱ��ʾ����

				if (fromUser)
					CountDown = 60; // �û��϶�����ʱ����60��
				else
					CountDown = 2;
			}
		});
	}

	public void ShowDialog(Activity act, String LanguageIndex, boolean UsingAnimation, View WindowParent)
	{
		if (!LanguageIndex.equals("3"))
		{
			Configuration config = act.getResources().getConfiguration(); // ������ö���

			if (LanguageIndex.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // ��������
			else if (LanguageIndex.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // ��������
			else if (LanguageIndex.equals("2"))
				config.locale = Locale.US; // ��ʽӢ��

			act.getResources().updateConfiguration(config, act.getResources().getDisplayMetrics());
		}

		skbVolume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		skbVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		CountDown = 2;

		if (skbVolume.getProgress() == 0)
			txtTitle.setText(act.getString(R.string.scrmain_volume_mute)); // ��ʾ����
		else
			txtTitle.setText(act.getString(R.string.scrmain_volume) + skbVolume.getProgress()); // ��ʱ��ʾ����

		if (!IsShown)
		{
			int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();
			if (ScreenOrientation == 1 || ScreenOrientation == 3)
				pw = new PopupWindow(view, 600, LayoutParams.WRAP_CONTENT, true);
			else
				pw = new PopupWindow(view, 440, LayoutParams.WRAP_CONTENT, true);
			pw.setBackgroundDrawable(new BitmapDrawable()); // ��Ӧ���ؼ���������

			if (UsingAnimation)
				pw.setAnimationStyle(R.style.DialogAnimation);

			pw.showAtLocation(WindowParent, Gravity.TOP + Gravity.CENTER_HORIZONTAL, 0, 0); // ��ʾ�ڶ���

			new Thread()
			{
				public void run()
				{
					while (CountDown > 0)
					{
						try
						{
							sleep(1000);
							CountDown -= 1;
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}

					IsShown = false;
					pw.dismiss();
				}
			}.start();
			IsShown = true;
		}
	}

	public PopupWindow getPw()
	{
		return pw;
	}

	public void setPw(PopupWindow pw)
	{
		this.pw = pw;
	}

	public View getView()
	{
		return view;
	}

	public void setView(View view)
	{
		this.view = view;
	}

	public TextView getTxtTitle()
	{
		return txtTitle;
	}

	public void setTxtTitle(TextView txtTitle)
	{
		this.txtTitle = txtTitle;
	}

	public SeekBar getSkbVolume()
	{
		return skbVolume;
	}

	public void setSkbVolume(SeekBar skbVolume)
	{
		this.skbVolume = skbVolume;
	}

	public AudioManager getAm()
	{
		return am;
	}

	public void setAm(AudioManager am)
	{
		this.am = am;
	}

	public boolean isIsShown()
	{
		return IsShown;
	}

	public void setIsShown(boolean isShown)
	{
		IsShown = isShown;
	}

	public int getCountDown()
	{
		return CountDown;
	}

	public void setCountDown(int countDown)
	{
		CountDown = countDown;
	}
}