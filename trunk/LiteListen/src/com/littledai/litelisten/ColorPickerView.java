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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View
{
	private static final float PI = 3.1415926f; // Բ����

	// ����
	private Paint paintCircleShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // ��ɫ����Ӱ
	private Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG); // ��ɫ��
	private Paint paintCenterShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // ������ɫԤ����Ӱ
	private Paint paintCenter = new Paint(Paint.ANTI_ALIAS_FLAG); // ������ɫԤ��
	private Paint paintGrayShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // ��ɫ�Ҷ���Ӱ
	private Paint paintGray = new Paint(Paint.ANTI_ALIAS_FLAG); // ��ɫ�Ҷ�
	private Paint paintLightShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // ��ɫ�Ҷ���Ӱ
	private Paint paintLight = new Paint(Paint.ANTI_ALIAS_FLAG); // ��ɫ�Ҷ�

	private double Zoom = 1; // ���ű���

	// ʰɫ����ɫ
	private int[] arrColorGray; // ��ɫ�Ҷ�����������ɫ����
	private final int[] arrColorCircle = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 }; // ��ɫ������������ɫ����

	// ��ͼ��ɱ�־
	private boolean mRedrawHSV;
	private boolean IsPressCenter; // ��ָλ���Ƿ���Բ��
	private boolean IsMoveCenter; // ��ָλ���Ƿ�λ��Բ��

	// λ�ó���
	private int CenterX = 100;
	private int CenterY = 100;
	private int CenterRadius = 30;

	private ColorDialog dlg = null;
	private String strColor = "";

	public ColorPickerView(Context context, int color, double Zoom, ColorDialog dlg)
	{
		super(context);

		this.dlg = dlg;
		this.Zoom = Zoom;

		CenterX = (int) (100 * Zoom);
		CenterY = (int) (100 * Zoom);
		CenterRadius = (int) (30 * Zoom);

		// ��ɫ����Ӱ
		paintCircleShadow.setColor(0xFF000000);
		paintCircleShadow.setStyle(Paint.Style.STROKE);
		paintCircleShadow.setStrokeWidth((float) (32 * Zoom));

		// ��ɫ��
		paintCircle.setShader(new SweepGradient(0, 0, arrColorCircle, null));
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth((float) (32 * Zoom));

		// ��ɫԤ����Ӱ
		paintCenterShadow.setColor(0xFF000000);
		paintCenterShadow.setStrokeWidth((float) (5 * Zoom));

		// ��ɫԤ��
		paintCenter.setColor(color);
		paintCenter.setStrokeWidth((float) (5 * Zoom));

		// ��ɫ�Ҷ���Ӱ
		paintGrayShadow.setColor(0xFF000000);
		paintGrayShadow.setStrokeWidth((float) (30 * Zoom));

		// ��ɫ�Ҷ�
		arrColorGray = new int[] { 0xFFFFFFFF, color, 0xFF000000 }; // ������ɫ�Ҷȵȼ�
		paintGray.setStrokeWidth((float) (30 * Zoom));

		// ��ɫ������Ӱ
		paintLightShadow.setColor(0xFF000000);
		paintLightShadow.setStrokeWidth((float) (60 * Zoom));

		// ��ɫ����
		paintLight.setStrokeWidth((float) (60 * Zoom));

		mRedrawHSV = true;
	}

	/* ��д��ͼ�¼� */
	@Override
	protected void onDraw(Canvas canvas)
	{
		// ��ͼ
		canvas.translate(CenterX, CenterY);
		float r = CenterX - paintCircle.getStrokeWidth() * 0.5f; // �뾶
		int color = paintCenter.getColor();
		dlg.getTxtColor().setText("#" + Integer.toHexString(color).substring(2).toUpperCase());
		strColor = "#" + Integer.toHexString(color).substring(2).toUpperCase();

		if (mRedrawHSV)
		{
			arrColorGray[1] = color;
			paintGray.setShader(new LinearGradient(CenterX, -CenterY, CenterX, (float) (100 * Zoom), arrColorGray, null, Shader.TileMode.CLAMP));
		}

		canvas.drawOval(new RectF(-r + 3, -r + 3, r + 3, r + 3), paintCircleShadow); // ��ɫ����Ӱ
		canvas.drawOval(new RectF(-r, -r, r, r), paintCircle); // ��ɫ��
		canvas.drawCircle(3, 3, CenterRadius, paintCenterShadow); // ��ɫԤ����Ӱ
		canvas.drawCircle(0, 0, CenterRadius, paintCenter); // ��ɫԤ��
		canvas.drawRect(new RectF(CenterX + (float) (18 * Zoom), -CenterY + 3, CenterX + (float) (48 * Zoom), (float) (103 * Zoom)), paintGrayShadow); // �Ҷȵȼ���Ӱ
		canvas.drawRect(new RectF(CenterX + (float) (15 * Zoom), -CenterY, CenterX + (float) (45 * Zoom), (float) (100 * Zoom)), paintGray); // �Ҷȵȼ�

		if (IsPressCenter)
		{// �����ָ������Բ�ģ�׼��Χ��Ԥ����ɫ��Ȧ
			paintCenter.setStyle(Paint.Style.STROKE); // ��Ȧ��ʽ

			if (IsMoveCenter) // ��ָ������
				paintCenter.setAlpha(0xFF);
			else
				// ��ָ�ƿ�����
				paintCenter.setAlpha(0x66);

			canvas.drawCircle(0, 0, CenterRadius + paintCenter.getStrokeWidth(), paintCenter); // ��Ȧ
			paintCenter.setStyle(Paint.Style.FILL); // ��ԭΪ�����ʽ
			paintCenter.setColor(color); // ��ԭ��ǰ����ɫ����ֹ��͸������
		}

		mRedrawHSV = true;
	}

	/* ������ͼ��С */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(CenterX * 2 + 50, CenterY * 2 + 23);
	}

	private int ave(int s, int d, float p)
	{
		return s + java.lang.Math.round(p * (d - s));
	}

	private int interpColor(int colors[], float unit)
	{
		if (unit <= 0)
		{
			return colors[0];
		}
		if (unit >= 1)
		{
			return colors[colors.length - 1];
		}

		float p = unit * (colors.length - 1);
		int i = (int) p;
		p -= i;

		int c0 = colors[i];
		int c1 = colors[i + 1];
		int a = ave(Color.alpha(c0), Color.alpha(c1), p);
		int r = ave(Color.red(c0), Color.red(c1), p);
		int g = ave(Color.green(c0), Color.green(c1), p);
		int b = ave(Color.blue(c0), Color.blue(c1), p);

		return Color.argb(a, r, g, b);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = event.getX() - CenterX;
		float y = event.getY() - CenterY;
		boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= CenterRadius;

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				IsPressCenter = inCenter;
				if (inCenter)
				{
					IsMoveCenter = true;
					invalidate();
					break;
				}
			}
			case MotionEvent.ACTION_MOVE:
			{
				if (IsPressCenter)
				{
					if (IsMoveCenter != inCenter)
					{
						IsMoveCenter = inCenter;
						invalidate();
					}
				}
				else if ((x >= -CenterX && x <= CenterX) && (y >= -CenterY && y <= CenterY))
				{// ������ɫ���ϵ���ɫ
					float angle = (float) java.lang.Math.atan2(y, x);
					float unit = angle / (2 * PI);
					if (unit < 0)
						unit += 1;
					paintCenter.setColor(interpColor(arrColorCircle, unit));
					invalidate();
				}
				else
				{// ����Ҷ����ϵ���ɫ
					int a, r, g, b, c0, c1;
					float p;

					if (y < 0)
					{
						c0 = arrColorGray[0];
						c1 = arrColorGray[1];
						p = (y + 100) / 100;
					}
					else
					{
						c0 = arrColorGray[1];
						c1 = arrColorGray[2];
						p = y / 100;
					}

					a = ave(Color.alpha(c0), Color.alpha(c1), p);
					r = ave(Color.red(c0), Color.red(c1), p);
					g = ave(Color.green(c0), Color.green(c1), p);
					b = ave(Color.blue(c0), Color.blue(c1), p);

					paintCenter.setColor(Color.argb(a, r, g, b));
					mRedrawHSV = false;
					invalidate();
				}

				break;
			}
			case MotionEvent.ACTION_UP:
			{
				if (IsPressCenter)
				{
					IsPressCenter = false;
					invalidate();
				}
				break;
			}
		}

		return true;
	}

	public Paint getPaintCircleShadow()
	{
		return paintCircleShadow;
	}

	public void setPaintCircleShadow(Paint paintCircleShadow)
	{
		this.paintCircleShadow = paintCircleShadow;
	}

	public Paint getPaintCircle()
	{
		return paintCircle;
	}

	public void setPaintCircle(Paint paintCircle)
	{
		this.paintCircle = paintCircle;
	}

	public Paint getPaintCenterShadow()
	{
		return paintCenterShadow;
	}

	public void setPaintCenterShadow(Paint paintCenterShadow)
	{
		this.paintCenterShadow = paintCenterShadow;
	}

	public Paint getPaintCenter()
	{
		return paintCenter;
	}

	public void setPaintCenter(Paint paintCenter)
	{
		this.paintCenter = paintCenter;
	}

	public Paint getPaintGrayShadow()
	{
		return paintGrayShadow;
	}

	public void setPaintGrayShadow(Paint paintGrayShadow)
	{
		this.paintGrayShadow = paintGrayShadow;
	}

	public Paint getPaintGray()
	{
		return paintGray;
	}

	public void setPaintGray(Paint paintGray)
	{
		this.paintGray = paintGray;
	}

	public int[] getArrColorGray()
	{
		return arrColorGray;
	}

	public void setArrColorGray(int[] arrColorGray)
	{
		this.arrColorGray = arrColorGray;
	}

	public boolean ismRedrawHSV()
	{
		return mRedrawHSV;
	}

	public void setmRedrawHSV(boolean mRedrawHSV)
	{
		this.mRedrawHSV = mRedrawHSV;
	}

	public boolean isIsPressCenter()
	{
		return IsPressCenter;
	}

	public void setIsPressCenter(boolean isPressCenter)
	{
		IsPressCenter = isPressCenter;
	}

	public boolean isIsMoveCenter()
	{
		return IsMoveCenter;
	}

	public void setIsMoveCenter(boolean isMoveCenter)
	{
		IsMoveCenter = isMoveCenter;
	}

	public int[] getArrColorCircle()
	{
		return arrColorCircle;
	}

	public ColorDialog getDlg()
	{
		return dlg;
	}

	public void setDlg(ColorDialog dlg)
	{
		this.dlg = dlg;
	}

	public String getStrColor()
	{
		return strColor;
	}

	public void setStrColor(String strColor)
	{
		this.strColor = strColor;
	}

	public double getZoom()
	{
		return Zoom;
	}

	public void setZoom(double zoom)
	{
		Zoom = zoom;
	}
}