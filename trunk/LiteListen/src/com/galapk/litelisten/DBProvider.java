/*
 * Copyright (C) 2011 The LiteListen Project
 * 
 * Licensed under the Mozilla Public Licence, version 1.1 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * 轻听音乐播放器项目 版权所有 2011
 * 基于 Mozilla Public Licence 1.1 发布（下称“协议”）。
 * 若非承诺遵守该协议，您将不能使用本文件。
 * 您可以从下述网站获得该协议的副本：
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 */

package com.galapk.litelisten;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBProvider extends SQLiteOpenHelper
{
	private static String DBName = "db_main"; // 数据库名
	private static int DBVersion = 2; // 数据库版本

	private srcMain main = null;

	public DBProvider(srcMain main)
	{
		super(main.getBaseContext(), DBName, null, DBVersion);
		this.main = main;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String strCreateMusicInfo = "create table music_info(title text, artist text, album text, year text, genre text, track text, comment text, title_py text, title_simple_py text, artist_py text, artist_simple_py text, music_path text primary key, lrc_path text, song_info text, play_times number, is_last_played number, id3_checked text, verify_code text);";
		db.execSQL(strCreateMusicInfo);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if (oldVersion == 1)
		{
			String strModifyMusicInfo = "alter table music_info add verify_code text; alter table music_info add primary key (music_path);";
			db.execSQL(strModifyMusicInfo);
		}
	}

	/* 获取当前程序的数据库实例 */
	public SQLiteDatabase GetInstance(boolean IsReadOnly)
	{
		SQLiteDatabase db; // 数据库

		if (IsReadOnly) // 只读
			db = getReadableDatabase();
		else
			db = getWritableDatabase();

		return db;
	}

	/* 清空数据表 */
	public boolean DBClear(String strTable)
	{
		String strCreateMusicInfo = "create table music_info(title text, artist text, album text, year text, genre text, track text, comment text, title_py text, title_simple_py text, artist_py, artist_simple_py, music_path text, lrc_path text, song_info text, play_times number, is_last_played number, id3_checked text);";

		if (!DropTable(strTable))
		{// 如果删除表失败
			if (CreateTable(strCreateMusicInfo))
				return true; // 创建成功
			else
				return false; // 创建失败
		}
		else
		{// 删除表成功
			if (CreateTable(strCreateMusicInfo)) // 创建新表
				return true;
			else
				return false;
		}
	}

	/* 创建数据表 */
	public boolean CreateTable(String strSQLTable)
	{
		SQLiteDatabase dbContact = GetInstance(false); // 数据库实例
		try
		{
			dbContact.execSQL(strSQLTable);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// 可能是数据表不存在
			Log.e("DBCreate", e.getMessage() + "|" + strSQLTable);
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* 删除数据表 */
	public boolean DropTable(String strTable)
	{
		SQLiteDatabase dbContact = GetInstance(false); // 数据库实例
		try
		{
			String strSQLDrop = "drop table " + strTable + ";";
			dbContact.execSQL(strSQLDrop);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// 可能是数据表不存在
			Log.e("DBDrop", e.getMessage() + "|" + strTable);
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* 插入数据 */
	public boolean InsertData(String strTable, String strData)
	{
		SQLiteDatabase dbContact = GetInstance(false); // 数据库实例
		try
		{
			if (strData.equals("") || strData.equals(null))
				return false;

			String strSQLInsert = "insert into " + strTable + " values(" + strData + ");";
			dbContact.execSQL(strSQLInsert);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{
			Log.e("DBInsert", e.getMessage() + "|" + strTable + "|" + strData);
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* 修改数据 */
	public boolean ModifiyData(String strTable, String strSubStringOfSet)
	{
		SQLiteDatabase dbContact = GetInstance(false); // 数据库实例
		try
		{
			if (strSubStringOfSet.equals("") || strSubStringOfSet.equals(null))
				return false;

			String strSQLDelete = "update " + strTable + " " + strSubStringOfSet + ";";
			dbContact.execSQL(strSQLDelete);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{
			Log.e("DBModifiy", e.getMessage() + "|" + strTable + "|" + strSubStringOfSet);
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* 删除数据 */
	public boolean DeleteData(String strTable, String strCondition)
	{
		SQLiteDatabase dbContact = GetInstance(false); // 数据库实例
		try
		{
			String strSQLDelete = "delete from " + strTable;
			if (!strCondition.equals("") && !strCondition.equals(null)) // 如果有条件则拼接
				strSQLDelete += " where " + strCondition + ";";
			dbContact.execSQL(strSQLDelete);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{
			Log.e("DBDelete", e.getMessage() + "|" + strTable + "|" + strCondition);
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}
}