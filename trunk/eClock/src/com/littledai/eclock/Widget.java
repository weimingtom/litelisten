package com.littledai.eclock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider
{
	private static final String strPackageName="cn.aaronsoft.atime";
	private Context ctxGlobal;
	
	int RefreshRate=500;  //小部件刷新率
	
	/*更新小部件*/
	@Override
	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds)
	{
		final int WidgetCount=appWidgetIds.length;
		for(int i=0;i<WidgetCount;i++)
		{
			RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.widget);
			Intent itt=new Intent(context,Settings.class);  //打开选项设置
			PendingIntent pdItent=PendingIntent.getActivity(context,0,itt,0);
			rv.setOnClickPendingIntent(R.id.imgHH,pdItent);
			rv.setOnClickPendingIntent(R.id.imgH,pdItent);
			rv.setOnClickPendingIntent(R.id.imgMM,pdItent);
			rv.setOnClickPendingIntent(R.id.imgM,pdItent);
			rv.setOnClickPendingIntent(R.id.imgSplit,pdItent);
			rv.setOnClickPendingIntent(R.id.labDate,pdItent);
			rv.setOnClickPendingIntent(R.id.frmTime,pdItent);
			rv.setOnClickPendingIntent(R.id.frmWidget,pdItent);

			appWidgetManager.updateAppWidget(appWidgetIds[i],rv);
		}

		ctxGlobal=context;
		trdRefresh.start();
	}
	
	/*按收到的消息处理小部件控件事件*/
	@Override
	public void onReceive(Context context,Intent intent)
	{
		if(intent.getAction().equals(strPackageName) || intent.getAction().equals(Intent.ACTION_TIME_TICK))
		{
			RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.widget);
			SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //读取配置文件
	
			boolean blTimeFormat=sp.getBoolean("chkTimeFormat",true);  //时间格式
        	String strDateFormat=sp.getString("lstDateFormat","0");  //日期格式
        	String strDateSplitter=sp.getString("txtDateSplitter","");  //日期分隔符
        	boolean blReduceResUse=sp.getBoolean("chkReduceResUse",true);  //降低资源占用
        	String strSkin=sp.getString("lstSkin","0");  //皮肤
        	boolean blShowBG=sp.getBoolean("chkShowBG",true);  //显示小部件的背景

        	DateFormat df;  //时间格式
        	if(blTimeFormat)  //24小时制
        		df=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss EEEE");  //HH表示24小时制，hh为12小时制
        	else  //12小时制
        		df=new SimpleDateFormat("yyyy.MM.dd hh:mm:ss EEEE");
        	String strTime=df.format(new Date());  //获取当前时间字符串
        	String strYear=strTime.substring(0,4);  //年
        	String strMonth=strTime.substring(5,7);  //月
        	String strDay=strTime.substring(8,10);  //日
        	String strHour1=strTime.substring(11,12);  //时（十位）
        	String strHour2=strTime.substring(12,13);  //时（个位）
        	String strMinite1=strTime.substring(14,15);  //分（十位）
        	String strMinite2=strTime.substring(15,16);  //分（个位）
        	String strWeek=GetChineseWeek(strTime.substring(20,strTime.length()));  //星期
        	
        	String strDateString="";  //日期字符串
        	if(strDateFormat.equals("0"))  //文本表达的日期格式
        		strDateString=strYear+"年"+Integer.parseInt(strMonth)+"月"+Integer.parseInt(strDay)+"日 "+strWeek;  //年月日
        	else if(strDateFormat.equals("1"))  //顺序简单表达
        		strDateString=strYear+strDateSplitter+strMonth+strDateSplitter+strDay+" "+strWeek;  //年-分隔-月-分隔-日
        	else  //倒序简单表达
        		strDateString=strMonth+strDateSplitter+strDay+strDateSplitter+strYear+" "+strWeek;  //月-分隔-日-分隔-年
        	
        	if(blReduceResUse)  //降低资源占用时每秒刷新
        		RefreshRate=1000;
        	else  //否则0.5秒刷新一次
        		RefreshRate=500;
        	
        	String strColor=sp.getString("strDateColor","#0099FF");  //获取默认颜色
        	rv.setTextColor(R.id.labDate,Color.parseColor(strColor));  //日期文字颜色
        	rv.setTextViewText(R.id.labDate,strDateString);  //设置显示时间

        	//设置皮肤
        	if(strSkin.equals("0"))
        	{//摩登蓝
    			rv.setImageViewResource(R.id.imgHH,GetSkinModernBlue(strHour1));
    			rv.setImageViewResource(R.id.imgH,GetSkinModernBlue(strHour2));
    			rv.setImageViewResource(R.id.imgSplit,GetSkinModernBlue(":"));
    			rv.setImageViewResource(R.id.imgMM,GetSkinModernBlue(strMinite1));
    			rv.setImageViewResource(R.id.imgM,GetSkinModernBlue(strMinite2));
    			
    			if(blShowBG)
    				rv.setImageViewResource(R.id.imgBG,GetSkinModernBlue("BG"));
    			else
    				rv.setImageViewResource(R.id.imgBG,R.drawable.bg_transparent);
        	}
        	else if(strSkin.equals("1"))
        	{//摩登红
    			rv.setImageViewResource(R.id.imgHH,GetSkinModernRed(strHour1));
    			rv.setImageViewResource(R.id.imgH,GetSkinModernRed(strHour2));
    			rv.setImageViewResource(R.id.imgSplit,GetSkinModernRed(":"));
    			rv.setImageViewResource(R.id.imgMM,GetSkinModernRed(strMinite1));
    			rv.setImageViewResource(R.id.imgM,GetSkinModernRed(strMinite2));
    			
    			if(blShowBG)
    				rv.setImageViewResource(R.id.imgBG,GetSkinModernRed("BG"));
    			else
    				rv.setImageViewResource(R.id.imgBG,R.drawable.bg_transparent);
        	}
        	else if(strSkin.equals("2"))
        	{//摩登红
    			rv.setImageViewResource(R.id.imgHH,GetSkinModernYellow(strHour1));
    			rv.setImageViewResource(R.id.imgH,GetSkinModernYellow(strHour2));
    			rv.setImageViewResource(R.id.imgSplit,GetSkinModernYellow(":"));
    			rv.setImageViewResource(R.id.imgMM,GetSkinModernYellow(strMinite1));
    			rv.setImageViewResource(R.id.imgM,GetSkinModernYellow(strMinite2));
    			
    			if(blShowBG)
    				rv.setImageViewResource(R.id.imgBG,GetSkinModernYellow("BG"));
    			else
    				rv.setImageViewResource(R.id.imgBG,R.drawable.bg_transparent);
        	}
        	else if(strSkin.equals("3"))
        	{//半透蓝
    			rv.setImageViewResource(R.id.imgHH,GetSkinHalfBlue(strHour1));
    			rv.setImageViewResource(R.id.imgH,GetSkinHalfBlue(strHour2));
    			rv.setImageViewResource(R.id.imgSplit,GetSkinHalfBlue(":"));
    			rv.setImageViewResource(R.id.imgMM,GetSkinHalfBlue(strMinite1));
    			rv.setImageViewResource(R.id.imgM,GetSkinHalfBlue(strMinite2));
    			
    			if(blShowBG)
    				rv.setImageViewResource(R.id.imgBG,GetSkinHalfBlue("BG"));
    			else
    				rv.setImageViewResource(R.id.imgBG,R.drawable.bg_transparent);
        	}

			ComponentName Me=new ComponentName(context,Widget.class);
			AppWidgetManager.getInstance(context).updateAppWidget(Me,rv);
		}
		else if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED))  //删除小部件
			System.exit(0);  //退出程序
		
		super.onReceive(context,intent);
	}
	
	/*获取摩登蓝皮肤*/
	public int GetSkinModernBlue(String strNumber)
	{
		if(strNumber.equals("0"))
			return R.drawable.modern_blue_n0;
		else if(strNumber.equals("1"))
			return R.drawable.modern_blue_n1;
		else if(strNumber.equals("2"))
			return R.drawable.modern_blue_n2;
		else if(strNumber.equals("3"))
			return R.drawable.modern_blue_n3;
		else if(strNumber.equals("4"))
			return R.drawable.modern_blue_n4;
		else if(strNumber.equals("5"))
			return R.drawable.modern_blue_n5;
		else if(strNumber.equals("6"))
			return R.drawable.modern_blue_n6;
		else if(strNumber.equals("7"))
			return R.drawable.modern_blue_n7;
		else if(strNumber.equals("8"))
			return R.drawable.modern_blue_n8;
		else if(strNumber.equals("9"))
			return R.drawable.modern_blue_n9;
		else if(strNumber.equals(":"))
			return R.drawable.modern_blue_split;
		else
			return R.drawable.modern_blue_bg;
	}
	
	/*获取摩登红皮肤*/
	public int GetSkinModernRed(String strNumber)
	{
		if(strNumber.equals("0"))
			return R.drawable.modern_red_n0;
		else if(strNumber.equals("1"))
			return R.drawable.modern_red_n1;
		else if(strNumber.equals("2"))
			return R.drawable.modern_red_n2;
		else if(strNumber.equals("3"))
			return R.drawable.modern_red_n3;
		else if(strNumber.equals("4"))
			return R.drawable.modern_red_n4;
		else if(strNumber.equals("5"))
			return R.drawable.modern_red_n5;
		else if(strNumber.equals("6"))
			return R.drawable.modern_red_n6;
		else if(strNumber.equals("7"))
			return R.drawable.modern_red_n7;
		else if(strNumber.equals("8"))
			return R.drawable.modern_red_n8;
		else if(strNumber.equals("9"))
			return R.drawable.modern_red_n9;
		else if(strNumber.equals(":"))
			return R.drawable.modern_red_split;
		else
			return R.drawable.modern_red_bg;
	}
	
	/*获取摩登黄皮肤*/
	public int GetSkinModernYellow(String strNumber)
	{
		if(strNumber.equals("0"))
			return R.drawable.modern_yellow_n0;
		else if(strNumber.equals("1"))
			return R.drawable.modern_yellow_n1;
		else if(strNumber.equals("2"))
			return R.drawable.modern_yellow_n2;
		else if(strNumber.equals("3"))
			return R.drawable.modern_yellow_n3;
		else if(strNumber.equals("4"))
			return R.drawable.modern_yellow_n4;
		else if(strNumber.equals("5"))
			return R.drawable.modern_yellow_n5;
		else if(strNumber.equals("6"))
			return R.drawable.modern_yellow_n6;
		else if(strNumber.equals("7"))
			return R.drawable.modern_yellow_n7;
		else if(strNumber.equals("8"))
			return R.drawable.modern_yellow_n8;
		else if(strNumber.equals("9"))
			return R.drawable.modern_yellow_n9;
		else if(strNumber.equals(":"))
			return R.drawable.modern_yellow_split;
		else
			return R.drawable.modern_yellow_bg;
	}
	
	/*获取半透蓝皮肤*/
	public int GetSkinHalfBlue(String strNumber)
	{
		if(strNumber.equals("0"))
			return R.drawable.half_blue_n0;
		else if(strNumber.equals("1"))
			return R.drawable.half_blue_n1;
		else if(strNumber.equals("2"))
			return R.drawable.half_blue_n2;
		else if(strNumber.equals("3"))
			return R.drawable.half_blue_n3;
		else if(strNumber.equals("4"))
			return R.drawable.half_blue_n4;
		else if(strNumber.equals("5"))
			return R.drawable.half_blue_n5;
		else if(strNumber.equals("6"))
			return R.drawable.half_blue_n6;
		else if(strNumber.equals("7"))
			return R.drawable.half_blue_n7;
		else if(strNumber.equals("8"))
			return R.drawable.half_blue_n8;
		else if(strNumber.equals("9"))
			return R.drawable.half_blue_n9;
		else if(strNumber.equals(":"))
			return R.drawable.half_blue_split;
		else
			return R.drawable.half_blue_bg;
	}
	
	/*将英文的星期名称翻译成中文*/
	public String GetChineseWeek(String strWeek)
	{
		String strRet="";  //返回的中文
		
		if(strWeek.equals("Sunday") || strWeek.equals("1"))
			strRet="星期日";
		else if(strWeek.equals("Monday") || strWeek.equals("2"))
			strRet="星期一";
		else if(strWeek.equals("Tuesday") || strWeek.equals("3"))
			strRet="星期二";
		else if(strWeek.equals("Wednesday") || strWeek.equals("4"))
			strRet="星期三";
		else if(strWeek.equals("Thursday") || strWeek.equals("5"))
			strRet="星期四";
		else if(strWeek.equals("Friday") || strWeek.equals("6"))
			strRet="星期五";
		else if(strWeek.equals("Saturday") || strWeek.equals("7"))
			strRet="星期六";
		else  //超出英文和数字范围，返回系统给定值
			strRet=strWeek;
		
		return strRet;
	}
	
	/*时间刷新线程*/
	Thread trdRefresh=new Thread()
	{
		public void run()
		{
			while(true)
			{
				try
				{
					Thread.sleep(RefreshRate);
					ctxGlobal.sendBroadcast(new Intent(strPackageName));  //通知刷新Widget的Intent
				}
				catch(Exception e)
				{
					System.exit(0);
				}
			}
		};
	};
}