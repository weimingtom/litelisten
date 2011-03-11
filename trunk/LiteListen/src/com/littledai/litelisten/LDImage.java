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

package com.littledai.litelisten;

import java.io.File;
import java.io.FileOutputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class LDImage
{
	public static boolean SaveBitmap(Bitmap bmpToSave, String SavePath, String FileName, String Extension, int Quality, boolean ForceCreate)
	{
		try
		{
			if (SavePath.lastIndexOf("/") == SavePath.length() - 1)
				SavePath = SavePath.substring(0, SavePath.length() - 1);

			File f = new File(SavePath);
			if (!f.exists())
				f.mkdirs();
			else if (ForceCreate)
			{
				f.delete();
				f.mkdirs();
			}

			f = new File(SavePath + "/" + FileName);
			if (!f.exists())
				f.createNewFile();
			else if (ForceCreate)
			{
				f.delete();
				f.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(f);

			if (Quality > 100)
				Quality = 100;
			else if (Quality < 1)
				Quality = 1;

			if (Extension.equals("png"))
				bmpToSave.compress(Bitmap.CompressFormat.PNG, Quality, fos);
			else
				bmpToSave.compress(Bitmap.CompressFormat.JPEG, Quality, fos);

			fos.flush();
			fos.close();

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/* 通过Drawable获取Bitmap */
	public static Bitmap GetBitmap(Resources res, int DrawableID)
	{
		Drawable d = res.getDrawable(DrawableID);
		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap bmpReturn = bd.getBitmap();

		return bmpReturn;
	}

	/* 通过Bitmap获取Drawable */
	public static Drawable GetDrawable(Bitmap bmpSource)
	{
		return new BitmapDrawable(bmpSource);
	}

	/* 设置图片透明度 */
	public static Bitmap SetAlpha(Bitmap bmpSource, int Transparency)
	{
		int[] argb = new int[bmpSource.getWidth() * bmpSource.getHeight()];
		bmpSource.getPixels(argb, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());// 获得图片的ARGB值
		Transparency = Transparency * 255 / 100;
		for (int i = 0; i < argb.length; i++)
		{
			argb[i] = (Transparency << 24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值
		}
		bmpSource = Bitmap.createBitmap(argb, bmpSource.getWidth(), bmpSource.getHeight(), Config.ARGB_8888);

		return bmpSource;
	}

	/* 设置图片模糊 */
	public static Bitmap SetBlur(Bitmap bmpSource, int Blur)
	{
		int pixels[] = new int[bmpSource.getWidth() * bmpSource.getHeight()]; // 颜色数组
		int pixelsRawSource[] = new int[bmpSource.getWidth() * bmpSource.getHeight() * 3]; // 三原色数组
		int pixelsRawNew[] = new int[bmpSource.getWidth() * bmpSource.getHeight() * 3]; // 三原色数组
		bmpSource.getPixels(pixels, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());

		// 模糊强度
		for (int k = 1; k <= Blur; k++)
		{
			// 从图片中获取每个像素三原色的值
			for (int i = 0; i < pixels.length; i++)
			{
				pixelsRawSource[i * 3 + 0] = Color.red(pixels[i]);
				pixelsRawSource[i * 3 + 1] = Color.green(pixels[i]);
				pixelsRawSource[i * 3 + 2] = Color.blue(pixels[i]);
			}

			// 取每个点上下左右点的平均值作自己的值
			int CurrentPixel = bmpSource.getWidth() * 3 + 3; // 当前处理的像素点，从点(2,2)开始
			for (int i = 0; i < bmpSource.getHeight() - 3; i++) // 高度循环
			{
				for (int j = 0; j < bmpSource.getWidth() * 3; j++) // 宽度循环
				{
					CurrentPixel += 1;
					// 取上下左右，取平均值
					int sumColor = 0; // 颜色和
					sumColor = pixelsRawSource[CurrentPixel - bmpSource.getWidth() * 3]; // 上一点
					sumColor = sumColor + pixelsRawSource[CurrentPixel - 3]; // 左一点
					sumColor = sumColor + pixelsRawSource[CurrentPixel + 3]; // 右一点
					sumColor = sumColor + pixelsRawSource[CurrentPixel + bmpSource.getWidth() * 3]; // 下一点
					pixelsRawNew[CurrentPixel] = Math.round(sumColor / 4); // 设置像素点
				}
			}

			// 将新三原色组合成像素颜色
			for (int i = 0; i < pixels.length; i++)
			{
				pixels[i] = Color.rgb(pixelsRawNew[i * 3 + 0], pixelsRawNew[i * 3 + 1], pixelsRawNew[i * 3 + 2]);
			}
		}

		// 应用到图像
		Bitmap bmpReturn = Bitmap.createBitmap(bmpSource.getWidth(), bmpSource.getHeight(), Config.ARGB_8888);
		bmpReturn.setPixels(pixels, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());

		return bmpReturn;
	}

	/* 图片缩放 */
	public static Bitmap SetZoom(Bitmap bmpSource, int NewWidth, int NewHeight)
	{
		int width = bmpSource.getWidth();
		int height = bmpSource.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) NewWidth / width);
		float scaleHeight = ((float) NewHeight / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bmpSource, 0, 0, width, height, matrix, true);
		return newbmp;
	}

	/* 圆角图片 */
	public static Bitmap SetRound(Bitmap bmpSource, float Round)
	{
		Bitmap output = Bitmap.createBitmap(bmpSource.getWidth(), bmpSource.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bmpSource.getWidth(), bmpSource.getHeight());
		RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xff424242);
		canvas.drawRoundRect(rectF, Round, Round, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bmpSource, rect, rect, paint);

		return output;
	}

	/* 图片倒影 */
	public static Bitmap SetReflection(Bitmap bmpSource)
	{
		final int reflectionGap = 4;
		int width = bmpSource.getWidth();
		int height = bmpSource.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bmpSource, 0, height / 2, width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bmpSource, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bmpSource.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}
}