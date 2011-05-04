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

import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatLRC extends LinearLayout
{
	// ��ָ���µ����View��λ��
	private float DownPosX = 0;
	private float DownPosY = 0;

	// ��ָ�����������Ļ��λ��
	private float x = 0;
	private float y = 0;

	private WindowManager wm;
	private WindowManager.LayoutParams layWM;
	private srcMain main;

	// �����ؼ�
	ImageView imgIcon;
	LinearLayout layMain;
	TextView txtLRC1;
	TextView txtLRC2;

	public FloatLRC(srcMain main)
	{
		super(main);
		wm = (WindowManager) main.getApplicationContext().getSystemService("window");
		layWM = main.getLayWM();
		this.main = main;

		// ����ר��ͼ��
		imgIcon = new ImageView(main);
		LinearLayout.LayoutParams layIcon = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layIcon.gravity = Gravity.CENTER_VERTICAL;
		imgIcon.setLayoutParams(layIcon);

		// ������
		layMain = new LinearLayout(main);
		LinearLayout.LayoutParams layMainPars = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layMainPars.leftMargin = 5;
		layMain.setOrientation(LinearLayout.VERTICAL);
		layMain.setLayoutParams(layMainPars);

		// ��һ����
		txtLRC1 = new TextView(main);
		LinearLayout.LayoutParams layText1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		txtLRC1.setLayoutParams(layText1);
		txtLRC1.setTextSize(20);
		txtLRC1.setTextColor(Color.WHITE);
		txtLRC1.setShadowLayer(2, 2, 2, Color.BLACK);
		txtLRC1.setSingleLine(true);
		txtLRC1.setMarqueeRepeatLimit(1);
		txtLRC1.setEllipsize(TruncateAt.MARQUEE);

		// �ڶ�����
		txtLRC2 = new TextView(main);
		LinearLayout.LayoutParams layText2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layText2.gravity = Gravity.RIGHT;
		txtLRC2.setLayoutParams(layText2);
		txtLRC2.setTextSize(20);
		txtLRC2.setTextColor(Color.WHITE);
		txtLRC2.setShadowLayer(2, 2, 2, Color.BLACK);
		txtLRC2.setSingleLine(true);
		txtLRC2.setMarqueeRepeatLimit(1);
		txtLRC2.setEllipsize(TruncateAt.MARQUEE);

		// ��View����װ����
		layMain.addView(txtLRC1);
		layMain.addView(txtLRC2);
		addView(imgIcon);
		addView(layMain);
	}

	/* ���ø������ */
	public void SetLRC(int IconResource, String Sentense1, int Color1, String Sentense2, int Color2)
	{
		imgIcon.setImageResource(IconResource);
		txtLRC1.setText(Sentense1);
		txtLRC1.setTextColor(Color1);
		txtLRC2.setText(Sentense2);
		txtLRC2.setTextColor(Color2);
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

	/* �Զ��Լ���λ�� */
	private void MoveView()
	{
		layWM.x = (int) (x - DownPosX);
		layWM.y = (int) (y - DownPosY);
		wm.updateViewLayout(this, layWM);
	}

	public float getDownPosX()
	{
		return DownPosX;
	}

	public void setDownPosX(float downPosX)
	{
		DownPosX = downPosX;
	}

	public float getDownPosY()
	{
		return DownPosY;
	}

	public void setDownPosY(float downPosY)
	{
		DownPosY = downPosY;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public WindowManager getWm()
	{
		return wm;
	}

	public void setWm(WindowManager wm)
	{
		this.wm = wm;
	}

	public WindowManager.LayoutParams getLayWM()
	{
		return layWM;
	}

	public void setLayWM(WindowManager.LayoutParams layWM)
	{
		this.layWM = layWM;
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}

	public ImageView getImgIcon()
	{
		return imgIcon;
	}

	public void setImgIcon(ImageView imgIcon)
	{
		this.imgIcon = imgIcon;
	}

	public LinearLayout getLayMain()
	{
		return layMain;
	}

	public void setLayMain(LinearLayout layMain)
	{
		this.layMain = layMain;
	}

	public TextView getTxtLRC1()
	{
		return txtLRC1;
	}

	public void setTxtLRC1(TextView txtLRC1)
	{
		this.txtLRC1 = txtLRC1;
	}

	public TextView getTxtLRC2()
	{
		return txtLRC2;
	}

	public void setTxtLRC2(TextView txtLRC2)
	{
		this.txtLRC2 = txtLRC2;
	}
}