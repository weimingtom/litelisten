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

			// ���������ʾ������
			Intent intent = new Intent(context, srcMain.class);
			PendingIntent pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.txtWidgetLRC, pdItent);

			// ��һ�ף�ʹ�ÿ�����Ϣ
			intent = new Intent(context, WidgetLarge.class);
			intent.setAction(MSG_LAST);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetLast, pdItent);

			// ���ţ�ʹ�ÿ�����Ϣ
			intent = new Intent(context, WidgetLarge.class);
			intent.setAction(MSG_PLAY);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetPlay, pdItent);

			// ��ͣ��ʹ�ÿ�����Ϣ
			intent = new Intent(context, WidgetLarge.class);
			intent.setAction(MSG_PAUSE);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetPause, pdItent);

			// ��һ�ף�ʹ�ÿ�����Ϣ
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

		// ��Ļ��ת�����°�Widget�ϵĿؼ�����
		int ScreenOrantation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();

		// ������Ļ����
		Editor edt = sp.edit();
		edt.putInt("ScreenOrantation", ScreenOrantation);
		edt.commit();

		// ���������ʾ������
		Intent itt = new Intent(context, srcMain.class);
		PendingIntent pdItent = PendingIntent.getActivity(context, 0, itt, 0);
		rv.setOnClickPendingIntent(R.id.txtWidgetLRC, pdItent);

		// ��һ�ף�ʹ�ÿ�����Ϣ
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_LAST);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetLast, pdItent);

		// ���ţ�ʹ�ÿ�����Ϣ
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_PLAY);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetPlay, pdItent);

		// ��ͣ��ʹ�ÿ�����Ϣ
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_PAUSE);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetPause, pdItent);

		// ��һ�ף�ʹ�ÿ�����Ϣ
		itt = new Intent(context, WidgetLarge.class);
		itt.setAction(MSG_NEXT);
		pdItent = PendingIntent.getBroadcast(context, 0, itt, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.btnWidgetNext, pdItent);

		if (intent.getAction().equals(IntentConst.INTENT_ACTION_REFRESH_TIME_N_TITLE))
		{// �����ʱ��
			rv.setTextViewText(R.id.txtWidgetTime, intent.getStringExtra("Time"));
			rv.setTextViewText(R.id.txtWidgetTitle, intent.getStringExtra("Title"));
		}
		else if (intent.getAction().equals(IntentConst.INTENT_ACTION_REFRESH_LRC)) // ���
			rv.setTextViewText(R.id.txtWidgetLRC, Html.fromHtml(intent.getStringExtra("LRCLarge")));
		else if (intent.getAction().equals(MSG_LAST))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 0); // ��һ��
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_PLAY))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 1); // ����/��ͣ
			edt.commit();
			rv.setViewVisibility(R.id.btnWidgetPlay, View.GONE);
			rv.setViewVisibility(R.id.btnWidgetPause, View.VISIBLE);
		}
		else if (intent.getAction().equals(MSG_NEXT))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 2); // ��һ��
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_PAUSE))
		{
			edt = sp.edit();
			edt.putInt("MusicControl", 1); // ��ͣ
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