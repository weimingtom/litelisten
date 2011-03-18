package com.littledai.top5friends;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBProvider extends SQLiteOpenHelper
{
	final static String strDBName="db_contactadv";  //���ݿ���
	final static int DBVersion=1;  //���ݿ�汾
	
	public DBProvider(Context context)
	{
		super(context,strDBName,null,DBVersion);
	}
	
	/*�������ݿ�ʱ����*/
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//������䣬�ֶΣ�������ȫƴ����ƴ���绰���루��,���ָ�������ϵ����
		String strCreateTable="create table contacts(name_full text,name_py_full text,name_py_simple text,contact_number text,using_times number);";
		db.execSQL(strCreateTable);
	}
	
	/*���ݿ�����ʱ����*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		;
	}
}