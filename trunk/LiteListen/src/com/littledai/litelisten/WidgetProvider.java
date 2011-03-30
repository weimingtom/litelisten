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
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider
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
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_4_2);

			// 单击歌词显示主界面
			Intent intent = new Intent(context, srcMain.class);
			PendingIntent pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.txtWidgetLRC, pdItent);

			// 上一首，使用控制消息
			intent = new Intent(context, WidgetProvider.class);
			intent.setAction(MSG_LAST);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetLast, pdItent);

			// 播放，使用控制消息
			intent = new Intent(context, WidgetProvider.class);
			intent.setAction(MSG_PLAY);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetPlay, pdItent);

			// 暂停，使用控制消息
			intent = new Intent(context, WidgetProvider.class);
			intent.setAction(MSG_PAUSE);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetPause, pdItent);

			// 下一首，使用控制消息
			intent = new Intent(context, WidgetProvider.class);
			intent.setAction(MSG_NEXT);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetNext, pdItent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_4_2);
		SharedPreferences sp = context.getSharedPreferences("com.littledai.litelisten_preferences", 0);

		if (intent.getAction().equals(IntentConst.INTENT_ACTION_REFRESH_TIME_N_TITLE))
		{// 标题和时间
			rv.setTextViewText(R.id.txtWidgetTime, intent.getStringExtra("Time"));
			rv.setTextViewText(R.id.txtWidgetTitle, intent.getStringExtra("Title"));
		}
		else if (intent.getAction().equals(IntentConst.INTENT_ACTION_REFRESH_LRC)) // 歌词
			rv.setTextViewText(R.id.txtWidgetLRC, Html.fromHtml(intent.getStringExtra("LRC")));
		else if (intent.getAction().equals(MSG_LAST))
		{
			Editor edt = sp.edit();
			edt.putInt("MusicControl", 0); // 上一首
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_PLAY))
		{
			Editor edt = sp.edit();
			edt.putInt("MusicControl", 1); // 播放/暂停
			edt.commit();
			rv.setViewVisibility(R.id.btnWidgetPlay, View.GONE);
			rv.setViewVisibility(R.id.btnWidgetPause, View.VISIBLE);
		}
		else if (intent.getAction().equals(MSG_NEXT))
		{
			Editor edt = sp.edit();
			edt.putInt("MusicControl", 2); // 下一首
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_PAUSE))
		{
			Editor edt = sp.edit();
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

		ComponentName cname = new ComponentName(context, WidgetProvider.class);
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