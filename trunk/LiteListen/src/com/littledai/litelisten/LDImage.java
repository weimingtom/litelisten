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

	/* ͨ��Drawable��ȡBitmap */
	public static Bitmap GetBitmap(Resources res, int DrawableID)
	{
		Drawable d = res.getDrawable(DrawableID);
		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap bmpReturn = bd.getBitmap();

		return bmpReturn;
	}

	/* ͨ��Bitmap��ȡDrawable */
	public static Drawable GetDrawable(Bitmap bmpSource)
	{
		return new BitmapDrawable(bmpSource);
	}

	/* ����ͼƬ͸���� */
	public static Bitmap SetAlpha(Bitmap bmpSource, int Transparency)
	{
		int[] argb = new int[bmpSource.getWidth() * bmpSource.getHeight()];
		bmpSource.getPixels(argb, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());// ���ͼƬ��ARGBֵ
		Transparency = Transparency * 255 / 100;
		for (int i = 0; i < argb.length; i++)
		{
			argb[i] = (Transparency << 24) | (argb[i] & 0x00FFFFFF);// �޸����2λ��ֵ
		}
		bmpSource = Bitmap.createBitmap(argb, bmpSource.getWidth(), bmpSource.getHeight(), Config.ARGB_8888);

		return bmpSource;
	}

	/* ����ͼƬģ�� */
	public static Bitmap SetBlur(Bitmap bmpSource, int Blur)
	{
		int pixels[] = new int[bmpSource.getWidth() * bmpSource.getHeight()]; // ��ɫ����
		int pixelsRawSource[] = new int[bmpSource.getWidth() * bmpSource.getHeight() * 3]; // ��ԭɫ����
		int pixelsRawNew[] = new int[bmpSource.getWidth() * bmpSource.getHeight() * 3]; // ��ԭɫ����
		bmpSource.getPixels(pixels, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());

		// ģ��ǿ��
		for (int k = 1; k <= Blur; k++)
		{
			// ��ͼƬ�л�ȡÿ��������ԭɫ��ֵ
			for (int i = 0; i < pixels.length; i++)
			{
				pixelsRawSource[i * 3 + 0] = Color.red(pixels[i]);
				pixelsRawSource[i * 3 + 1] = Color.green(pixels[i]);
				pixelsRawSource[i * 3 + 2] = Color.blue(pixels[i]);
			}

			// ȡÿ�����������ҵ��ƽ��ֵ���Լ���ֵ
			int CurrentPixel = bmpSource.getWidth() * 3 + 3; // ��ǰ��������ص㣬�ӵ�(2,2)��ʼ
			for (int i = 0; i < bmpSource.getHeight() - 3; i++) // �߶�ѭ��
			{
				for (int j = 0; j < bmpSource.getWidth() * 3; j++) // ���ѭ��
				{
					CurrentPixel += 1;
					// ȡ�������ң�ȡƽ��ֵ
					int sumColor = 0; // ��ɫ��
					sumColor = pixelsRawSource[CurrentPixel - bmpSource.getWidth() * 3]; // ��һ��
					sumColor = sumColor + pixelsRawSource[CurrentPixel - 3]; // ��һ��
					sumColor = sumColor + pixelsRawSource[CurrentPixel + 3]; // ��һ��
					sumColor = sumColor + pixelsRawSource[CurrentPixel + bmpSource.getWidth() * 3]; // ��һ��
					pixelsRawNew[CurrentPixel] = Math.round(sumColor / 4); // �������ص�
				}
			}

			// ������ԭɫ��ϳ�������ɫ
			for (int i = 0; i < pixels.length; i++)
			{
				pixels[i] = Color.rgb(pixelsRawNew[i * 3 + 0], pixelsRawNew[i * 3 + 1], pixelsRawNew[i * 3 + 2]);
			}
		}

		// Ӧ�õ�ͼ��
		Bitmap bmpReturn = Bitmap.createBitmap(bmpSource.getWidth(), bmpSource.getHeight(), Config.ARGB_8888);
		bmpReturn.setPixels(pixels, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());

		return bmpReturn;
	}

	/* ͼƬ���� */
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

	/* Բ��ͼƬ */
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

	/* ͼƬ��Ӱ */
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