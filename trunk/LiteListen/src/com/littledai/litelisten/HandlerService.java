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

	/* 启动后设置语言的Handler */
	private Handler hdlSetStartupLanguage = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.SetLanguage();
		}
	};

	/* 显示扫描时提示信息的Handler */
	private Handler hdlShowScanHint = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getTxtScanHint().setVisibility(View.VISIBLE); // 显示提示标签
		}
	};

	/* 更新扫描时提示信息的Handler */
	private Handler hdlUpdateScanHint = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getTxtScanHint().setText(main.getResources().getString(R.string.srcmain_scanning_songs) + String.valueOf(msg.what));
		}
	};

	/* 隐藏扫描时提示信息的Handler */
	private Handler hdlHideScanHint = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getTxtScanHint().setVisibility(View.GONE); // 隐藏提示标签
		}
	};

	/* 清空Adapter的Handler */
	private Handler hdlAdapterClearHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getLstMusic().setAdapter(null);
		}
	};

	/* 更新Adapter的Handler */
	private Handler hdlAdapterUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getLstMusic().setAdapter((LDMusicAdapter) msg.obj);
			main.SetAlbumIcon();
		}
	};

	/* 刷新时间显示的 Handler */
	private Handler hdlRefreshTime = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// 设置时间
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

	/* 歌词同步的 Handler */
	private Handler hdlLRCSync = new Handler()
	{
		int LastPos = -1; // 上一次播放动画的位置
		int LastIndex = -1; // 上一次播放的歌曲

		@Override
		public void handleMessage(Message msg)
		{
			if (main.getLs().isCanRefreshLRC())
			{// 允许LRC滚动
				AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // 获取scrLRC尺寸参数
				int OldY = layLRC.y; // 记录当前位置
				layLRC.y = msg.what;
				layLRC.height = main.getTxtLRC().getLineCount() * main.getTxtLRC().getLineHeight();
				main.getTxtLRC().setLayoutParams(layLRC);
				main.getTxtLRC().setLines(main.getTxtLRC().getLineCount());
				main.getTxtLRC().setText((SpannableStringBuilder) msg.obj);

				// 歌曲更换后重置参数
				if (LastIndex != msg.arg2)
				{
					LastPos = -1;
					LastIndex = msg.arg2;
				}

				// 位置不相同时才播放动画
				int OffsetY = 200; // 默认竖屏，偏移200dip
				if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
					OffsetY = 80; // 横屏偏移80dip

				if (LastPos != layLRC.y && layLRC.y != OffsetY)
				{
					if (main.getSp().getBoolean("chkUseAnimation", true))
					{
						Animation anim = new TranslateAnimation(0, 0, OldY - msg.what, 0); // 从当前位置到目标位置动画
						anim.setDuration(200);
						main.getTxtLRC().setAnimation(anim);
					}

					LastPos = layLRC.y;
				}
			}
			else
				// 禁止LRC滚动但更新歌词颜色
				main.getTxtLRC().setText((SpannableStringBuilder) msg.obj);
		}
	};

	/* 刷新横竖屏的 Handler */
	private Handler hdlLoadLRC = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // 获取scrLRC尺寸参数
			if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
				layLRC.y = 80;
			else
				layLRC.y = 200;
			main.getTxtLRC().setLayoutParams(layLRC);
			main.getTxtLRC().setText(main.getLs().getStrLRC());
		}
	};

	/* 显示主界面的 Handler */
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
					// 托盘消失动画
					Animation animHide = new AlphaAnimation(1, 0);
					animHide.setDuration(srcMain.getAnimationTime());
					animHide.setInterpolator(new DecelerateInterpolator());

					// 托盘显示动画
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