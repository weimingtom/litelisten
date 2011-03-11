package com.littledai.litelisten;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBProvider extends SQLiteOpenHelper
{
	private static String DBName = "db_main"; // 数据库名
	private static int DBVersion = 1; // 数据库版本

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

	/* 获取当前程序的数据库实例 */
	public SQLiteDatabase GetDBInstance(boolean IsReadOnly)
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
		String strCreateMusicInfo = "create table music_info(title text, artist text, album text, year text, genre text, track text, comment text, title_py text, title_simple_py text, artist_py, artist_simple_py, music_path text, lrc_path text, song_info text, play_times number, is_last_played number);";

		if (!DBDrop(strTable))
		{// 如果删除表失败
			try
			{// 尝试创建新表
				if (DBCreate(strCreateMusicInfo))
					return true; // 创建成功
				else
					return false; // 创建失败
			}
			catch (Exception e)
			{// 创建失败
				e.printStackTrace();
				return false;
			}
		}
		else
		{// 删除表成功
			if (DBCreate(strCreateMusicInfo)) // 创建新表
				return true;
			else
				return false;
		}
	}

	/* 创建数据表 */
	public boolean DBCreate(String strSQLTable)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // 数据库实例
		try
		{
			dbContact.execSQL(strSQLTable);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// 可能是数据表不存在
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* 删除数据表 */
	public boolean DBDrop(String strTable)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // 数据库实例
		try
		{
			String strSQLDrop = "drop table " + strTable + ";";
			dbContact.execSQL(strSQLDrop);
			dbContact.close();

			return true;
		}
		catch (Exception e)
		{// 可能是数据表不存在
			dbContact.close();
			e.printStackTrace();

			return false;
		}
	}

	/* 插入数据 */
	public boolean DBInsert(String strTable, String strData)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // 数据库实例
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

	/* 修改数据 */
	public boolean DBModifiy(String strTable, String strSubStringOfSet)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // 数据库实例
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

	/* 删除数据 */
	public boolean DBDelete(String strTable, String strCondition)
	{
		SQLiteDatabase dbContact = GetDBInstance(false); // 数据库实例
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