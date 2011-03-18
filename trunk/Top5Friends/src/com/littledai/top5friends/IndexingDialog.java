package com.littledai.top5friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.KeyEvent;
import android.content.DialogInterface.OnKeyListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class IndexingDialog extends Activity
{
	int RecCurr=0;  //当前处理完的记录序号
	int RecCount=0;  //需要处理的记录总数
	
	/*定义控件*/
	TextView labDetail;
	ProgressBar prgBar;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indexing_dialog);

		/*定义控件实体*/
		labDetail=(TextView)findViewById(R.id.labDetail);
		prgBar=(ProgressBar)findViewById(R.id.prgBar);
        
        //询问用户是否需要建立索引
        new AlertDialog.Builder(IndexingDialog.this).setMessage("确定建立联系人索引？")
        .setTitle("建立索引")
        .setPositiveButton("确定",new DialogInterface.OnClickListener()
        {
        	public void onClick(DialogInterface dialog,int whichButton)
        	{
        		new Indexing().start();  //执行线程
        	}
        })
        .setNegativeButton("取消",new DialogInterface.OnClickListener()
        {
        	public void onClick(DialogInterface dialog,int whichButton)
        	{
        		finish();
        	}
        })
        .setOnKeyListener(CtrlFunc_txtKeywordKeyPress)  //对话框按键委托
        .setIcon(R.drawable.index_contact)
        .show();
	}
	
	/*获取当前程序的数据库实例*/
	public SQLiteDatabase GetDBInstance(boolean IsReadOnly)
	{
		SQLiteDatabase db;  //数据库
		
		if(IsReadOnly)  //只读
			db=(new DBProvider(getBaseContext())).getReadableDatabase();
		else
			db=(new DBProvider(getBaseContext())).getWritableDatabase();
			
		return db;
	}
	
	/*清空数据表*/
	public boolean DBClear(String strTable)
	{
		//建表语句，字段：姓名，全拼，简拼，电话号码（“,”分隔），联系次数
		String strSQLTable="create table contacts(name_full text,name_py_full text,name_py_simple text,"+
				"contact_number text,using_times number);";
		
		if(!DBDrop(strTable))
		{//如果删除表失败
			try
			{//尝试创建新表
				if(DBCreate(strSQLTable))
					return true;  //创建成功
				else
					return false;  //创建失败
			}
			catch(Exception e)
			{//创建失败
				Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else
		{//删除表成功
			if(DBCreate(strSQLTable))  //创建新表
				return true;
			else
				return false;
		}
	}
	
	/*创建数据表*/
	public boolean DBCreate(String strSQLTable)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //数据库实例
		try
		{
			dbContact.execSQL(strSQLTable);
			dbContact.close();
			return true;
		}
		catch(Exception e)
		{//可能是数据表不存在
			dbContact.close();
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*删除数据表*/
	public boolean DBDrop(String strTable)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //数据库实例
		try
		{
			String strSQLDrop="drop table "+strTable+";";
			dbContact.execSQL(strSQLDrop);
			dbContact.close();
			return true;
		}
		catch(Exception e)
		{//可能是数据表不存在
			dbContact.close();
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*插入数据*/
	public boolean DBInsert(String strTable,String strData)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //数据库实例		
		try
		{
			if(strData.equals("") || strData.equals(null)) return false;
			String strSQLInsert="insert into "+strTable+" values("+strData+");";
			dbContact.execSQL(strSQLInsert);
			dbContact.close();
			return true;
		}
		catch(Exception e)
		{
			dbContact.close();
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*修改数据*/
	public boolean DBModifiy(String strTable,String strSubStringOfSet)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //数据库实例
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
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*删除数据*/
	public boolean DBDelete(String strTable,String strCondition)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //数据库实例
		try
		{
			String strSQLDelete="delete from "+strTable;
			if(!strCondition.equals("") && !strCondition.equals(null))  //如果有条件则拼接
				strSQLDelete+=" where "+strCondition+";";
			dbContact.execSQL(strSQLDelete);
			dbContact.close();
			return true;
		}
		catch(Exception e)
		{
			dbContact.close();
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*建立联系人索引*/
	private void MakeContactsIndex()
	{
		Cursor curContact=getContentResolver().query(Phone.CONTENT_URI,null,null,null,null);  //从系统联系人表中查出所有联系人
		String strIDAdded="";  //已添加的联系人ID
		DBClear("contacts");  //先清空原数据库
		RecCount=curContact.getCount();  //获取总记录数
		hdl.sendEmptyMessage(0);  //通知Handler更新界面
		
		while(curContact.moveToNext())
		{
			String strName=curContact.getString(curContact.getColumnIndex(Phone.DISPLAY_NAME));
            String strID=curContact.getString(curContact.getColumnIndex(Phone.CONTACT_ID));
			String strPhoneNumber="";
			RecCurr=curContact.getPosition()+1;  //获取当前记录序号

			if(strIDAdded.indexOf(strID)<0)
			{//如果已经添加则跳过
				//取得电话号码（可能存在多个号码）
				Cursor curPhoneNum=getContentResolver().query(Phone.CONTENT_URI,null,Phone.CONTACT_ID+"="+strID,null,null);
				while(curPhoneNum.moveToNext())
				{
					strPhoneNumber+=curPhoneNum.getString(curPhoneNum.getColumnIndex(Phone.NUMBER)).trim()+",";  //用“,”隔开
				}
				strPhoneNumber=strPhoneNumber.substring(0,strPhoneNumber.length()-1);  //删除最后一个“,”
				curPhoneNum.close();

				//将取得的数据存入程序数据库
				String strPYFull=PYProvider.GetPYFull(strName);  //获取全拼代码
				String strPYSimple=PYProvider.GetPYSimple(strPYFull);  //获取简拼代码
				String strSQLInsert="'"+strName+"','"+strPYFull+"','"+strPYSimple+"','"+strPhoneNumber+"','0'";  //定义需要插入的数据
				DBInsert("contacts",strSQLInsert);  //执行插入

				hdl.sendEmptyMessage(0);  //通知Handler更新界面
				strIDAdded+=strID+",";  //将添加过的ID号放入
			}
		}
    	curContact.close();
	}

	/*刷新界面显示的Handler*/
	Handler hdl=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
    		prgBar.setMax(RecCount);  //设置进度条最大值
			labDetail.setText("已处理："+RecCurr+"/"+RecCount);  //设置文本提示信息
			prgBar.setProgress(RecCurr);  //设置进度条计数
			if(RecCurr>=RecCount)  //索引建立完成
				Toast.makeText(IndexingDialog.this,"联系人索引建立完成。",Toast.LENGTH_LONG).show();
        }
    };

	/*索引线程*/
	public class Indexing extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				MakeContactsIndex();  //先建立索引
				sleep(5000);  //索引建立完成后等待5秒，使Toast提示信息能够正常显示
				System.exit(0);  //5秒后退出系统
			}
			catch(Exception e)
			{
				System.exit(0);  //异常时退出程序，无法使用Toast显示出错信息
			}
		}
	}
	
	/*按键委托*/
	private OnKeyListener CtrlFunc_txtKeywordKeyPress=new OnKeyListener()
	{
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
		{
	    	if(keyCode==KeyEvent.KEYCODE_BACK)
	    		finish();  //按返回键关闭窗口

        	return false;  //为true时不响应物理键盘
		}
	};
}