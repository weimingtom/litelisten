package com.galapk.litelisten;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ewrwsaf extends Activity
{
	/** Called when the activity is first created. */
	TextView sss;
	private float MovedDistance = 0; // 手指在歌词控件上移动的距离

	/* 获取一行字符串自动换行后的行数 */
	public int GetSentenceLines(String Sentence, float TextSize, int ShowingAreaWidth)
	{
		// 计算行数
		float FontWidth = GetTextWidth(Sentence, TextSize); // 获取字符串宽度
		int ClearlyLineNumber = (int) Math.floor(FontWidth / ShowingAreaWidth);
		float RemainLine = FontWidth % ShowingAreaWidth;

		if (ClearlyLineNumber == 0 && RemainLine == 0) // 空行也算一行
			ClearlyLineNumber = 1;

		if (RemainLine != 0) // 有余数则需要额外的一行
			ClearlyLineNumber += 1;

		return ClearlyLineNumber;
	}

	public static float GetTextWidth(String Sentence, float Size)
	{
		Paint FontPaint = new Paint();
		FontPaint.setTextSize(Size);
		return FontPaint.measureText(Sentence.trim()) + (int) (Size * 0.1); // 留点余地
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sss = (TextView) findViewById(R.id.sss);

		
		sss.setOnTouchListener(new OnTouchListener()
		{
			boolean startTwoTouchPoint = false;
			double startDistance;
			int startHeight;

//			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getPointerCount() == 2)
				{
					if (startTwoTouchPoint == false)
					{
						startDistance = getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
						startTwoTouchPoint = true;

						String aaa = sss.getText().toString();
						String ssss[] = aaa.split("\n");
						int line = ssss.length;
						for (int i = 0; i < ssss.length; i++)
							line += GetSentenceLines(ssss[i], sss.getTextSize(), 320);
						startHeight = sss.getLineHeight() * line;
					}
					else if (event.getAction() == MotionEvent.ACTION_MOVE)
					{
						double distance = getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
						sss.setTextSize((float) (sss.getTextSize() * distance / startDistance));
						if (sss.getTextSize() > 100.0f)
							sss.setTextSize(100.0f);
						if (sss.getTextSize() < 1.0f)
							sss.setTextSize(1.0f);

						AbsoluteLayout.LayoutParams lll = (AbsoluteLayout.LayoutParams) sss.getLayoutParams();

						String aaa = sss.getText().toString();
						String ssss[] = aaa.split("\n");
						int line = ssss.length;
						for (int i = 0; i < ssss.length; i++)
							line += GetSentenceLines(ssss[i], sss.getTextSize(), 320);
						if (distance > startDistance)
							lll.y -= Math.sqrt((startHeight - sss.getLineHeight() * line) * (startHeight - sss.getLineHeight() * line)) / 4;
						else
							lll.y += Math.sqrt((startHeight - sss.getLineHeight() * line) * (startHeight - sss.getLineHeight() * line)) / 4;

						sss.setLayoutParams(lll);
						sss.setHeight(sss.getLineHeight() * line);

						sss.invalidate();
						startDistance = distance;
						startHeight = sss.getLineHeight() * line;
					}
				}
				else
				{
					startTwoTouchPoint = false;
				}
				return true;
			}
		});
	}

	private double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

}
