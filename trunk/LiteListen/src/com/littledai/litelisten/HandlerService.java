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

import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
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

	/* ��ʾɨ��ʱ��ʾ��Ϣ��Handler */
	private Handler hdlShowScanHint = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getTxtScanHint().setVisibility(View.VISIBLE); // ��ʾ��ʾ��ǩ
		}
	};

	/* ����ɨ��ʱ��ʾ��Ϣ��Handler */
	private Handler hdlUpdateScanHint = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getTxtScanHint().setText(main.getResources().getString(R.string.srcmain_scanning_songs) + String.valueOf(msg.what));
		}
	};

	/* ����ɨ��ʱ��ʾ��Ϣ��Handler */
	private Handler hdlHideScanHint = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getTxtScanHint().setVisibility(View.GONE); // ������ʾ��ǩ
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
			main.getLstMusic().setAdapter((LDMusicAdapter) msg.obj);
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
			if (main.getMs().getStrPlayerStatus() != MusicService.STATUS_STOP)
			{
				if (main.getMs().isCanRefreshSeekBar())
				{
					main.getSkbMusic().setMax(main.getMs().GetTotalTime());
					main.getSkbMusic().setProgress(main.getMs().GetCurrTime());
				}
				main.getTxtTime().setText(main.getLs().IntegerToTime(main.getMs().GetCurrTime()) + " / " + main.getLs().IntegerToTime(main.getMs().GetTotalTime()));
			}
			else
			{
				main.getSkbMusic().setMax(0);
				main.getSkbMusic().setProgress(0);
				main.getTxtTime().setText("00:00 / 00:00");
			}
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
				AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // ��ȡscrLRC�ߴ����
				int OldY = layLRC.y; // ��¼��ǰλ��
				layLRC.y = msg.what;
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

				if (LastPos != layLRC.y && layLRC.y != OffsetY)
				{
					if (main.getSp().getBoolean("chkUseAnimation", true))
					{
						Animation anim = new TranslateAnimation(0, 0, OldY - msg.what, 0); // �ӵ�ǰλ�õ�Ŀ��λ�ö���
						anim.setDuration(200);
						main.getTxtLRC().setAnimation(anim);
					}

					LastPos = layLRC.y;
				}
			}
			else
				// ��ֹLRC���������¸����ɫ
				main.getTxtLRC().setText((SpannableStringBuilder) msg.obj);
		}
	};

	/* ˢ�º������� Handler */
	private Handler hdlLoadLRC = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // ��ȡscrLRC�ߴ����
			if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
				layLRC.y = 80;
			else
				layLRC.y = 200;
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

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}

	public Handler getHdlShowScanHint()
	{
		return hdlShowScanHint;
	}

	public void setHdlShowScanHint(Handler hdlShowScanHint)
	{
		this.hdlShowScanHint = hdlShowScanHint;
	}

	public Handler getHdlUpdateScanHint()
	{
		return hdlUpdateScanHint;
	}

	public void setHdlUpdateScanHint(Handler hdlUpdateScanHint)
	{
		this.hdlUpdateScanHint = hdlUpdateScanHint;
	}

	public Handler getHdlHideScanHint()
	{
		return hdlHideScanHint;
	}

	public void setHdlHideScanHint(Handler hdlHideScanHint)
	{
		this.hdlHideScanHint = hdlHideScanHint;
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