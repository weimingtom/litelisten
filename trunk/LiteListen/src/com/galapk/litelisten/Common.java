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

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

public class Common
{
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

	/* �������� */
	public enum RingType
	{
		RINGTONE, ALARM
	}

//	/* ��ָ��·����������Ϊ���� */
//	public static void SetToRingtongs(Context context, String path, RingType rt)
//	{
//		// �������������������ļ���Ϣ
//		ContentValues values = new ContentValues();
//		values.put(MediaStore.MediaColumns.DATA, path);
//		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
//
//		if (rt != null && rt == RingType.RINGTONE)
//		{
//			values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
//			values.put(MediaStore.Audio.Media.IS_ALARM, false);
//			values.put(MediaStore.Audio.Media.IS_MUSIC, false);
//		}
//		else if (rt != null && rt == RingType.ALARM)
//		{
//			values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
//			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
//			values.put(MediaStore.Audio.Media.IS_ALARM, true);
//			values.put(MediaStore.Audio.Media.IS_MUSIC, false);
//		}
//
//		Uri newUri = context.getContentResolver().insert(Uri.parse(path), values);
//		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
//	}
}