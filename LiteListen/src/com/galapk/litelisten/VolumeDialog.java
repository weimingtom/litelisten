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

		// 监听按键
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

		/* 音量滑块 */
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
					txtTitle.setText(act.getString(R.string.scrmain_volume_mute)); // 显示静音
				else
					txtTitle.setText(act.getString(R.string.scrmain_volume) + progress); // 暂时显示音量

				if (fromUser)
					CountDown = 60; // 用户拖动滑块时保持60秒
				else
					CountDown = 2;
			}
		});
	}

	public void ShowDialog(Activity act, String LanguageIndex, boolean UsingAnimation, View WindowParent)
	{
		if (!LanguageIndex.equals("3"))
		{
			Configuration config = act.getResources().getConfiguration(); // 获得设置对象

			if (LanguageIndex.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
			else if (LanguageIndex.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // 繁体中文
			else if (LanguageIndex.equals("2"))
				config.locale = Locale.US; // 美式英语

			act.getResources().updateConfiguration(config, act.getResources().getDisplayMetrics());
		}

		skbVolume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		skbVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		CountDown = 2;

		if (skbVolume.getProgress() == 0)
			txtTitle.setText(act.getString(R.string.scrmain_volume_mute)); // 显示静音
		else
			txtTitle.setText(act.getString(R.string.scrmain_volume) + skbVolume.getProgress()); // 暂时显示音量

		if (!IsShown)
		{
			int ScreenOrientation = act.getWindowManager().getDefaultDisplay().getOrientation();
			if (ScreenOrientation == 1 || ScreenOrientation == 3)
				pw = new PopupWindow(view, 600, LayoutParams.WRAP_CONTENT, true);
			else
				pw = new PopupWindow(view, 440, LayoutParams.WRAP_CONTENT, true);
			pw.setBackgroundDrawable(new BitmapDrawable()); // 响应返回键必须的语句

			if (UsingAnimation)
				pw.setAnimationStyle(R.style.DialogAnimation);

			pw.showAtLocation(WindowParent, Gravity.TOP + Gravity.CENTER_HORIZONTAL, 0, 0); // 显示在顶端

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