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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class LRCDownService
{
	final private static char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/* 从千千静听服务器下载歌词 */
	public static String GetLyricFromTT(String ID, String Artist, String Title)
	{
		String strURL = "http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?dl?Id={id}&Code={code}";
		if (ID != null && !ID.equals(""))
			strURL = strURL.replace("{id}", ID).replace("{code}", GetVerifyCode(Artist, Title, Integer.parseInt(ID, 10)));
		else
			return "";

		return Common.GetHTTPContent(strURL, "utf-8");
	}

	/* 解析千千静听LRC列表XML */
	public static List<Map<String, String>> AnalyzeXML(String xml)
	{
		String strRemaining = xml; // 截取后还剩余的文本内容
		List<Map<String, String>> lstLRC = new ArrayList<Map<String, String>>();
		while (strRemaining != null && !strRemaining.equals("") && strRemaining.indexOf("<lrc") != -1 && strRemaining.indexOf("</lrc>") != -1) // 有LRC标签
		{
			String strTemp = strRemaining.substring(strRemaining.indexOf("<lrc"), strRemaining.indexOf("</lrc>")).trim();
			strRemaining = strRemaining.substring(strRemaining.indexOf("</lrc>") + 5).trim();

			if (strTemp != null && !strTemp.equals("")) // 有LRC标签
			{
				Map<String, String> map = new HashMap<String, String>();
				map = AnalyzeElements(strTemp);
				lstLRC.add(map);
			}
			else
				continue;
		}

		return lstLRC;
	}

	/* 解析一行数据 */
	public static Map<String, String> AnalyzeElements(String Elements)
	{
		Map<String, String> map = new HashMap<String, String>();

		String strTemp = Elements.substring(Elements.indexOf("id=\"") + 4);
		map.put("ID", strTemp.substring(0, strTemp.indexOf("\"")));

		strTemp = Elements.substring(Elements.indexOf("artist=\"") + 8);
		map.put("Artist", strTemp.substring(0, strTemp.indexOf("\"")));

		strTemp = Elements.substring(Elements.indexOf("title=\"") + 7);
		map.put("Title", strTemp.substring(0, strTemp.indexOf("\"")));

		return map;
	}

	/* 搜索千千静听歌词服务器 */
	public static List<Map<String, String>> SearchLyricFromTT(String Artist, String Title)
	{
		try
		{
			String strURL = "http://ttlrcct.qianqian.com/dll/lyricsvr.dll?sh?Artist={ar}&Title={ti}&Flags=0";
			strURL = strURL.replace("{ar}", EncodingTo16LE(Artist)).replace("{ti}", EncodingTo16LE(Title));

			return AnalyzeXML(Common.GetHTTPContent(strURL, "utf-8"));
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

	/* 将字符串转换为UTF-16LE编码 */
	private static String EncodingTo16LE(String Source)
	{
		if (Source == null)
			Source = "";

		Source = Source.replace(" ", "").replace("'", "").toLowerCase();
		byte[] bytes = null;

		try
		{
			bytes = Source.getBytes("UTF-16LE");
		}
		catch (Exception e)
		{
			if (e.getMessage() != null)
				Log.w(Common.LOGCAT_TAG, e.getMessage());
			else
				e.printStackTrace();

			bytes = Source.getBytes();
		}

		char[] str = new char[2];
		StringBuilder builder = new StringBuilder();
		for (byte byteValue : bytes)
		{
			str[0] = digit[(byteValue >>> 4) & 0X0F];
			str[1] = digit[byteValue & 0X0F];
			builder.append(str);
		}

		return builder.toString();
	}

	/* 计算千千静听验证码 */
	public static String GetVerifyCode(String Artist, String Title, int lrcId)
	{
		try
		{
			byte[] bytes = (Artist + Title).getBytes("UTF-8");
			int[] song = new int[bytes.length];
			for (int i = 0; i < bytes.length; i++)
				song[i] = bytes[i] & 0xff;
			int intVal1 = 0, intVal2 = 0, intVal3 = 0;
			intVal1 = (lrcId & 0xFF00) >> 8;
			if ((lrcId & 0xFF0000) == 0)
				intVal3 = 0xFF & ~intVal1;
			else
				intVal3 = 0xFF & ((lrcId & 0x00FF0000) >> 16);

			intVal3 = intVal3 | ((0xFF & lrcId) << 8);
			intVal3 = intVal3 << 8;
			intVal3 = intVal3 | (0xFF & intVal1);
			intVal3 = intVal3 << 8;

			if ((lrcId & 0xFF000000) == 0)
				intVal3 = intVal3 | (0xFF & (~lrcId));
			else
				intVal3 = intVal3 | (0xFF & (lrcId >> 24));

			int uBound = bytes.length - 1;
			while (uBound >= 0)
			{
				int c = song[uBound];
				if (c >= 0x80)
					c = c - 0x100;
				intVal1 = c + intVal2;
				intVal2 = intVal2 << (uBound % 2 + 4);
				intVal2 = intVal1 + intVal2;
				uBound -= 1;
			}

			uBound = 0;
			intVal1 = 0;

			while (uBound <= bytes.length - 1)
			{
				int c = song[uBound];
				if (c >= 128)
					c = c - 256;
				int intVal4 = c + intVal1;
				intVal1 = intVal1 << (uBound % 2 + 3);
				intVal1 = intVal1 + intVal4;
				uBound += 1;
			}

			int intVal5 = intVal2 ^ intVal3;
			intVal5 = intVal5 + (intVal1 | lrcId);
			intVal5 = intVal5 * (intVal1 | intVal3);
			intVal5 = intVal5 * (intVal2 ^ lrcId);

			return String.valueOf(intVal5);
		}
		catch (Exception e)
		{
			if (e.getMessage() != null)
				Log.w(Common.LOGCAT_TAG, e.getMessage());
			else
				e.printStackTrace();

			return "";
		}
	}
}