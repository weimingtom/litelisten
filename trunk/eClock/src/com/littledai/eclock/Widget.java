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
	
	int RefreshRate=500;  //С����ˢ����
	
	/*����С����*/
	@Override
	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds)
	{
		final int WidgetCount=appWidgetIds.length;
		for(int i=0;i<WidgetCount;i++)
		{
			RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.widget);
			Intent itt=new Intent(context,Settings.class);  //��ѡ������
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
	
	/*���յ�����Ϣ����С�����ؼ��¼�*/
	@Override
	public void onReceive(Context context,Intent intent)
	{
		if(intent.getAction().equals(strPackageName) || intent.getAction().equals(Intent.ACTION_TIME_TICK))
		{
			RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.widget);
			SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //��ȡ�����ļ�
	
			boolean blTimeFormat=sp.getBoolean("chkTimeFormat",true);  //ʱ���ʽ
        	String strDateFormat=sp.getString("lstDateFormat","0");  //���ڸ�ʽ
        	String strDateSplitter=sp.getString("txtDateSplitter","");  //���ڷָ���
        	boolean blReduceResUse=sp.getBoolean("chkReduceResUse",true);  //������Դռ��
        	String strSkin=sp.getString("lstSkin","0");  //Ƥ��
        	boolean blShowBG=sp.getBoolean("chkShowBG",true);  //��ʾС�����ı���

        	DateFormat df;  //ʱ���ʽ
        	if(blTimeFormat)  //24Сʱ��
        		df=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss EEEE");  //HH��ʾ24Сʱ�ƣ�hhΪ12Сʱ��
        	else  //12Сʱ��
        		df=new SimpleDateFormat("yyyy.MM.dd hh:mm:ss EEEE");
        	String strTime=df.format(new Date());  //��ȡ��ǰʱ���ַ���
        	String strYear=strTime.substring(0,4);  //��
        	String strMonth=strTime.substring(5,7);  //��
        	String strDay=strTime.substring(8,10);  //��
        	String strHour1=strTime.substring(11,12);  //ʱ��ʮλ��
        	String strHour2=strTime.substring(12,13);  //ʱ����λ��
        	String strMinite1=strTime.substring(14,15);  //�֣�ʮλ��
        	String strMinite2=strTime.substring(15,16);  //�֣���λ��
        	String strWeek=GetChineseWeek(strTime.substring(20,strTime.length()));  //����
        	
        	String strDateString="";  //�����ַ���
        	if(strDateFormat.equals("0"))  //�ı��������ڸ�ʽ
        		strDateString=strYear+"��"+Integer.parseInt(strMonth)+"��"+Integer.parseInt(strDay)+"�� "+strWeek;  //������
        	else if(strDateFormat.equals("1"))  //˳��򵥱��
        		strDateString=strYear+strDateSplitter+strMonth+strDateSplitter+strDay+" "+strWeek;  //��-�ָ�-��-�ָ�-��
        	else  //����򵥱��
        		strDateString=strMonth+strDateSplitter+strDay+strDateSplitter+strYear+" "+strWeek;  //��-�ָ�-��-�ָ�-��
        	
        	if(blReduceResUse)  //������Դռ��ʱÿ��ˢ��
        		RefreshRate=1000;
        	else  //����0.5��ˢ��һ��
        		RefreshRate=500;
        	
        	String strColor=sp.getString("strDateColor","#0099FF");  //��ȡĬ����ɫ
        	rv.setTextColor(R.id.labDate,Color.parseColor(strColor));  //����������ɫ
        	rv.setTextViewText(R.id.labDate,strDateString);  //������ʾʱ��

        	//����Ƥ��
        	if(strSkin.equals("0"))
        	{//Ħ����
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
        	{//Ħ�Ǻ�
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
        	{//Ħ�Ǻ�
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
        	{//��͸��
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
		else if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED))  //ɾ��С����
			System.exit(0);  //�˳�����
		
		super.onReceive(context,intent);
	}
	
	/*��ȡĦ����Ƥ��*/
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
	
	/*��ȡĦ�Ǻ�Ƥ��*/
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
	
	/*��ȡĦ�ǻ�Ƥ��*/
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
	
	/*��ȡ��͸��Ƥ��*/
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
	
	/*��Ӣ�ĵ��������Ʒ��������*/
	public String GetChineseWeek(String strWeek)
	{
		String strRet="";  //���ص�����
		
		if(strWeek.equals("Sunday") || strWeek.equals("1"))
			strRet="������";
		else if(strWeek.equals("Monday") || strWeek.equals("2"))
			strRet="����һ";
		else if(strWeek.equals("Tuesday") || strWeek.equals("3"))
			strRet="���ڶ�";
		else if(strWeek.equals("Wednesday") || strWeek.equals("4"))
			strRet="������";
		else if(strWeek.equals("Thursday") || strWeek.equals("5"))
			strRet="������";
		else if(strWeek.equals("Friday") || strWeek.equals("6"))
			strRet="������";
		else if(strWeek.equals("Saturday") || strWeek.equals("7"))
			strRet="������";
		else  //����Ӣ�ĺ����ַ�Χ������ϵͳ����ֵ
			strRet=strWeek;
		
		return strRet;
	}
	
	/*ʱ��ˢ���߳�*/
	Thread trdRefresh=new Thread()
	{
		public void run()
		{
			while(true)
			{
				try
				{
					Thread.sleep(RefreshRate);
					ctxGlobal.sendBroadcast(new Intent(strPackageName));  //֪ͨˢ��Widget��Intent
				}
				catch(Exception e)
				{
					System.exit(0);
				}
			}
		};
	};
}