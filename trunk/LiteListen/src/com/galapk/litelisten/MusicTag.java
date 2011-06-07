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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v1.ID3V1_1Tag;
import org.blinkenlights.jid3.v2.ID3V2Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class MusicTag
{
	/* ��ȡASF��WMA��WMV����ǩ */
	public static Map<String, Object> ReadASFTag(String path)
	{
		try
		{
			char[] WMAHead = { 0x30, 0x26, 0xB2, 0x75, 0x8E, 0x66, 0xCF, 0x11, 0xA6, 0xD9, 0x00, 0xAA, 0x00, 0x62, 0xCE, 0x6C }; // WMAͷ����ʶ
			char[] StandardHead = { 0x33, 0x26, 0xB2, 0x75, 0x8E, 0x66, 0xCF, 0x11, 0xA6, 0xD9, 0x00, 0xAA, 0x00, 0x62, 0xCE, 0x6C }; // ��׼TAGͷ����ʶ
			char[] ExtendHead = { 0x40, 0xA4, 0xD0, 0xD2, 0x07, 0xE3, 0xD2, 0x11, 0x97, 0xF0, 0x00, 0xA0, 0xC9, 0x5E, 0xA8, 0x50 }; // ��չTAGͷ����ʶ
			String[] StandardHeadString = { "33", "26", "B2", "75", "8E", "66", "CF", "11", "A6", "D9", "00", "AA", "00", "62", "CE", "6C" }; // ��׼TAGͷ����ʶ
			String[] ExtendHeadString = { "40", "A4", "D0", "D2", "07", "E3", "D2", "11", "97", "F0", "00", "A0", "C9", "5E", "A8", "50" }; // ��չTAGͷ����ʶ
			MusicTagEntity mt = new MusicTagEntity();

			File f = new File(path);
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "ISO-8859-1")); // ͷ����Ҫ��ASCII����

			// ��ȡͷ���ж��Ƿ�ΪWMA�ļ�
			char[] buf = new char[16];
			br.read(buf);

			if (Arrays.equals(buf, WMAHead))
			{// ��WMA
				br.read(buf = new char[14]); // �������õ�8+6�ֽ�
				br.read(buf = new char[16]); // ȷ����ǩ���͵�ͷ

				// ��Ҫ�ж�����������չ�ٱ�׼�����ȱ�׼����չ
				if (Arrays.equals(buf, ExtendHead))
				{// ��չ
					br.read(buf = new char[8]); // �ٴηŹ�8�ֽڣ��˴����õ�8�ֽڱ�־λ��
					br.read(buf = new char[2]); // ��չ��ǩ���ܸ���
					int ExtendCount = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ
					for (int i = 0; i < ExtendCount; i++)
					{
						br.read(buf = new char[2]); // ��չ���Ƴ���
						int FiledNameLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ
						br.read(buf = new char[FiledNameLength]); // ��ȡ��չ����
						String strFieldName = Common.UnicodeCharToString(buf);

						br.read(buf = new char[2]); // Flag����ʱ����

						br.read(buf = new char[2]); // ��չ�ֶγ���
						int FiledLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ
						br.read(buf = new char[FiledLength]); // ��ȡ��չ�ֶ�

						if (strFieldName.equals("WM/TrackNumber"))
							mt.setTrack(Common.UnicodeCharToString(buf));
						else if (strFieldName.equals("WM/Track"))
							mt.setTrack(Common.UnicodeCharToString(buf));
						else if (strFieldName.equals("WM/AlbumArtist"))
							mt.setArtist(Common.UnicodeCharToString(buf));
						else if (strFieldName.equals("WM/AlbumTitle"))
							mt.setAlbum(Common.UnicodeCharToString(buf));
						else if (strFieldName.equals("WM/Year"))
							mt.setYear(Common.UnicodeCharToString(buf));
						else if (strFieldName.equals("WM/Genre"))
							mt.setGener(Common.UnicodeCharToString(buf));
						else if (strFieldName.equals("WM/GenreID"))
							mt.setGener(Common.UnicodeCharToString(buf));
						else if (strFieldName.equals("WM/Lyrics"))
							mt.setLyric(Common.UnicodeCharToString(buf));
					}

					// ��ʼ��ȡ��׼ͷ
					do
					{// �����հ��ַ�
						br.read(buf = new char[1]);
					}
					while ((int) buf[0] == 0);

					boolean IsStandartHeader = true; // �Ƿ������׼ͷ����Ϣ

					if (Integer.toHexString((int) buf[0]).equals(StandardHeadString[0]))
					{
						for (int i = 1; i <= 15; i++)
						{
							br.read(buf = new char[1]);

							String strHex = Integer.toHexString((int) buf[0]).toUpperCase();
							if (strHex.length() == 1)
								strHex = "0" + strHex;

							if (!strHex.equals(StandardHeadString[i]))
							{
								IsStandartHeader = false;
								break;
							}
						}

						if (IsStandartHeader)
						{// �ҵ���׼ͷ
							br.read(buf = new char[8]); // 8�ֽ���Ч����

							br.read(buf = new char[2]); // ���ⳤ��
							int TitleLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

							br.read(buf = new char[2]); // �����ҳ���
							int ArtistLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

							br.read(buf = new char[2]); // ��Ȩ����
							int CopyrightLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

							br.read(buf = new char[2]); // ��ע����
							int CommentLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

							br.read(buf = new char[2]); // 2�ֽ���Ч����

							// ��ȡ����
							br.read(buf = new char[TitleLength]);
							mt.setTitle(Common.UnicodeCharToString(buf));

							// ��ȡ������
							br.read(buf = new char[ArtistLength]);
							if (mt.getArtist().equals("")) // �����չ������û�д���Ϣ�������
								mt.setArtist(Common.UnicodeCharToString(buf));

							br.read(buf = new char[CopyrightLength]); // ������Ȩ˵��

							// ��ȡ��ע
							br.read(buf = new char[CommentLength]);
							mt.setComment(Common.UnicodeCharToString(buf));
						}
					}
				}
				else if (Arrays.equals(buf, StandardHead))
				{// ��׼
					br.read(buf = new char[8]); // 8�ֽ���Ч����

					br.read(buf = new char[2]); // ���ⳤ��
					int TitleLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

					br.read(buf = new char[2]); // �����ҳ���
					int ArtistLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

					br.read(buf = new char[2]); // ��Ȩ����
					int CopyrightLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

					br.read(buf = new char[2]); // ��ע����
					int CommentLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ

					br.read(buf = new char[2]); // 2�ֽ���Ч����

					// ��ȡ����
					br.read(buf = new char[TitleLength]);
					mt.setTitle(Common.UnicodeCharToString(buf));

					// ��ȡ������
					br.read(buf = new char[ArtistLength]);
					mt.setArtist(Common.UnicodeCharToString(buf));

					br.read(buf = new char[CopyrightLength]); // ������Ȩ˵��

					// ��ȡ��ע
					br.read(buf = new char[CommentLength]);
					mt.setComment(Common.UnicodeCharToString(buf));

					// ��ʼ��ȡ��չͷ
					do
					{// �����հ��ַ�
						br.read(buf = new char[1]);
					}
					while ((int) buf[0] == 0);

					boolean IsExtendHeader = true; // �Ƿ������׼ͷ����Ϣ

					if (Integer.toHexString((int) buf[0]).equals(ExtendHeadString[0]))
					{
						for (int i = 1; i <= 15; i++)
						{
							br.read(buf = new char[1]);

							String strHex = Integer.toHexString((int) buf[0]).toUpperCase();
							if (strHex.length() == 1)
								strHex = "0" + strHex;

							if (!strHex.equals(ExtendHeadString[i]))
							{
								IsExtendHeader = false;
								break;
							}
						}

						if (IsExtendHeader)
						{// �ҵ���չͷ
							br.read(buf = new char[8]); // �ٴηŹ�8�ֽڣ��˴����õ�8�ֽڱ�־λ��
							br.read(buf = new char[2]); // ��չ��ǩ���ܸ���
							int ExtendCount = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ
							for (int i = 0; i < ExtendCount; i++)
							{
								br.read(buf = new char[2]); // ��չ���Ƴ���
								int FiledNameLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ
								br.read(buf = new char[FiledNameLength]); // ��ȡ��չ����
								String strFieldName = Common.UnicodeCharToString(buf);

								br.read(buf = new char[2]); // Flag����ʱ����

								br.read(buf = new char[2]); // ��չ�ֶγ���
								int FiledLength = Integer.valueOf(Common.DecodeUnicodeHex(buf), 16); // ת������ֵ
								br.read(buf = new char[FiledLength]); // ��ȡ��չ�ֶ�

								if (strFieldName.equals("WM/TrackNumber"))
									mt.setTrack(Common.UnicodeCharToString(buf));
								else if (strFieldName.equals("WM/Track"))
									mt.setTrack(Common.UnicodeCharToString(buf));
								else if (strFieldName.equals("WM/AlbumArtist"))
									mt.setArtist(Common.UnicodeCharToString(buf));
								else if (strFieldName.equals("WM/AlbumTitle"))
									mt.setAlbum(Common.UnicodeCharToString(buf));
								else if (strFieldName.equals("WM/Year"))
									mt.setYear(Common.UnicodeCharToString(buf));
								else if (strFieldName.equals("WM/Genre"))
									mt.setGener(Common.UnicodeCharToString(buf));
								else if (strFieldName.equals("WM/GenreID"))
									mt.setGener(Common.UnicodeCharToString(buf));
								else if (strFieldName.equals("WM/Lyrics"))
									mt.setLyric(Common.UnicodeCharToString(buf));
							}
						}
					}
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("Title", mt.getTitle());
				map.put("Artist", mt.getArtist());
				map.put("Album", mt.getAlbum());
				map.put("Comment", mt.getComment());
				map.put("Year", mt.getYear());
				map.put("Track", mt.getTrack());
				map.put("Genre", mt.getGener());
				map.put("MusicPath", path);
				map.put("LRCPath", path.substring(0, path.lastIndexOf(".")) + ".lrc");
				map.put("ID3Checked", "1");

				if (!mt.getArtist().equals("") && !mt.getAlbum().equals(""))
					map.put("SongInfo", mt.getArtist() + " - " + mt.getAlbum());
				else if (!mt.getArtist().equals("") || !mt.getAlbum().equals(""))
					map.put("SongInfo", mt.getArtist() + mt.getAlbum());

				br.close();
				is.close();
				return map;
			}
			else
			{// ����ASF��ʽ
				br.close();
				is.close();
				return null;
			}
		}
		catch (Exception e)
		{
			if (e.getMessage() != null)
				Log.w(Common.LOGCAT_TAG, e.getMessage());
			else
				e.printStackTrace();

			return null;
		}
	}

	/* ��ȡID3��ǩ */
	public static ID3Tag ReadID3(String path)
	{
		try
		{
			File f = new File(path);
			MediaFile mf = new MP3File(f);
			ID3Tag[] ID3 = mf.getTags();

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

			return null;
		}
		catch (Exception e)
		{
			if (e.getMessage() != null)
				Log.w(Common.LOGCAT_TAG, e.getMessage());
			else
				e.printStackTrace();

			return null;
		}
	}

	/* ���±���ID3��ǩΪGBK */
	public static ID3Tag ReCodecTag(ID3Tag ID3)
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
				if (e.getMessage() != null)
					Log.w(Common.LOGCAT_TAG, e.getMessage());
				else
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
					if (e.getMessage() != null)
						Log.w(Common.LOGCAT_TAG, e.getMessage());
					else
						e.printStackTrace();
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

	/* ���ò����б���Ŀ���� */
	public static Map<String, Object> GetMP3Info(String path, String oldname)
	{
		// 148 �����ɣ�80 ���������ɺ� 68 ����չ���ɣ�
		String Genre[] = { "Blues", "ClassicRock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "NewAge", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock",
				"Techno", "Industrial", "Alternative", "Ska", "DeathMetal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical",
				"Instrumental", "Acid", "House", "Game", "SoundClip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "InstrumentalPop", "InstrumentalRock", "Ethnic",
				"Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "SouthernRock", "Comedy", "Cult", "Gangsta", "Top", "ChristianRap", "Pop/Funk", "Jungle",
				"NativeAmerican", "Cabaret", "NewWave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "AcidPunk", "AcidJazz", "Polka", "Retro", "Musical", "Rock&Roll", "HardRock",
				"Folk", "Folk-Rock", "NationalFolk", "Swing", "FastFusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "GothicRock", "ProgessiveRock", "PsychedelicRock",
				"SymphonicRock", "SlowRock", "BigBand", "Chorus", "EasyListening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "ChamberMusic", "Sonata", "Symphony", "BootyBass", "Primus",
				"PornGroove", "Satire", "SlowJam", "Club", "Tango", "Samba", "Folklore", "Ballad", "PowerBallad", "RhythmicSoul", "Freestyle", "Duet", "PunkRock", "DrumSolo", "Acapella",
				"Euro-House", "DanceHall", "Goa", "Drum&Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "PolskPunk", "Beat", "ChristianGangstaRap", "HeavyMetal",
				"BlackMetal", "Crossover", "ContemporaryChristian", "ChristianRock", "Merengue", "Salsa", "TrashMetal", "Anime", "JPop", "Synthpop" };

		Map<String, Object> map = new HashMap<String, Object>();

		ID3Tag ID3All = (ID3Tag) ReadID3(path);
		if (ID3All != null)
		{
			if (ID3All instanceof ID3V2_3_0Tag)
			{
				ID3V2_3_0Tag ID3v2 = (ID3V2_3_0Tag) ID3All;

				String strGenre = ID3v2.getGenre();
				if (strGenre.indexOf("((") != -1 && strGenre.lastIndexOf("") != -1)
				{
					if (Common.IsNumeric(strGenre.substring(strGenre.indexOf("((") + 2, strGenre.lastIndexOf("") - 1)))
						strGenre = Genre[Integer.parseInt(strGenre.substring(strGenre.indexOf("((") + 2, strGenre.lastIndexOf("") - 1))];
					else
						strGenre = Genre[12]; // ����
				}
				map.put("Genre", strGenre);
				map.put("Artist", ID3v2.getArtist());
				map.put("Album", ID3v2.getAlbum());
				map.put("Comment", ID3v2.getComment());
				map.put("ID3Checked", "1");

				if (!ID3v2.getTitle().equals(""))
					map.put("Title", ID3v2.getTitle());
				else
					map.put("Title", oldname.substring(oldname.lastIndexOf("/") + 1));

				if (!ID3v2.getArtist().equals("") && !ID3v2.getAlbum().equals(""))
					map.put("SongInfo", ID3v2.getArtist() + " - " + ID3v2.getAlbum());
				else if (!ID3v2.getArtist().equals("") || !ID3v2.getAlbum().equals(""))
					map.put("SongInfo", ID3v2.getArtist() + ID3v2.getAlbum());

				try
				{
					map.put("Year", String.valueOf(ID3v2.getYear()));
					map.put("Track", String.valueOf(ID3v2.getTrackNumber()));
				}
				catch (Exception e)
				{
					if (e.getMessage() != null)
						Log.w(Common.LOGCAT_TAG, e.getMessage());
					else
						e.printStackTrace();
				}
			}
			else if (ID3All instanceof ID3V1_1Tag)
			{
				ID3V1_1Tag ID3v1_1 = (ID3V1_1Tag) ID3All;
				map.put("Artist", ID3v1_1.getArtist());
				map.put("Album", ID3v1_1.getAlbum());
				map.put("Comment", ID3v1_1.getComment());
				map.put("Year", ID3v1_1.getYear());
				map.put("Track", String.valueOf(ID3v1_1.getAlbumTrack()));
				map.put("Genre", String.valueOf(ID3v1_1.getGenre()));
				map.put("ID3Checked", "1");

				if (!ID3v1_1.getTitle().equals(""))
					map.put("Title", ID3v1_1.getTitle());
				else
					map.put("Title", oldname.substring(oldname.lastIndexOf("/") + 1));

				if (!ID3v1_1.getArtist().equals("") && !ID3v1_1.getAlbum().equals(""))
					map.put("SongInfo", ID3v1_1.getArtist() + " - " + ID3v1_1.getAlbum());
				else if (!ID3v1_1.getArtist().equals("") || !ID3v1_1.getAlbum().equals(""))
					map.put("SongInfo", ID3v1_1.getArtist() + ID3v1_1.getAlbum());
			}
			else if (ID3All instanceof ID3V1_0Tag)
			{
				ID3V1_0Tag ID3v1_0 = (ID3V1_0Tag) ID3All;
				map.put("Artist", ID3v1_0.getArtist());
				map.put("Album", ID3v1_0.getAlbum());
				map.put("Comment", ID3v1_0.getComment());
				map.put("Year", ID3v1_0.getYear());
				map.put("Track", "");
				map.put("Genre", String.valueOf(ID3v1_0.getGenre()));
				map.put("ID3Checked", "1");

				if (!ID3v1_0.getTitle().equals(""))
					map.put("Title", ID3v1_0.getTitle());
				else
					map.put("Title", oldname.substring(oldname.lastIndexOf("/") + 1));

				if (!ID3v1_0.getArtist().equals("") && !ID3v1_0.getAlbum().equals(""))
					map.put("SongInfo", ID3v1_0.getArtist() + " - " + ID3v1_0.getAlbum());
				else if (!ID3v1_0.getArtist().equals("") || !ID3v1_0.getAlbum().equals(""))
					map.put("SongInfo", ID3v1_0.getArtist() + ID3v1_0.getAlbum());
			}
		}
		else
		{
			map.put("Title", oldname.substring(oldname.lastIndexOf("/") + 1));
			map.put("SongInfo", "");
			map.put("Artist", "");
			map.put("Album", "");
			map.put("Comment", "");
			map.put("Year", "");
			map.put("Track", "");
			map.put("Genre", "");
			map.put("ID3Checked", "0");
		}

		// ��������·��
		map.put("MusicPath", path);
		map.put("LRCPath", path.substring(0, path.lastIndexOf(".")) + ".lrc");

		return map;
	}

	/* ��ȡ���ֱ�ǩ��Ϣ */
	public static Map<String, Object> GetMusicInfo(Activity act, String path, String oldname)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Cursor cursor = act.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, "_data=?", new String[] { path }, Media.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.moveToFirst())
		{// ���ȶ�ȡϵͳ������
			map.put("Title", cursor.getColumnIndexOrThrow(Media.TITLE));
			map.put("SongInfo", cursor.getColumnIndexOrThrow(Media.ARTIST) + " - " + cursor.getColumnIndexOrThrow(Media.ALBUM));
			map.put("Artist", cursor.getColumnIndexOrThrow(Media.ARTIST));
			map.put("Album", cursor.getColumnIndexOrThrow(Media.ALBUM));
			map.put("Comment", "");
			map.put("Year", cursor.getColumnIndexOrThrow(Media.YEAR));
			map.put("Track", cursor.getColumnIndexOrThrow(Media.TRACK));
			map.put("Genre", "");
			map.put("MusicPath", path);
			map.put("LRCPath", path.substring(0, path.lastIndexOf(".")) + ".lrc");
			map.put("ID3Checked", "1");
		}
		else
		{// ϵͳ�в����ڸ�����
			if (path.substring(path.lastIndexOf(".")).equals(".mp3"))
				map = GetMP3Info(path, oldname); // �����MP3�������ж�ȡ
			else
			{// WMA���ÿ�
				map = ReadASFTag(path);

				// ���û�ж�����Ϣ
				if (map == null)
				{
					map = new HashMap<String, Object>();
					map.put("Title", oldname.substring(oldname.lastIndexOf("/") + 1));
					map.put("SongInfo", "");
					map.put("Artist", "");
					map.put("Album", "");
					map.put("Comment", "");
					map.put("Year", "");
					map.put("Track", "");
					map.put("Genre", "");
					map.put("MusicPath", path);
					map.put("LRCPath", path.substring(0, path.lastIndexOf(".")) + ".lrc");
					map.put("ID3Checked", "0");
				}
			}
		}

		return map;
	}
}