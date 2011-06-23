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

public class MusicTagEntity
{
	private String Album = ""; // ר������
	private String Title = ""; // ��������
	private String Gener = ""; // ����
	private String Artist = ""; // ������
	private String Year = ""; // ���
	private String Track = ""; // �����
	private String Lyric = ""; // ���
	private String Comment = ""; // ��ע

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