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
	int RecCurr=0;  //��ǰ������ļ�¼���
	int RecCount=0;  //��Ҫ����ļ�¼����
	
	/*����ؼ�*/
	TextView labDetail;
	ProgressBar prgBar;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indexing_dialog);

		/*����ؼ�ʵ��*/
		labDetail=(TextView)findViewById(R.id.labDetail);
		prgBar=(ProgressBar)findViewById(R.id.prgBar);
        
        //ѯ���û��Ƿ���Ҫ��������
        new AlertDialog.Builder(IndexingDialog.this).setMessage("ȷ��������ϵ��������")
        .setTitle("��������")
        .setPositiveButton("ȷ��",new DialogInterface.OnClickListener()
        {
        	public void onClick(DialogInterface dialog,int whichButton)
        	{
        		new Indexing().start();  //ִ���߳�
        	}
        })
        .setNegativeButton("ȡ��",new DialogInterface.OnClickListener()
        {
        	public void onClick(DialogInterface dialog,int whichButton)
        	{
        		finish();
        	}
        })
        .setOnKeyListener(CtrlFunc_txtKeywordKeyPress)  //�Ի��򰴼�ί��
        .setIcon(R.drawable.index_contact)
        .show();
	}
	
	/*��ȡ��ǰ��������ݿ�ʵ��*/
	public SQLiteDatabase GetDBInstance(boolean IsReadOnly)
	{
		SQLiteDatabase db;  //���ݿ�
		
		if(IsReadOnly)  //ֻ��
			db=(new DBProvider(getBaseContext())).getReadableDatabase();
		else
			db=(new DBProvider(getBaseContext())).getWritableDatabase();
			
		return db;
	}
	
	/*������ݱ�*/
	public boolean DBClear(String strTable)
	{
		//������䣬�ֶΣ�������ȫƴ����ƴ���绰���루��,���ָ�������ϵ����
		String strSQLTable="create table contacts(name_full text,name_py_full text,name_py_simple text,"+
				"contact_number text,using_times number);";
		
		if(!DBDrop(strTable))
		{//���ɾ����ʧ��
			try
			{//���Դ����±�
				if(DBCreate(strSQLTable))
					return true;  //�����ɹ�
				else
					return false;  //����ʧ��
			}
			catch(Exception e)
			{//����ʧ��
				Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else
		{//ɾ����ɹ�
			if(DBCreate(strSQLTable))  //�����±�
				return true;
			else
				return false;
		}
	}
	
	/*�������ݱ�*/
	public boolean DBCreate(String strSQLTable)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //���ݿ�ʵ��
		try
		{
			dbContact.execSQL(strSQLTable);
			dbContact.close();
			return true;
		}
		catch(Exception e)
		{//���������ݱ�����
			dbContact.close();
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*ɾ�����ݱ�*/
	public boolean DBDrop(String strTable)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //���ݿ�ʵ��
		try
		{
			String strSQLDrop="drop table "+strTable+";";
			dbContact.execSQL(strSQLDrop);
			dbContact.close();
			return true;
		}
		catch(Exception e)
		{//���������ݱ�����
			dbContact.close();
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/*��������*/
	public boolean DBInsert(String strTable,String strData)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //���ݿ�ʵ��		
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
	
	/*�޸�����*/
	public boolean DBModifiy(String strTable,String strSubStringOfSet)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //���ݿ�ʵ��
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
	
	/*ɾ������*/
	public boolean DBDelete(String strTable,String strCondition)
	{
		SQLiteDatabase dbContact=GetDBInstance(false);  //���ݿ�ʵ��
		try
		{
			String strSQLDelete="delete from "+strTable;
			if(!strCondition.equals("") && !strCondition.equals(null))  //�����������ƴ��
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
	
	/*������ϵ������*/
	private void MakeContactsIndex()
	{
		Cursor curContact=getContentResolver().query(Phone.CONTENT_URI,null,null,null,null);  //��ϵͳ��ϵ�˱��в��������ϵ��
		String strIDAdded="";  //����ӵ���ϵ��ID
		DBClear("contacts");  //�����ԭ���ݿ�
		RecCount=curContact.getCount();  //��ȡ�ܼ�¼��
		hdl.sendEmptyMessage(0);  //֪ͨHandler���½���
		
		while(curContact.moveToNext())
		{
			String strName=curContact.getString(curContact.getColumnIndex(Phone.DISPLAY_NAME));
            String strID=curContact.getString(curContact.getColumnIndex(Phone.CONTACT_ID));
			String strPhoneNumber="";
			RecCurr=curContact.getPosition()+1;  //��ȡ��ǰ��¼���

			if(strIDAdded.indexOf(strID)<0)
			{//����Ѿ����������
				//ȡ�õ绰���루���ܴ��ڶ�����룩
				Cursor curPhoneNum=getContentResolver().query(Phone.CONTENT_URI,null,Phone.CONTACT_ID+"="+strID,null,null);
				while(curPhoneNum.moveToNext())
				{
					strPhoneNumber+=curPhoneNum.getString(curPhoneNum.getColumnIndex(Phone.NUMBER)).trim()+",";  //�á�,������
				}
				strPhoneNumber=strPhoneNumber.substring(0,strPhoneNumber.length()-1);  //ɾ�����һ����,��
				curPhoneNum.close();

				//��ȡ�õ����ݴ���������ݿ�
				String strPYFull=PYProvider.GetPYFull(strName);  //��ȡȫƴ����
				String strPYSimple=PYProvider.GetPYSimple(strPYFull);  //��ȡ��ƴ����
				String strSQLInsert="'"+strName+"','"+strPYFull+"','"+strPYSimple+"','"+strPhoneNumber+"','0'";  //������Ҫ���������
				DBInsert("contacts",strSQLInsert);  //ִ�в���

				hdl.sendEmptyMessage(0);  //֪ͨHandler���½���
				strIDAdded+=strID+",";  //����ӹ���ID�ŷ���
			}
		}
    	curContact.close();
	}

	/*ˢ�½�����ʾ��Handler*/
	Handler hdl=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
    		prgBar.setMax(RecCount);  //���ý��������ֵ
			labDetail.setText("�Ѵ���"+RecCurr+"/"+RecCount);  //�����ı���ʾ��Ϣ
			prgBar.setProgress(RecCurr);  //���ý���������
			if(RecCurr>=RecCount)  //�����������
				Toast.makeText(IndexingDialog.this,"��ϵ������������ɡ�",Toast.LENGTH_LONG).show();
        }
    };

	/*�����߳�*/
	public class Indexing extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				MakeContactsIndex();  //�Ƚ�������
				sleep(5000);  //����������ɺ�ȴ�5�룬ʹToast��ʾ��Ϣ�ܹ�������ʾ
				System.exit(0);  //5����˳�ϵͳ
			}
			catch(Exception e)
			{
				System.exit(0);  //�쳣ʱ�˳������޷�ʹ��Toast��ʾ������Ϣ
			}
		}
	}
	
	/*����ί��*/
	private OnKeyListener CtrlFunc_txtKeywordKeyPress=new OnKeyListener()
	{
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
		{
	    	if(keyCode==KeyEvent.KEYCODE_BACK)
	    		finish();  //�����ؼ��رմ���

        	return false;  //Ϊtrueʱ����Ӧ�������
		}
	};
}