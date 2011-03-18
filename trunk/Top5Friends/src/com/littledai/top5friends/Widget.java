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
	/*发送给主程序的消息*/
	final String strContactSelected="MSG_Contact";
	final String strFuncsMenu="MSG_Menu";
	final String strClearSelection="MSG_Clear";
	final String strCall="MSG_Call";
	final String strSMS="MSG_SMS";
	
	//控件ID数组
	final int[] btnContacts = {R.id.btnContact1, R.id.btnContact2, R.id.btnContact3, R.id.btnContact4, R.id.btnContact5};
	final int[] labContacts = {R.id.labContact1, R.id.labContact2, R.id.labContact3, R.id.labContact4, R.id.labContact5};
	final int[] frmFuncs = {R.id.frmFuncsBG1, R.id.frmFuncsBG2, R.id.frmFuncsBG3, R.id.frmFuncsBG4, R.id.frmFuncsBG5};
	final int[] btnCalls = {R.id.btnCall1, R.id.btnCall2, R.id.btnCall3, R.id.btnCall4, R.id.btnCall5};
	final int[] btnSMSs = {R.id.btnSMS1, R.id.btnSMS2, R.id.btnSMS3, R.id.btnSMS4, R.id.btnSMS5};
	
	/*更新小部件*/
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		final int WidgetCount = appWidgetIds.length;
		for(int i = 0; i < WidgetCount; i++)
		{
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
			Intent intent = new Intent(context, Widget.class);
			PendingIntent pdItent;
			
			intent = null;  //先清空再新建，否则可能出错
			intent = new Intent(context, IndexingDialog.class);  //弹出索引框
			pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.btnIndexing, pdItent);
	
			intent = null;  //先清空再新建，否则可能出错
			intent = new Intent(context, srcMain.class);  //弹出主界面
			pdItent = PendingIntent.getActivity(context, 0, intent, 0);
			rv.setOnClickPendingIntent(R.id.btnOpenActivity, pdItent);

			intent = null;  //先清空再新建，否则可能出错
			intent = new Intent(context, Widget.class);
			
			//设置联系人按钮和标签动作
			for(int j = 0; j< btnContacts.length; j++)
			{
				intent.setAction(strContactSelected + Integer.toString(j + 1));  //单击联系人
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(btnContacts[j], pdItent);

				intent.setAction(strClearSelection);  //清除选择
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(labContacts[j], pdItent);
				
				intent.setAction(strCall + Integer.toString(j + 1));  //拨号按钮动作
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(btnCalls[j], pdItent);
				
				intent.setAction(strSMS + Integer.toString(j + 1));  //发送按钮动作
				pdItent = PendingIntent.getBroadcast(context, 0, intent, 0);
				rv.setOnClickPendingIntent(btnSMSs[j], pdItent);
			}

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
	}
	
	/*按收到的消息处理小部件控件事件*/
	@Override
	public void onReceive(Context context, Intent intent)
	{
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);  //小部件视图
		
		//将当前选中项高亮显示
		if (intent.getAction().equals(strContactSelected + "1"))
		{
			//显示配套的菜单托盘
			int[] MenuOpts = {View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//隐藏白色标签显示按钮
			int[] LabelOpts = {View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);

			//隐藏按钮显示白色标签
			int[] ButtonOpts = {View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE};  //与控件对应的操作
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "2"))
		{
			//显示配套的菜单托盘
			int[] MenuOpts = {View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//隐藏白色标签显示按钮
			int[] LabelOpts = {View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//隐藏按钮显示白色标签
			int[] ButtonOpts = {View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE};  //与控件对应的操作
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "3"))
		{
			//显示配套的菜单托盘
			int[] MenuOpts = {View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//隐藏白色标签显示按钮
			int[] LabelOpts = {View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//隐藏按钮显示白色标签
			int[] ButtonOpts = {View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE};  //与控件对应的操作
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "4"))
		{
			//显示配套的菜单托盘
			int[] MenuOpts = {View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//隐藏白色标签显示按钮
			int[] LabelOpts = {View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//隐藏按钮显示白色标签
			int[] ButtonOpts = {View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE};  //与控件对应的操作
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strContactSelected + "5"))
		{
			//显示配套的菜单托盘
			int[] MenuOpts = {View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE};  //与控件对应的操作
			for(int i = 0; i < MenuOpts.length; i++)
				rv.setViewVisibility(frmFuncs[i], MenuOpts[i]);

			//隐藏白色标签显示按钮
			int[] LabelOpts = {View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE};  //与控件对应的操作
			for(int i = 0; i < LabelOpts.length; i++)
				rv.setViewVisibility(labContacts[i], LabelOpts[i]);
			
			//隐藏按钮显示白色标签
			int[] ButtonOpts = {View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE};  //与控件对应的操作
			for(int i = 0; i < ButtonOpts.length; i++)
				rv.setViewVisibility(btnContacts[i], ButtonOpts[i]);
		}
		else if(intent.getAction().equals(strClearSelection))
		{//清除选择的联系人
			//显示配套的菜单托盘
			for(int i = 0; i < btnContacts.length; i++)
				rv.setViewVisibility(frmFuncs[i], View.GONE);
			
			//显示按钮
			for(int i = 0; i < btnContacts.length; i++)
				rv.setViewVisibility(btnContacts[i], View.VISIBLE);
			
			//隐藏白色标签
			for(int i = 0; i < btnContacts.length; i++)
				rv.setViewVisibility(labContacts[i], View.GONE);
		}
		else if(intent.getAction().startsWith(strCall))
		{//拨打电话
			//获取当前联系人姓名和电话
			int Order = Integer.parseInt(intent.getAction().substring(intent.getAction().length() - 1));  //最后一位数为联系人序号
			String strName = GetContactNumberBySelectedOrder(context, "name_full", Order - 1);
			String strNumber = GetContactNumberBySelectedOrder(context, "contact_number", Order - 1);
			
			//执行拨号程序
	    	Intent itt = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+strNumber));
	    	itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	context.startActivity(itt);
	    	Toast.makeText(context, "正在拨号：" + strName, Toast.LENGTH_LONG).show();
	    	AddContactUsingTimes(context, strNumber, Order);  //增加联系次数
		}
		else if(intent.getAction().startsWith(strSMS))
		{//发送短信
			//获取当前联系人姓名和电话
			int Order = Integer.parseInt(intent.getAction().substring(intent.getAction().length() - 1));  //最后一位数为联系人序号
			String strName = GetContactNumberBySelectedOrder(context, "name_full", Order - 1);
			String strNumber = GetContactNumberBySelectedOrder(context, "contact_number", Order - 1);
			
			//调用短信程序
	    	Intent itt = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+strNumber));
	    	itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	context.startActivity(itt);
	    	Toast.makeText(context, "正在启动短信程序，目标：" + strName, Toast.LENGTH_LONG).show();
	    	AddContactUsingTimes(context, strNumber, Order);  //增加联系次数				
		}
		else if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED))  //删除小部件
			System.exit(0);  //退出程序

		//显示五个常用联系人
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
	
	/*获取当前程序的数据库实例*/
	public SQLiteDatabase GetDBInstance(Context context,boolean IsReadOnly)
	{
		SQLiteDatabase db;  //数据库
		
		if(IsReadOnly)  //只读
			db=(new DBProvider(context)).getReadableDatabase();
		else
			db=(new DBProvider(context)).getWritableDatabase();
			
		return db;
	}
	
	/*修改数据*/
	public boolean DBModifiy(Context context,String strTable,String strSubStringOfSet)
	{
		SQLiteDatabase dbContact=GetDBInstance(context,false);  //数据库实例
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
	
	/*通过选中的联系人序号从数据库中查询单个字段*/
	public String GetContactNumberBySelectedOrder(Context context,String strFieldName,int DataPos)  //DataPos最小值为0
	{
		Cursor curContact=GetDBInstance(context,true).query("contacts",null,null,null,null,null,"using_times desc");  //倒序
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

	/*增加联系人的联系次数*/
	public void AddContactUsingTimes(Context context,String strNumber,int DataPos)
	{
		int UsingTimes=Integer.parseInt(GetContactNumberBySelectedOrder(context,"using_times",DataPos-1));
    	String strUsingTimes=Integer.toString(UsingTimes+1);  //联系次数+1
    	String strSQLModify="set using_times='"+strUsingTimes+"' where contact_number='"+strNumber+"';";
    	DBModifiy(context,"contacts",strSQLModify);
	}
}