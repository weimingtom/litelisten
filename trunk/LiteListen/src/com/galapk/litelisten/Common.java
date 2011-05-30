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
	public static String LOGCAT_TAG = "LiteListenLog"; // Logcat��ǩ

	/* ���Wi-Fi�Ƿ����� */
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

	/* ��ȡ�ַ������ */
	public static float GetTextWidth(String Sentence, float Size)
	{
		Paint FontPaint = new Paint();
		FontPaint.setTextSize(Size);
		return FontPaint.measureText(Sentence.trim()) + (int) (Size * 0.1); // �������
	}

	/* ��ȡ��������� */
	public static int GetRandomIndex(int min, int max)
	{
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	/* �ж��Ƿ�Ϊ���� */
	public static boolean IsNumeric(String number)
	{
		for (int i = 0; i < number.length(); i++)
		{
			if (!Character.isDigit(number.charAt(i)))
				return false;
		}

		return true;
	}

	/* ��POST������������ */
	public static boolean CallURLPost(String URL, int TimeOut)
	{
		try
		{
			URLConnection conn = new URL(URL).openConnection();
			conn.setConnectTimeout(TimeOut);
			conn.connect();
			conn.getContentType(); // ִ�е������������������

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

	/* ��ȡָ��URL����Ӧ���ı����� */
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

	/* ������ */
	public static String CheckForUpdate(int CurrentVersion)
	{
		int RemoteVersion = Integer.parseInt(GetHTTPContent("http://www.littledai.com/LiteListen/GetVersion.php", "utf-8"));

		if (RemoteVersion > CurrentVersion)
			return String.valueOf(RemoteVersion);
		else
			return "";
	}

	/* ��HTTP��ʽ��ȡ�ļ� */
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

	/* ��ȡ�����ļ� */
	public static File GetUpdate()
	{
		return GetFileFromHTTP("http://www.littledai.com/LiteListen/LiteListen.apk", "LiteListen", ".apk");
	}

	/* ��ȡ�ļ���MIME���ͣ���׷�� */
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