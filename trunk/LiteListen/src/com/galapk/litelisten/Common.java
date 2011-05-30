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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Common
{
	public static String LOGCAT_TAG = "LiteListenLog"; // Logcat标签

	/* 检测Wi-Fi是否连接 */
	public static boolean IsWiFiConnected(Context context)
	{
		ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected())
						return true;
				}
			}
		}

		return false;
	}

	/* 获取字符串宽度 */
	public static float GetTextWidth(String Sentence, float Size)
	{
		Paint FontPaint = new Paint();
		FontPaint.setTextSize(Size);
		return FontPaint.measureText(Sentence.trim()) + (int) (Size * 0.1); // 留点余地
	}

	/* 获取随机正整数 */
	public static int GetRandomIndex(int min, int max)
	{
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	/* 判断是否为数字 */
	public static boolean IsNumeric(String number)
	{
		for (int i = 0; i < number.length(); i++)
		{
			if (!Character.isDigit(number.charAt(i)))
				return false;
		}

		return true;
	}

	/* 以POST方法传递数据 */
	public static boolean CallURLPost(String URL, int TimeOut)
	{
		try
		{
			URLConnection conn = new URL(URL).openConnection();
			conn.setConnectTimeout(TimeOut);
			conn.connect();
			conn.getContentType(); // 执行到这里才算真正调到了

			return true;
		}
		catch (Exception e)
		{
			if (e.getMessage() != null)
				Log.w(Common.LOGCAT_TAG, e.getMessage());
			else
				e.printStackTrace();

			return false;
		}
	}

	/* 获取指定URL所对应的文本内容 */
	public static String GetHTTPContent(String strURL, String Encoding)
	{
		try
		{
			URLConnection conn = new URL(strURL).openConnection();
			conn.connect();
			String contentType = conn.getContentType();
			if (contentType == null)
				contentType = Encoding;

			final Pattern ptnCharset = Pattern.compile("(?i)\\bcharset=([^\\s;]+)");
			Matcher m = ptnCharset.matcher(contentType);
			String Encoder = Encoding;
			if (m.find())
				Encoder = m.group(1);

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Encoder));
			char[] str = new char[4096];
			StringBuilder builder = new StringBuilder();
			for (int len; (len = reader.read(str)) > -1;)
				builder.append(str, 0, len);
			return builder.toString();
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

	/* 检查更新 */
	public static String CheckForUpdate(int CurrentVersion)
	{
		int RemoteVersion = Integer.parseInt(GetHTTPContent("http://www.littledai.com/LiteListen/GetVersion.php", "utf-8"));

		if (RemoteVersion > CurrentVersion)
			return String.valueOf(RemoteVersion);
		else
			return "";
	}

	/* 以HTTP方式获取文件 */
	public static File GetFileFromHTTP(String URL, String TempFilePrefix, String TempFileSuffix)
	{
		try
		{
			URL url = new URL(URL);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();

			InputStream is = conn.getInputStream();
			if (is != null)
			{
				File f = File.createTempFile(TempFilePrefix, TempFileSuffix);
				FileOutputStream fos = new FileOutputStream(f);
				byte buff[] = new byte[128];

				while (true)
				{
					int readbyte = is.read(buff);

					if (readbyte <= 0)
						break;

					fos.write(buff, 0, readbyte);
				}

				is.close();
				return f;
			}
			else
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

	/* 获取更新文件 */
	public static File GetUpdate()
	{
		return GetFileFromHTTP("http://www.littledai.com/LiteListen/LiteListen.apk", "LiteListen", ".apk");
	}

	/* 获取文件的MIME类型，可追加 */
	public static String GetMIMEType(File f)
	{
		String strSuffix = f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length()).toLowerCase();

		if (strSuffix.equals("apk"))
			return "application/vnd.android.package-archive";
		else
			return "/*";
	}

	public static String getLOGCAT_TAG()
	{
		return LOGCAT_TAG;
	}

	public static void setLOGCAT_TAG(String lOGCATTAG)
	{
		LOGCAT_TAG = lOGCATTAG;
	}
}