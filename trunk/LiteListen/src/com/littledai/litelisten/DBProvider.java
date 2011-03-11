package com.littledai.litelisten;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBProvider extends SQLiteOpenHelper
{
	private static String DBName = "db_main"; // ���ݿ���
	private static int DBVersion = 1; // ���ݿ�汾

	private srcMain main = null;

	public DBProvider(srcMain main)
	{
		super(main.getBaseContext(), DBName, null, DBVersion);
		this.main = main;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String strCreateMusicInfo = "create table music_info(title text, artist text, album text, year text, genre text, track text, comment text, title_py text, title_simple_py text, artist_py, artist_simple_py, music_path text, lrc_path text, song_info text, play_times number, is_last_played number);";
		db.execSQL(strCreateMusicInfo);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		;
	}

	/* ��ȡ��ǰ��������ݿ�ʵ�� */
	public SQLiteDatabase GetDBInstance(boolean IsReadOnly)
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
		String strCreateMusicInfo = "create table music_info(title text, artist text, album text, year text, genre text, track text, comment text, title_py text, title_simple_py text, artist_py, artist_simple_py, music_path text, lrc_path text, song_info text, play_times number, is_last_played number);";

		if (!DBDrop(strTable))
		{// ���ɾ����ʧ��
			try
			{// ���Դ����±�
				if (DBCreate(strCreateMusicInfo))
					return true; // �����ɹ�
				else
					return false; // ����ʧ��
			}
			catch (Exception e)
			{// ����ʧ��
				e.printStackTrace();
				return false;
			}
		}
		else
		{// ɾ����ɹ�
			if (DBCreate(strCreateMusicInfo)) // �����±�
				return true;
			else
				return false;
		}
	}

	/* �������ݱ� */
	public boolean DBCreate(String strSQLTable)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // ���ݿ�ʵ��
		try
		{
			dbContact.execSQL(strSQLTable);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// ���������ݱ�����
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* ɾ�����ݱ� */
	public boolean DBDrop(String strTable)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // ���ݿ�ʵ��
		try
		{
			String strSQLDrop = "drop table " + strTable + ";";
			dbContact.execSQL(strSQLDrop);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// ���������ݱ�����
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* �������� */
	public boolean DBInsert(String strTable, String strData)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // ���ݿ�ʵ��
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
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* �޸����� */
	public boolean DBModifiy(String strTable, String strSubStringOfSet)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // ���ݿ�ʵ��
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
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* ɾ������ */
	public boolean DBDelete(String strTable, String strCondition)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // ���ݿ�ʵ��
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