package com.littledai.top5friends;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBProvider extends SQLiteOpenHelper
{
	final static String strDBName="db_contactadv";  //数据库名
	final static int DBVersion=1;  //数据库版本
	
	public DBProvider(Context context)
	{
		super(context,strDBName,null,DBVersion);
	}
	
	/*创建数据库时调用*/
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//建表语句，字段：姓名，全拼，简拼，电话号码（“,”分隔），联系次数
		String strCreateTable="create table contacts(name_full text,name_py_full text,name_py_simple text,contact_number text,using_times number);";
		db.execSQL(strCreateTable);
	}
	
	/*数据库升级时调用*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		;
	}
}