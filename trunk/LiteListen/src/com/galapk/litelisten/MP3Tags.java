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

import java.io.File;

import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;

import android.util.Log;

public class MP3Tags
{
	private srcMain main = null;

	public MP3Tags(srcMain main)
	{
		this.main = main;
	}

	public ID3Tag ReadID3(String path)
	{
		try
		{
			File f = new File(path);
			MediaFile mf = new MP3File(f);
			ID3Tag[] ID3 = mf.getTags();

			String index = main.getSp().getString("lstPropertyReadPriority", "0");
			if (index.equals("0"))
			{
				// ���ȼ��ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);

				// Ȼ����ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// �����ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);
			}
			else if (index.equals("1"))
			{
				// ���ȼ��ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// Ȼ����ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);

				// �����ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);
			}
			else if (index.equals("0"))
			{
				// ���ȼ��ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);

				// Ȼ����ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// �����ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);
			}
			else if (index.equals("0"))
			{
				// ���ȼ��ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// Ȼ����ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);

				// �����ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);
			}

			return null;
		}
		catch (Exception e)
		{
			Log.e("ReadID3", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/* ���±���ID3��ǩΪGBK */
	public ID3Tag ReCodecTag(ID3Tag ID3)
	{
		if (ID3 instanceof ID3V2_3_0Tag)
		{
			ID3V2_3_0Tag ID3v2 = (ID3V2_3_0Tag) ID3;
			int Step = 0; // ��¼get����һ���ֶγ�������

			try
			{
				Step++;
				if (ID3v2.getArtist() != null && !ID3v2.getArtist().equals("null"))
					ID3v2.setArtist(new String(ID3v2.getArtist().getBytes("ISO-8859-1"), "GBK"));
				else
					ID3v2.setArtist("");

				Step++;
				if (ID3v2.getGenre() != null && !ID3v2.getGenre().equals("null"))
					ID3v2.setGenre(new String(ID3v2.getGenre().getBytes("ISO-8859-1"), "GBK"));
				else
					ID3v2.setGenre("");

				Step++;
				if (ID3v2.getTitle() != null && !ID3v2.getTitle().equals("null"))
					ID3v2.setTitle(new String(ID3v2.getTitle().getBytes("ISO-8859-1"), "GBK"));
				else
					ID3v2.setTitle("");

				Step++;
				if (ID3v2.getAlbum() != null && !ID3v2.getAlbum().equals("null"))
					ID3v2.setAlbum(new String(ID3v2.getAlbum().getBytes("ISO-8859-1"), "GBK"));
				else
					ID3v2.setAlbum("");

				Step++;
				if (ID3v2.getComment() != null && !ID3v2.getComment().equals("null"))
					ID3v2.setComment(new String(ID3v2.getComment().getBytes("ISO-8859-1"), "GBK"));
				else
					ID3v2.setComment("");
			}
			catch (Exception e)
			{
				Log.e("ReCodecTag", e.getMessage());
				e.printStackTrace();

				try
				{
					if (Step == 4)
						ID3v2.setAlbum("");
					else if (Step == 5)
						ID3v2.setComment("");
				}
				catch (Exception e1)
				{
					Log.e("ReCodecTag1", e.getMessage());
					e1.printStackTrace();
				}
			}

			return ID3v2;
		}
		else if (ID3 instanceof ID3V1_1Tag)
			return (ID3V1_1Tag) ID3;
		else if (ID3 instanceof ID3V1_0Tag)
			return (ID3V1_0Tag) ID3;

		return null;
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