package com.littledai.top5friends;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Widget extends AppWidgetProvider
{
	/*���͸����������Ϣ*/
	final String strContactSelected="MSG_Contact";
	final String strFuncsMenu="MSG_Menu";
	final String strClearSelection="MSG_Clear";
	final String strCall="MSG_Call";
	final String strSMS="MSG_SMS";
	
	//�ؼ�ID����
	final int[] btnContacts = {R.id.btnContact1, R.id.btnContact2, R.id.btnContact3, R.id.btnContact4, R.id.btnContact5};
	final int[] labContacts = {R.id.labContact1, R.id.labContact2, R.id.labContact3, R.id.labContact4, R.id.labContact5};
	final int[] frmFuncs = {R.id.frmFuncsBG1, R.id.frmFuncsBG2, R.id.frmFuncsBG3, R.id.frmFuncsBG4, R.id.frmFuncsBG5};
	final int[] btnCalls = {R.id.btnCall1, R.id.btnCall2, R.id.btnCall3, R.id.btnCall4, R.id.btnCall5};
	final int[] btnSMSs = {R.id.btnSMS1, R.id.btnSMS2, R.id.btnSMS3, R.id.btnSMS4, R.id.btnSMS5};
	
	/*����С����*/
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		final int WidgetCount = appWidgetIds.length;
		for(int i = 0; i < WidgetCount; i++)
		{
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
			Intent intent = new Intent(context, Widget.class);
			PendingIntent pdItent;
			
			intent = null;  //��������½���������ܳ���
			intent = new Intent(context, IndexingDialog.class);  //����������
			pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.btnIndexing, pdItent);
	
			intent = null;  //��������½���������ܳ���
			intent = new Intent(context, srcMain.class);  //����������
			pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.btnOpenActivity, pdItent);

			intent = null;  //��������½���������ܳ���
			intent = new Intent(context, Widget.class);
			
			//������ϵ�˰�ť�ͱ�ǩ����
			for(int j = 0; j< btnContacts.length; j++)
			{
				intent.setAction(strContactSelected + Integer.toString(j + 1));  //������ϵ��
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(btnContacts[j], pdItent);

				intent.setAction(strClearSelection);  //���ѡ��
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(labContacts[j], pdItent);
				
				intent.setAction(strCall + Integer.toString(j + 1));  //���Ű�ť����
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(btnCalls[j], pdItent);
				
				intent.setAction(strSMS + Integer.toString(j + 1));  //���Ͱ�ť����
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(btnSMSs[j], pdItent);
			}

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
	}
	
	/*���յ�����Ϣ����С�����ؼ��¼�*/
	@Override
	public void onReceive(Context context, Intent intent)
	{
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);  //С������ͼ
		
		//����ǰѡ���������ʾ
		if (intent.getAction().equals(strContactSelected + "1"))
		{
			//��ʾ���׵Ĳ˵�����
			int[] MenuOpts = {View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//���ذ�ɫ��ǩ��ʾ��ť
			int[] LabelOpts = {View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);

			//���ذ�ť��ʾ��ɫ��ǩ
			int[] ButtonOpts = {View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "2"))
		{
			//��ʾ���׵Ĳ˵�����
			int[] MenuOpts = {View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//���ذ�ɫ��ǩ��ʾ��ť
			int[] LabelOpts = {View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//���ذ�ť��ʾ��ɫ��ǩ
			int[] ButtonOpts = {View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "3"))
		{
			//��ʾ���׵Ĳ˵�����
			int[] MenuOpts = {View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//���ذ�ɫ��ǩ��ʾ��ť
			int[] LabelOpts = {View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//���ذ�ť��ʾ��ɫ��ǩ
			int[] ButtonOpts = {View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "4"))
		{
			//��ʾ���׵Ĳ˵�����
			int[] MenuOpts = {View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//���ذ�ɫ��ǩ��ʾ��ť
			int[] LabelOpts = {View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//���ذ�ť��ʾ��ɫ��ǩ
			int[] ButtonOpts = {View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "5"))
		{
			//��ʾ���׵Ĳ˵�����
			int[] MenuOpts = {View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//���ذ�ɫ��ǩ��ʾ��ť
			int[] LabelOpts = {View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//���ذ�ť��ʾ��ɫ��ǩ
			int[] ButtonOpts = {View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE};  //��ؼ���Ӧ�Ĳ���
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strClearSelection))
		{//���ѡ�����ϵ��
			//��ʾ���׵Ĳ˵�����
			for(int i = 0; i < btnContacts.length; i++)
				rv.setViewVisibility(frmFuncs[i], View.GONE);
			
			//��ʾ��ť
			for(int i = 0; i < btnContacts.length; i++)
				rv.setViewVisibility(btnContacts[i], View.VISIBLE);
			
			//���ذ�ɫ��ǩ
			for(int i = 0; i < btnContacts.length; i++)
				rv.setViewVisibility(labContacts[i], View.GONE);
		}
		else if(intent.getAction().startsWith(strCall))
		{//����绰
			//��ȡ��ǰ��ϵ�������͵绰
			int Order = Integer.parseInt(intent.getAction().substring(intent.getAction().length() - 1));  //���һλ��Ϊ��ϵ�����
			String strName = GetContactNumberBySelectedOrder(context, "name_full", Order - 1);
			String strNumber = GetContactNumberBySelectedOrder(context, "contact_number", Order - 1);
			
			//ִ�в��ų���
	    	Intent itt = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+strNumber));
	    	itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	context.startActivity(itt);
	    	Toast.makeText(context, "���ڲ��ţ�" + strName, Toast.LENGTH_LONG).show();
	    	AddContactUsingTimes(context, strNumber, Order);  //������ϵ����
		}
		else if(intent.getAction().startsWith(strSMS))
		{//���Ͷ���
			//��ȡ��ǰ��ϵ�������͵绰
			int Order = Integer.parseInt(intent.getAction().substring(intent.getAction().length() - 1));  //���һλ��Ϊ��ϵ�����
			String strName = GetContactNumberBySelectedOrder(context, "name_full", Order - 1);
			String strNumber = GetContactNumberBySelectedOrder(context, "contact_number", Order - 1);
			
			//���ö��ų���
	    	Intent itt = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+strNumber));
	    	itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	context.startActivity(itt);
	    	Toast.makeText(context, "�����������ų���Ŀ�꣺" + strName, Toast.LENGTH_LONG).show();
	    	AddContactUsingTimes(context, strNumber, Order);  //������ϵ����				
		}
		else if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED))  //ɾ��С����
			System.exit(0);  //�˳�����

		//��ʾ���������ϵ��
		for(int i = 0; i < btnContacts.length; i++)
		{
			String strNameTemp = GetContactNumberBySelectedOrder(context, "name_full", i);
			String strNumberTemp = GetContactNumberBySelectedOrder(context, "contact_number", i);
			rv.setTextViewText(btnContacts[i], strNameTemp + "<" + strNumberTemp + ">");
			rv.setTextViewText(labContacts[i], strNameTemp + "<" + strNumberTemp + ">");
		}
		
		ComponentName Me=new ComponentName(context,Widget.class);
		AppWidgetManager.getInstance(context).updateAppWidget(Me,rv);
	
		super.onReceive(context, intent);
	}
	
	/*��ȡ��ǰ��������ݿ�ʵ��*/
	public SQLiteDatabase GetDBInstance(Context context,boolean IsReadOnly)
	{
		SQLiteDatabase db;  //���ݿ�
		
		if(IsReadOnly)  //ֻ��
			db=(new DBProvider(context)).getReadableDatabase();
		else
			db=(new DBProvider(context)).getWritableDatabase();
			
		return db;
	}
	
	/*�޸�����*/
	public boolean DBModifiy(Context context,String strTable,String strSubStringOfSet)
	{
		SQLiteDatabase dbContact=GetDBInstance(context,false);  //���ݿ�ʵ��
		try
		{
			if(strSubStringOfSet.equals("") || strSubStringOfSet.equals(null)) return false;
			String strSQLDelete="update "+strTable+" "+strSubStringOfSet+";";
			dbContact.execSQL(strSQLDelete);
			dbContact.close();
			return true;
		}
		catch(Exception e)
		{
			dbContact.close();
			Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*ͨ��ѡ�е���ϵ����Ŵ����ݿ��в�ѯ�����ֶ�*/
	public String GetContactNumberBySelectedOrder(Context context,String strFieldName,int DataPos)  //DataPos��СֵΪ0
	{
		Cursor curContact=GetDBInstance(context,true).query("contacts",null,null,null,null,null,"using_times desc");  //����
		if(curContact.getCount()>DataPos)
		{
			if(curContact.moveToPosition(DataPos))
			{
				String strRet=curContact.getString(curContact.getColumnIndex(strFieldName));
				return strRet;
			}
		}
		
		return null;
	}

	/*������ϵ�˵���ϵ����*/
	public void AddContactUsingTimes(Context context,String strNumber,int DataPos)
	{
		int UsingTimes=Integer.parseInt(GetContactNumberBySelectedOrder(context,"using_times",DataPos-1));
    	String strUsingTimes=Integer.toString(UsingTimes+1);  //��ϵ����+1
    	String strSQLModify="set using_times='"+strUsingTimes+"' where contact_number='"+strNumber+"';";
    	DBModifiy(context,"contacts",strSQLModify);
	}
}