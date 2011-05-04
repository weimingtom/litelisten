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

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class HandlerService
{
	private srcMain main = null;

	public HandlerService(srcMain main)
	{
		this.main = main;
	}

	/* �������������Ե�Handler */
	private Handler hdlSetStartupLanguage = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.SetLanguage();
		}
	};

	/* ���Adapter��Handler */
	private Handler hdlAdapterClearHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getLstMusic().setAdapter(null);
		}
	};

	/* ����Adapter��Handler */
	private Handler hdlAdapterUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getLstMusic().setAdapter((MusicAdapter) msg.obj);
			main.SetAlbumIcon();
		}
	};

	/* ˢ��ʱ����ʾ�� Handler */
	private Handler hdlRefreshTime = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// ����ʱ��
			if (main.getMs().getPlayerStatus() != MusicService.STATUS_STOP)
			{
				if (main.getMs().isCanRefreshSeekBar())
				{
					main.getSkbMusic().setMax(main.getMs().GetTotalTime());
					main.getSkbMusic().setProgress(main.getMs().GetCurrTime());
				}
				main.getTxtTime().setText(main.getLs().IntegerToTime(main.getMs().GetCurrTime()) + " / " + main.getLs().IntegerToTime(main.getMs().GetTotalTime()));
				main.CallMusicNotify(main.getMs().getStrShownTitle() + " - " + main.getMs().getStrArtist(), main.getMs().getStrShownTitle() + " - " + main.getMs().getStrArtist(), main.getMs()
						.GetCurrTime(), main.getMs().GetTotalTime(), R.drawable.album_playing);
			}
			else
			{
				main.getSkbMusic().setMax(0);
				main.getSkbMusic().setProgress(0);
				main.getTxtTime().setText("00:00 / 00:00");
				main.CallMusicNotify(main.getResources().getString(R.string.global_app_name_no_version), main.getResources().getString(R.string.global_app_name_no_version), 0, 0, R.drawable.icon);
			}

			// ͨ���㲥���� Widget
			Intent intent = new Intent(IntentConst.INTENT_ACTION_REFRESH_TIME_N_TITLE);
			intent.putExtra("Time", main.getTxtTime().getText().toString());
			intent.putExtra("Title", main.getMs().getStrArtist() + " - " + main.getMs().getStrShownTitle());
			main.sendBroadcast(intent);
		}
	};

	/* ���ͬ���� Handler */
	private Handler hdlLRCSync = new Handler()
	{
		int LastPos = -1; // ��һ�β��Ŷ�����λ��
		int LastIndex = -1; // ��һ�β��ŵĸ���

		@Override
		public void handleMessage(Message msg)
		{
			if (main.getLs().isCanRefreshLRC())
			{// ����LRC����
				if (main.getCurrentShown() == 1)
				{
					LinearLayout.LayoutParams layLRC = (LinearLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // ��ȡscrLRC�ߴ����
					int OldY = layLRC.topMargin; // ��¼��ǰλ��
					layLRC.topMargin = msg.what;
					layLRC.height = main.getTxtLRC().getLineCount() * main.getTxtLRC().getLineHeight();

					main.getTxtLRC().setLayoutParams(layLRC);
					main.getTxtLRC().setLines(main.getTxtLRC().getLineCount());
					main.getTxtLRC().setText((SpannableStringBuilder) msg.obj);

					// �������������ò���
					if (LastIndex != msg.arg2)
					{
						LastPos = -1;
						LastIndex = msg.arg2;
					}

					// λ�ò���ͬʱ�Ų��Ŷ���
					int OffsetY = 200; // Ĭ��������ƫ��200dip
					if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
						OffsetY = 80; // ����ƫ��80dip

					if (LastPos != layLRC.topMargin && layLRC.topMargin != OffsetY)
					{
						if (main.getSp().getBoolean("chkUseAnimation", true))
						{
							Animation anim = new TranslateAnimation(0, 0, OldY - msg.what, 0); // �ӵ�ǰλ�õ�Ŀ��λ�ö���
							anim.setDuration(200);
							main.getTxtLRC().setAnimation(anim);
						}

						LastPos = layLRC.topMargin;
					}
				}
				else if (main.getCurrentShown() == 0)
					main.getTxtLRC().setText((SpannableStringBuilder) msg.obj); // ��ֹLRC���������¸����ɫ
			}

			// �����ǰ�ں�̨�������������С����
			if (main.getSp().getBoolean("IsRunBackground", false))
			{
				Intent intent = new Intent(IntentConst.INTENT_ACTION_REFRESH_LRC);
				intent.putExtra("LRCSmall", main.getLs().getStrCurrLRCSentenceSmall());
				intent.putExtra("LRCMedium", main.getLs().getStrCurrLRCSentenceMedium());
				intent.putExtra("LRCLarge", main.getLs().getStrCurrLRCSentenceLarge());
				main.sendBroadcast(intent);
			}

			if (main.getLs().isIsChanged())
			{
				if (main.getLs().isCanRefreshFloatRC())
					main.getFl().SetLRC(R.drawable.album_selected, main.getLs().getStrLRCToFloat1(), Color.WHITE, main.getLs().getStrLRCToFloat2(), Color.rgb(155, 215, 255));
				else
					main.getFl().SetLRC(R.drawable.album_selected, main.getLs().getStrLRCToFloat1(), Color.rgb(155, 215, 255), main.getLs().getStrLRCToFloat2(), Color.WHITE);
			}
		}
	};

	/* ˢ�º������� Handler */
	private Handler hdlLoadLRC = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			LinearLayout.LayoutParams layLRC = (LinearLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // ��ȡscrLRC�ߴ����
			if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
				layLRC.topMargin = 80;
			else
				layLRC.topMargin = 200;
			main.getTxtLRC().setLayoutParams(layLRC);
			main.getTxtLRC().setText(main.getLs().getStrLRC());
		}
	};

	/* ��ʾ������� Handler */
	private Handler hdlShowMain = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (main.getLaySplash().getVisibility() == View.VISIBLE)
			{
				main.getLaySplash().setVisibility(View.GONE);

				if (main.getSp().getBoolean("chkUseAnimation", true))
				{
					// ������ʧ����
					Animation animHide = new AlphaAnimation(1, 0);
					animHide.setDuration(srcMain.getAnimationTime());
					animHide.setInterpolator(new DecelerateInterpolator());

					// ������ʾ����
					Animation animShow = new AlphaAnimation(0, 1);
					animShow.setDuration(srcMain.getAnimationTime());
					animShow.setInterpolator(new DecelerateInterpolator());

					main.getLaySplash().setAnimation(animHide);
					main.getLayMain().setAnimation(animShow);
				}
			}
		}
	};

	/* ����/��ͣ�� Handler */
	private Handler hdlPlayPause = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getMs().PlayPause();
		}
	};

	/* ������һ�׵� Handler */
	private Handler hdlPlayLast = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getMs().Last();
		}
	};

	/* ������һ�׵� Handler */
	private Handler hdlPlayNext = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getMs().Next(false);
		}
	};

	public Handler getHdlPlayPause()
	{
		return hdlPlayPause;
	}

	public void setHdlPlayPause(Handler hdlPlayPause)
	{
		this.hdlPlayPause = hdlPlayPause;
	}

	public Handler getHdlPlayLast()
	{
		return hdlPlayLast;
	}

	public void setHdlPlayLast(Handler hdlPlayLast)
	{
		this.hdlPlayLast = hdlPlayLast;
	}

	public Handler getHdlPlayNext()
	{
		return hdlPlayNext;
	}

	public void setHdlPlayNext(Handler hdlPlayNext)
	{
		this.hdlPlayNext = hdlPlayNext;
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}

	public Handler getHdlAdapterClearHandler()
	{
		return hdlAdapterClearHandler;
	}

	public void setHdlAdapterClearHandler(Handler hdlAdapterClearHandler)
	{
		this.hdlAdapterClearHandler = hdlAdapterClearHandler;
	}

	public Handler getHdlAdapterUpdateHandler()
	{
		return hdlAdapterUpdateHandler;
	}

	public void setHdlAdapterUpdateHandler(Handler hdlAdapterUpdateHandler)
	{
		this.hdlAdapterUpdateHandler = hdlAdapterUpdateHandler;
	}

	public Handler getHdlSetStartupLanguage()
	{
		return hdlSetStartupLanguage;
	}

	public void setHdlSetStartupLanguage(Handler hdlSetStartupLanguage)
	{
		this.hdlSetStartupLanguage = hdlSetStartupLanguage;
	}

	public Handler getHdlRefreshTime()
	{
		return hdlRefreshTime;
	}

	public void setHdlRefreshTime(Handler hdlRefreshTime)
	{
		this.hdlRefreshTime = hdlRefreshTime;
	}

	public Handler getHdlLRCSync()
	{
		return hdlLRCSync;
	}

	public void setHdlLRCSync(Handler hdlLRCSync)
	{
		this.hdlLRCSync = hdlLRCSync;
	}

	public Handler getHdlLoadLRC()
	{
		return hdlLoadLRC;
	}

	public void setHdlLoadLRC(Handler hdlLoadLRC)
	{
		this.hdlLoadLRC = hdlLoadLRC;
	}

	public Handler getHdlShowMain()
	{
		return hdlShowMain;
	}

	public void setHdlShowMain(Handler hdlShowMain)
	{
		this.hdlShowMain = hdlShowMain;
	}
}