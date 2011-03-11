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

import java.util.HashMap;
import java.util.Map;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;

public class MusicService
{
	public static final int STATUS_PLAY = 0;
	public static final int STATUS_STOP = 1;
	public static final int STATUS_PAUSE = 2;

	private int strPlayerStatus = STATUS_STOP;
	private MediaPlayer mp = new MediaPlayer();
	private int CurrIndex = 0; // ��ǰ���ŵ��������
	private srcMain main = null;
	private String strShownTitle = ""; // ���ļ���
	private String strLRCPath = ""; // ���·��
	private boolean CanRefreshSeekBar = true;
	private boolean CanRefreshTime = false; // �Ƿ�����ˢ��ʱ�䣨�ر�ʱͬʱ������̣߳�
	private boolean IsLast = false; // �Ƿ�������һ��
	private int CurrentIndexOfPlayedList = -1; // ��ʱ�����б�ǰ���ڲ��ŵ����
	private int PlayedListCount = -1; // ��ʱ�����б���ռ�õĳ���
	private int PlayedList[] = new int[1000]; // �����ʷ���ż�¼����ʱ�����б�

	/* ���캯�����½�ʱ��ȡ·�����б� */
	public MusicService(srcMain main)
	{
		this.main = main;
		strShownTitle = main.getResources().getString(R.string.global_app_name);

		/* �������� */
		mp.setOnCompletionListener(new OnCompletionListener()
		{
			public void onCompletion(MediaPlayer mp)
			{
				if (strPlayerStatus == MusicService.STATUS_PLAY)
					Next(true);
			}
		});
	}

