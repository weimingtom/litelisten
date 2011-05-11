/*
 * Copyright (C) 2011 The LiteListen Project
 * 
 * Licensed under the Mozilla Public Licence, version 1.1 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * �������ֲ�������Ŀ ��Ȩ���� 2011
 * ���� Mozilla Public Licence 1.1 �������³ơ�Э�顱����
 * ���ǳ�ŵ���ظ�Э�飬��������ʹ�ñ��ļ���
 * �����Դ�������վ��ø�Э��ĸ�����
 *
 *      http://www.mozilla.org/MPL/MPL-1.1.html
 */

package com.galapk.litelisten;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBProvider extends SQLiteOpenHelper
{
	private static String DBName = "db_main"; // ���ݿ���
	private static int DBVersion = 2; // ���ݿ�汾

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

	/* ��ȡ��ǰ��������ݿ�ʵ�� */
	public SQLiteDatabase GetInstance(boolean IsReadOnly)
	{
		SQLiteDatabase db; // ���ݿ�

		if (IsReadOnly) // ֻ��
			db = getReadableDatabase();
		else
			db = getWritableDatabase();

		return db;
	}

	/* ������ݱ� */
	public boolean DBClear(String strTable)
	{
		String strCreateMusicInfo = "create table music_info(title text, artist text, album text, year text, genre text, track text, comment text, title_py text, title_simple_py text, artist_py, artist_simple_py, music_path text, lrc_path text, song_info text, play_times number, is_last_played number, id3_checked text);";

		if (!DropTable(strTable))
		{// ���ɾ����ʧ��
			if (CreateTable(strCreateMusicInfo))
				return true; // �����ɹ�
			else
				return false; // ����ʧ��
		}
		else
		{// ɾ����ɹ�
			if (CreateTable(strCreateMusicInfo)) // �����±�
				return true;
			else
				return false;
		}
	}

	/* �������ݱ� */
	public boolean CreateTable(String strSQLTable)
	{
		SQLiteDatabase dbContact = GetInstance(false); // ���ݿ�ʵ��
		try
		{
			dbContact.execSQL(strSQLTable);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// ���������ݱ�����
			Log.e("DBCreate", e.getMessage() + "|" + strSQLTable);
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* ɾ�����ݱ� */
	public boolean DropTable(String strTable)
	{
		SQLiteDatabase dbContact = GetInstance(false); // ���ݿ�ʵ��
		try
		{
			String strSQLDrop = "drop table " + strTable + ";";
			dbContact.execSQL(strSQLDrop);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// ���������ݱ�����
			Log.e("DBDrop", e.getMessage() + "|" + strTable);
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* �������� */
	public boolean InsertData(String strTable, String strData)
	{
		SQLiteDatabase dbContact = GetInstance(false); // ���ݿ�ʵ��
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

	/* �޸����� */
	public boolean ModifiyData(String strTable, String strSubStringOfSet)
	{
		SQLiteDatabase dbContact = GetInstance(false); // ���ݿ�ʵ��
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

	/* ɾ������ */
	public boolean DeleteData(String strTable, String strCondition)
	{
		SQLiteDatabase dbContact = GetInstance(false); // ���ݿ�ʵ��
		try
		{
			String strSQLDelete = "delete from " + strTable;
			if (!strCondition.equals("") && !strCondition.equals(null)) // �����������ƴ��
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