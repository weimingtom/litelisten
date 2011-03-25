package com.littledai.litelisten;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider
{
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		final int WidgetCount = appWidgetIds.length;
		for (int i = 0; i < WidgetCount; i++)
		{
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_4_2);
			Intent intent = new Intent(context, srcMain.class);
			PendingIntent pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.txtWidgetLRC, pdItent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_4_2);

		if (intent.getAction().equals(HandlerService.INTENT_ACTION_REFRESH_TIME_N_TITLE))
		{// 标题和时间
			rv.setTextViewText(R.id.txtWidgetTime, intent.getStringExtra("Time"));
			rv.setTextViewText(R.id.txtWidgetTitle, intent.getStringExtra("Title"));
		}
		else if (intent.getAction().equals(HandlerService.INTENT_ACTION_REFRESH_LRC)) // 歌词
			rv.setTextViewText(R.id.txtWidgetLRC, Html.fromHtml(intent.getStringExtra("LRC")) );

		ComponentName cname = new ComponentName(context, WidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(cname, rv);

		super.onReceive(context, intent);
	}

	@Override
	public void onDisabled(Context context)
	{
		System.exit(0); // 退出程序
	}
}