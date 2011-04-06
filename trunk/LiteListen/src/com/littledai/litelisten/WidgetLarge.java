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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class WidgetLarge extends AppWidgetProvider
{
	private String MSG_LAST = "MSG_LAST";
	private String MSG_PLAY = "MSG_PLAY";
	private String MSG_PAUSE = "MSG_PAUSE";
	private String MSG_NEXT = "MSG_NEXT";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		final int WidgetCount = appWidgetIds.length;
		for (int i = 0; i < WidgetCount; i++)
		{
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_large);

			// 单击歌词显示主界面
			Intent intent = new Intent(context, srcMain.class);
			PendingIntent pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.txtWidgetLRC, pdItent);

			// 上一首，使用控制消息
			intent = new Intent(context, WidgetLarge.class);
			intent.setAction(MSG_LAST);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetLast, pdItent);

			// 播放，使用控制消息
			intent = new Intent(context, WidgetLarge.class);
			intent.setAction(MSG_PLAY);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetPlay, pdItent);

			// 暂停，使用控制消息
			intent = new Intent(context, WidgetLarge.class);
			intent.setAction(MSG_PAUSE);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetPause, pdItent);

			// 下一首，使用控制消息
			intent = new Intent(context, WidgetLarge.class);
			intent.setAction(MSG_NEXT);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetNext, pdItent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_large);
		SharedPreferences sp = context.getSharedPreferences("com.littledai.litelisten_preferences", 0);

		// 屏幕旋转后重新绑定Widget上的控件动作
		int ScreenOrantation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();

		// 更新屏幕方向
		Editor edt = sp.edit();
		edt.putInt("ScreenOrantation", ScreenOrantation);
		edt.commit();

		// 单击歌词显示主界面
		Intent itt = new Intent(context, srcMain.class);
		PendingIntent pdItent = PendingIntent.getActivity(context, 0, itt, 0);
		rv.setOnClickPendingIntent(R.id.txtWidgetLRC, pdItent);

		// 上一首，使用控制消息
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_LAST);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetLast, pdItent);

		// 播放，使用控制消息
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_PLAY);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetPlay, pdItent);

		// 暂停，使用控制消息
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_PAUSE);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetPause, pdItent);

		// 下一首，使用控制消息
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_NEXT);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetNext, pdItent);

		if (intent.getAction().equals(IntentConst.INTENT_ACTION_REFRESH_TIME_N_TITLE))
		{// 标题和时间
			rv.setTextViewText(R.id.txtWidgetTime, intent.getStringExtra("Time"));
			rv.setTextViewText(R.id.txtWidgetTitle, intent.getStringExtra("Title"));
		}
		else if (intent.getAction().equals(IntentConst.INTENT_ACTION_REFRESH_LRC)) // 歌词
			rv.setTextViewText(R.id.txtWidgetLRC, Html.fromHtml(intent.getStringExtra("LRCLarge")));
		else if (intent.getAction().equals(MSG_LAST))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 0); // 上一首
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_PLAY))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 1); // 播放/暂停
			edt.commit();
			rv.setViewVisibility(R.id.btnWidgetPlay, View.GONE);
			rv.setViewVisibility(R.id.btnWidgetPause, View.VISIBLE);
		}
		else if (intent.getAction().equals(MSG_NEXT))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 2); // 下一首
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_PAUSE))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 1); // 暂停
			edt.commit();
			rv.setViewVisibility(R.id.btnWidgetPause, View.GONE);
			rv.setViewVisibility(R.id.btnWidgetPlay, View.VISIBLE);
		}
		else if (intent.getAction().equals(IntentConst.getINTENT_ACTION_IS_PLAYING()))
		{
			rv.setViewVisibility(R.id.btnWidgetPlay, View.GONE);
			rv.setViewVisibility(R.id.btnWidgetPause, View.VISIBLE);
		}
		else if (intent.getAction().equals(IntentConst.getINTENT_ACTION_NOT_PLAYING()))
		{
			rv.setViewVisibility(R.id.btnWidgetPause, View.GONE);
			rv.setViewVisibility(R.id.btnWidgetPlay, View.VISIBLE);
		}

		ComponentName cname = new ComponentName(context, WidgetLarge.class);
		AppWidgetManager.getInstance(context).updateAppWidget(cname, rv);

		super.onReceive(context, intent);
	}

	public String getMSG_LAST()
	{
		return MSG_LAST;
	}

	public void setMSG_LAST(String mSGLAST)
	{
		MSG_LAST = mSGLAST;
	}

	public String getMSG_PLAY()
	{
		return MSG_PLAY;
	}

	public void setMSG_PLAY(String mSGPLAY)
	{
		MSG_PLAY = mSGPLAY;
	}

	public String getMSG_PAUSE()
	{
		return MSG_PAUSE;
	}

	public void setMSG_PAUSE(String mSGPAUSE)
	{
		MSG_PAUSE = mSGPAUSE;
	}

	public String getMSG_NEXT()
	{
		return MSG_NEXT;
	}

	public void setMSG_NEXT(String mSGNEXT)
	{
		MSG_NEXT = mSGNEXT;
	}
}