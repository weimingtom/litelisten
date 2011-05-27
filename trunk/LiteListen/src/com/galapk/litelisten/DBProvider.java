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

public class DBProvider extends SQLiteOpenHelper
{
	private static String DBName = "db_main"; // 数据库名
	private static int DBVersion = 2; // 数据库版本

	public DBProvider(scrMain main)
	{
		super(main.getBaseContext(), DBName, null, DBVersion);
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
}