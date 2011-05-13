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

import android.graphics.Paint;

public class Common
{
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

	// /* 铃声类型 */
	// public enum RingType
	// {
	// RINGTONE, ALARM
	// }
	//
	// /* 将指定路径的音乐设为铃声 */
	// public static void SetToRingtongs(Context context, String path, RingType
	// rt)
	// {
	//
	// }
}