	/* ���� */
	public void Play(int index)
	{
		try
		{
			if (strPlayerStatus == MusicService.STATUS_PAUSE && CurrIndex == index)
			{
				mp.start();
				CanRefreshTime = true;

				main.getBtnPlay().setVisibility(View.GONE);
				main.getBtnPause().setVisibility(View.VISIBLE);
				setStrPlayerStatus(MusicService.STATUS_PLAY);
				main.SetAlbumIcon();

				new Thread()
				{
					public void run()
					{
						int index = CurrIndex;

						while (CanRefreshTime)
						{
							try
							{
								// ÿ�ζ�ҪУ���Ƿ�ò����߳�רΪ�Լ�����
								if (index != CurrIndex)
									break;

								main.getHs().getHdlRefreshTime().sendEmptyMessage(0); // ֪ͨHandler���½���
								main.getLs().RefreshLRC();

								sleep(250);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}.start();
			}
			else if (strPlayerStatus == MusicService.STATUS_STOP || CurrIndex != index) // ֹͣ�����ڲ��ŵ�ѡ������뵱ǰ��һ�²��ܼ���
			{
				Map<String, Object> map = new HashMap<String, Object>();
				map = main.getLstSong().get(index);

				mp.reset();
				mp.setDataSource((String) map.get("MusicPath"));
				mp.prepare();
				mp.start();

				CanRefreshTime = true;

				if (!IsLast)
				{
					PlayedListCount++;
					CurrentIndexOfPlayedList = PlayedListCount;
					PlayedList[PlayedListCount] = index;
				}

				CurrIndex = index;

				main.getBtnPlay().setVisibility(View.GONE);
				main.getBtnPause().setVisibility(View.VISIBLE);
				setStrPlayerStatus(MusicService.STATUS_PLAY);
				setStrLRCPath((String) map.get("LRCPath"));
				setStrShownTitle((String) map.get("Title"));

				main.getLs().setStrLRCPath(strLRCPath);
				main.getTxtTitle().setText(getStrShownTitle());
				main.SetAlbumIcon();

				new Thread()
				{
					public void run()
					{
						int index = CurrIndex;

						while (CanRefreshTime)
						{
							try
							{
								// ÿ�ζ�ҪУ���Ƿ�ò����߳�רΪ�Լ�����
								if (index != CurrIndex)
									break;

								main.getHs().getHdlRefreshTime().sendEmptyMessage(0); // ֪ͨHandler���½���
								main.getLs().RefreshLRC();

								sleep(250);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}.start();
			}

			if (main.getSp().getBoolean("chkAutoSwitchToLRC", false))
				main.List2LRCSwitcher();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			getMain().getBtnPlay().setVisibility(View.VISIBLE);
			getMain().getBtnPause().setVisibility(View.GONE);
			setStrPlayerStatus(MusicService.STATUS_STOP);
		}
	}

	/* ��һ�� */
	public void Next(boolean AutoNext)
	{
		int index = -1;

		String ModeIndex = main.getSp().getString("lstPlayMode", "1");
		if (ModeIndex.equals("0"))
		{
			if (CurrIndex < main.getLstSong().size() - 1)
				index = CurrIndex + 1;
			else
			{
				if (AutoNext)
				{
					Stop();
					return;
				}
				else
					index = 0;
			}
		}
		else if (ModeIndex.equals("1"))
		{
			if (CurrIndex < main.getLstSong().size() - 1)
				index = CurrIndex + 1;
			else if (CurrIndex == main.getLstSong().size() - 1)
				index = 0;
		}
		else if (ModeIndex.equals("2"))
		{
			if (AutoNext)
			{
				Stop();
				return;
			}
			else
			{
				if (CurrIndex < main.getLstSong().size() - 1)
					index = CurrIndex + 1;
				else if (CurrIndex == main.getLstSong().size() - 1)
					index = 0;
			}
		}
		else if (ModeIndex.equals("3"))
		{
			if (AutoNext)
				index = CurrIndex;
			else
			{
				if (CurrIndex < main.getLstSong().size() - 1)
					index = CurrIndex + 1;
				else if (CurrIndex == main.getLstSong().size() - 1)
					index = 0;
			}
		}
		else if (ModeIndex.equals("4"))
			index = GetRandomIndex(0, main.getLstSong().size() - 1);

		Stop();
		Play(index);
		CurrIndex = index;
	}

	/* ��һ�� */
	public void Last()
	{
		// ��ǰ�������ų������������²���
		if (mp.getCurrentPosition() > 20000)
		{
			Stop();
			Play(CurrIndex);
		}
		else
		{// ���򲥷���һ�ײ��Ź��ĸ���
			Stop();

			CurrentIndexOfPlayedList--;
			if (CurrentIndexOfPlayedList < 0)
				CurrentIndexOfPlayedList = 0;

			IsLast = true;
			Play(PlayedList[CurrentIndexOfPlayedList]); // �Ӳ�����ʷ��¼���ҵ��ϴβ��ŵ����
			IsLast = false;
			CurrIndex = PlayedList[CurrentIndexOfPlayedList];
		}
	}

	/* ֹͣ */
	public void Stop()
	{
		mp.reset();
		CanRefreshTime = false;
		getMain().getBtnPlay().setVisibility(View.VISIBLE);
		getMain().getBtnPause().setVisibility(View.GONE);
		setStrPlayerStatus(MusicService.STATUS_STOP);
		main.SetAlbumIcon();
	}

	/* ��ͣ */
	public void Pause()
	{
		if (strPlayerStatus == MusicService.STATUS_PLAY)
		{
			mp.pause();
			CanRefreshTime = false;
			getMain().getBtnPlay().setVisibility(View.VISIBLE);
			getMain().getBtnPause().setVisibility(View.GONE);
			setStrPlayerStatus(MusicService.STATUS_PAUSE);
			main.SetAlbumIcon();
		}
	}

	/* ����+��ͣ������������ */
	public void PlayPause()
	{
		if (strPlayerStatus == MusicService.STATUS_PLAY)
			Pause();
		else
			Play(CurrIndex);
	}

	/* �����ȡ������� */
	public int GetRandomIndex(int min, int max)
	{
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	/* ��ȡ��ǰ���ŵ�ʱ�� */
	public int GetCurrTime()
	{
		return mp.getCurrentPosition();
	}

	/* ��ȡ������ʱ�� */
	public int GetTotalTime()
	{
		return mp.getDuration();
	}

	public int getStrPlayerStatus()
	{
		return strPlayerStatus;
	}

	public void setStrPlayerStatus(int strPlayerStatus)
	{
		this.strPlayerStatus = strPlayerStatus;
	}

	public MediaPlayer getMp()
	{
		return mp;
	}

	public void setMp(MediaPlayer mp)
	{
		this.mp = mp;
	}

	public int getCurrIndex()
	{
		return CurrIndex;
	}

	public void setCurrIndex(int currIndex)
	{
		CurrIndex = currIndex;
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}

	public String getStrShownTitle()
	{
		return strShownTitle;
	}

	public void setStrShownTitle(String strShownTitle)
	{
		this.strShownTitle = strShownTitle;
	}

	public String getStrLRCPath()
	{
		return strLRCPath;
	}

	public void setStrLRCPath(String strLRCPath)
	{
		this.strLRCPath = strLRCPath;
	}

	public boolean isCanRefreshSeekBar()
	{
		return CanRefreshSeekBar;
	}

	public void setCanRefreshSeekBar(boolean canRefreshSeekBar)
	{
		CanRefreshSeekBar = canRefreshSeekBar;
	}

	public boolean isCanRefreshTime()
	{
		return CanRefreshTime;
	}

	public void setCanRefreshTime(boolean canRefreshTime)
	{
		CanRefreshTime = canRefreshTime;
	}
}