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

public class MusicTagEntity
{
	private String Album = ""; // 专辑名称
	private String Title = ""; // 歌曲标题
	private String Gener = ""; // 流派
	private String Artist = ""; // 艺术家
	private String Year = ""; // 年份
	private String Track = ""; // 音轨号
	private String Lyric = ""; // 歌词
	private String Comment = ""; // 备注

	public String getAlbum()
	{
		return Album;
	}

	public void setAlbum(String album)
	{
		Album = album;
	}

	public String getTitle()
	{
		return Title;
	}

	public void setTitle(String title)
	{
		Title = title;
	}

	public String getGener()
	{
		return Gener;
	}

	public void setGener(String gener)
	{
		Gener = gener;
	}

	public String getArtist()
	{
		return Artist;
	}

	public void setArtist(String artist)
	{
		Artist = artist;
	}

	public String getYear()
	{
		return Year;
	}

	public void setYear(String year)
	{
		Year = year;
	}

	public String getTrack()
	{
		return Track;
	}

	public void setTrack(String track)
	{
		Track = track;
	}

	public String getLyric()
	{
		return Lyric;
	}

	public void setLyric(String lyric)
	{
		Lyric = lyric;
	}

	public String getComment()
	{
		return Comment;
	}

	public void setComment(String comment)
	{
		Comment = comment;
	}
}