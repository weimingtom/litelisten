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

import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatLRC extends LinearLayout
{
	// 手指按下的相对View的位置
	private float DownPosX = 0;
	private float DownPosY = 0;

	// 手指触摸的相对屏幕的位置
	private float x = 0;
	private float y = 0;

	private WindowManager wm;
	private WindowManager.LayoutParams layWM;

	// 歌词秀控件
	ImageView imgIcon;
	LinearLayout layMain;
	TextView txtLRC1;
	TextView txtLRC2;

	public FloatLRC(srcMain main)
	{
		super(main);
		wm = (WindowManager) main.getApplicationContext().getSystemService("window");
		layWM = main.getLayWM();

		// 设置专辑图标
		imgIcon = new ImageView(main);
		LinearLayout.LayoutParams layIcon = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layIcon.gravity = Gravity.CENTER_VERTICAL;
		imgIcon.setLayoutParams(layIcon);

		// 主体框架
		layMain = new LinearLayout(main);
		LinearLayout.LayoutParams layMainPars = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layMainPars.leftMargin = 5;
		layMain.setOrientation(LinearLayout.VERTICAL);
		layMain.setLayoutParams(layMainPars);

		// 第一句歌词
		txtLRC1 = new TextView(main);
		LinearLayout.LayoutParams layText1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		txtLRC1.setLayoutParams(layText1);
		txtLRC1.setTextSize(20);
		txtLRC1.setTextColor(Color.WHITE);
		txtLRC1.setShadowLayer(2, 2, 2, Color.BLACK);

		// 第二句歌词
		txtLRC2 = new TextView(main);
		LinearLayout.LayoutParams layText2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layText2.gravity = Gravity.RIGHT;
		txtLRC2.setLayoutParams(layText2);
		txtLRC2.setTextSize(20);
		txtLRC2.setTextColor(Color.WHITE);
		txtLRC2.setShadowLayer(2, 2, 2, Color.BLACK);

		// 将View依次装入框架
		layMain.addView(txtLRC1);
		layMain.addView(txtLRC2);
		addView(imgIcon);
		addView(layMain);
	}

	/* 设置歌词内容 */
	public void SetLRC(int IconResource, String Sentense1, String Sentense2)
	{
		imgIcon.setImageResource(IconResource);
		txtLRC1.setText(Sentense1);
		txtLRC2.setText(Sentense2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		x = event.getRawX();
		y = event.getRawY() - 25;

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				DownPosX = event.getX();
				DownPosY = event.getY();

				break;
			case MotionEvent.ACTION_MOVE:
				MoveView();
				break;

			case MotionEvent.ACTION_UP:
				MoveView();
				DownPosX = 0;
				DownPosY = 0;
				break;
		}
		return true;
	}

	/* 自动自己的位置 */
	private void MoveView()
	{
		layWM.x = (int) (x - DownPosX);
		layWM.y = (int) (y - DownPosY);
		wm.updateViewLayout(this, layWM);
	}
}