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
	private static final float PI = 3.1415926f; // 圆周率

	// 画笔
	private Paint paintCircleShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // 颜色环阴影
	private Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG); // 颜色环
	private Paint paintCenterShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // 中心颜色预览阴影
	private Paint paintCenter = new Paint(Paint.ANTI_ALIAS_FLAG); // 中心颜色预览
	private Paint paintGrayShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // 颜色灰度阴影
	private Paint paintGray = new Paint(Paint.ANTI_ALIAS_FLAG); // 颜色灰度
	private Paint paintLightShadow = new Paint(Paint.ANTI_ALIAS_FLAG); // 颜色灰度阴影
	private Paint paintLight = new Paint(Paint.ANTI_ALIAS_FLAG); // 颜色灰度

	private double Zoom = 1; // 缩放比例

	// 拾色器颜色
	private int[] arrColorGray; // 颜色灰度所包含的颜色数组
	private final int[] arrColorCircle = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 }; // 颜色环所包含的颜色数组

	// 绘图许可标志
	private boolean mRedrawHSV;
	private boolean IsPressCenter; // 手指位置是否按在圆心
	private boolean IsMoveCenter; // 手指位置是否位于圆心

	// 位置常量
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

		// 颜色环阴影
		paintCircleShadow.setColor(0xFF000000);
		paintCircleShadow.setStyle(Paint.Style.STROKE);
		paintCircleShadow.setStrokeWidth((float) (32 * Zoom));

		// 颜色环
		paintCircle.setShader(new SweepGradient(0, 0, arrColorCircle, null));
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth((float) (32 * Zoom));

		// 颜色预览阴影
		paintCenterShadow.setColor(0xFF000000);
		paintCenterShadow.setStrokeWidth((float) (5 * Zoom));

		// 颜色预览
		paintCenter.setColor(color);
		paintCenter.setStrokeWidth((float) (5 * Zoom));

		// 颜色灰度阴影
		paintGrayShadow.setColor(0xFF000000);
		paintGrayShadow.setStrokeWidth((float) (30 * Zoom));

		// 颜色灰度
		arrColorGray = new int[] { 0xFFFFFFFF, color, 0xFF000000 }; // 设置颜色灰度等级
		paintGray.setStrokeWidth((float) (30 * Zoom));

		// 颜色光谱阴影
		paintLightShadow.setColor(0xFF000000);
		paintLightShadow.setStrokeWidth((float) (60 * Zoom));

		// 颜色光谱
		paintLight.setStrokeWidth((float) (60 * Zoom));

		mRedrawHSV = true;
	}

	/* 重写绘图事件 */
	@Override
	protected void onDraw(Canvas canvas)
	{
		// 绘图
		canvas.translate(CenterX, CenterY);
		float r = CenterX - paintCircle.getStrokeWidth() * 0.5f; // 半径
		int color = paintCenter.getColor();
		dlg.getTxtColor().setText("#" + Integer.toHexString(color).substring(2).toUpperCase());
		strColor = "#" + Integer.toHexString(color).substring(2).toUpperCase();

		if (mRedrawHSV)
		{
			arrColorGray[1] = color;
			paintGray.setShader(new LinearGradient(CenterX, -CenterY, CenterX, (float) (100 * Zoom), arrColorGray, null, Shader.TileMode.CLAMP));
		}

		canvas.drawOval(new RectF(-r + 3, -r + 3, r + 3, r + 3), paintCircleShadow); // 颜色环阴影
		canvas.drawOval(new RectF(-r, -r, r, r), paintCircle); // 颜色环
		canvas.drawCircle(3, 3, CenterRadius, paintCenterShadow); // 颜色预览阴影
		canvas.drawCircle(0, 0, CenterRadius, paintCenter); // 颜色预览
		canvas.drawRect(new RectF(CenterX + (float) (18 * Zoom), -CenterY + 3, CenterX + (float) (48 * Zoom), (float) (103 * Zoom)), paintGrayShadow); // 灰度等级阴影
		canvas.drawRect(new RectF(CenterX + (float) (15 * Zoom), -CenterY, CenterX + (float) (45 * Zoom), (float) (100 * Zoom)), paintGray); // 灰度等级

		if (IsPressCenter)
		{// 如果手指单击了圆心，准备围绕预览颜色画圈
			paintCenter.setStyle(Paint.Style.STROKE); // 画圈样式

			if (IsMoveCenter) // 手指在中心
				paintCenter.setAlpha(0xFF);
			else
				// 手指移开中心
				paintCenter.setAlpha(0x66);

			canvas.drawCircle(0, 0, CenterRadius + paintCenter.getStrokeWidth(), paintCenter); // 画圈
			paintCenter.setStyle(Paint.Style.FILL); // 还原为填充样式
			paintCenter.setColor(color); // 还原先前的颜色（防止被透明处理）
		}

		mRedrawHSV = true;
	}

	/* 控制视图大小 */
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
				{// 计算颜色环上的颜色
					float angle = (float) java.lang.Math.atan2(y, x);
					float unit = angle / (2 * PI);
					if (unit < 0)
						unit += 1;
					paintCenter.setColor(interpColor(arrColorCircle, unit));
					invalidate();
				}
				else
				{// 计算灰度条上的颜色
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