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
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider
{
	private String MSG_LAST = "MSG_LAST";
	private String MSG_PLAY = "MSG_PLAY";
	private String MSG_NEXT = "MSG_NEXT";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		final int WidgetCount = appWidgetIds.length;
		for (int i = 0; i < WidgetCount; i++)
		{
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_4_2);

			// ���������ʾ������
			Intent intent = new Intent(context, srcMain.class);
			PendingIntent pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.txtWidgetLRC, pdItent);

			// ��һ�ף�ʹ�ÿ�����Ϣ
			intent = new Intent(context, WidgetProvider.class);
			intent.setAction(MSG_LAST);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetLast, pdItent);

			// ���ţ�ʹ�ÿ�����Ϣ
			intent = new Intent(context, WidgetProvider.class);
			intent.setAction(MSG_PLAY);
			pdItent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.btnWidgetPlay, pdItent);

			// ��һ�ף�ʹ�ÿ�����Ϣ
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
		{// �����ʱ��
			rv.setTextViewText(R.id.txtWidgetTime, intent.getStringExtra("Time"));
			rv.setTextViewText(R.id.txtWidgetTitle, intent.getStringExtra("Title"));
		}
		else if (intent.getAction().equals(IntentConst.INTENT_ACTION_REFRESH_LRC)) // ���
			rv.setTextViewText(R.id.txtWidgetLRC, Html.fromHtml(intent.getStringExtra("LRC")));
		else if (intent.getAction().equals(MSG_LAST))
		{
			Editor edt = sp.edit();
			edt.putInt("MusicControl", 0); // ��һ��
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_PLAY))
		{
			Editor edt = sp.edit();
			edt.putInt("MusicControl", 1); // ����/��ͣ
			edt.commit();
		}
		else if (intent.getAction().equals(MSG_NEXT))
		{
			Editor edt = sp.edit();
			edt.putInt("MusicControl", 2); // ��һ��
			edt.commit();
		}

		if (!sp.getBoolean("Started", false))
		{
			Intent intentCallMain = new Intent(context, srcMain.class);
			context.startActivity(intentCallMain);
		}

		ComponentName cname = new ComponentName(context, WidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(cname, rv);

		super.onReceive(context, intent);
	}
}