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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class LRCService
{
	private String strLRCPath = ""; // 歌词路径
	private String strLRC = ""; // 歌词文本
	private boolean IsLyricExist = false; // 指示歌词是否存在
	private Map<Long, String> map = new HashMap<Long, String>();
	private List<Long> lstTimeStamp = new ArrayList<Long>();
	private srcMain main = null;
	private int LastIndex = 0; // 上一次歌词的index
	private boolean CanRefreshLRC = true; // 判断能否更新歌词
	private String strCurrLRCSentence = ""; // 当前正在播放的这句歌词

	public LRCService(srcMain main)
	{
		setMain(main);
	}

	/* 刷新歌词 */
	public void RefreshLRC()
	{
		if (IsLyricExist)
		{
			if (main.getTxtLRC().getText() == "") // 如果没有歌词则添加
				main.getHs().getHdlLoadLRC().sendEmptyMessage(0);

			// 获取当前歌词的index和时间值
			int index = 0;
			Long CurrTime = null;
			for (int i = 0; i < lstTimeStamp.size(); i++)
			{
				if (lstTimeStamp != null && main.getMs().GetCurrTime() > lstTimeStamp.get(i)) // 找当前语句的序号
				{
					index = i;
					CurrTime = lstTimeStamp.get(i);
				}
				else
					break;
			}

			if (CurrTime == null)
				return;

			int LineCount = 0; // 字符串所占行数

			if (LastIndex != index || (index == 0 && LastIndex == 0))
			{// 如果本次和上一次处在一个index下，则表示重复处理，所以忽略（歌曲开头0除外）
				String strLRCTemp = ""; // 从头到当前歌词

				// 获取当前时间点前的所有歌词
				for (int i = 0; i < lstTimeStamp.size(); i++)
				{
					if (lstTimeStamp.get(i) <= CurrTime)
					{
						LineCount += GetSentenceLines(map.get(lstTimeStamp.get(i))) - 1;
						strLRCTemp += map.get(lstTimeStamp.get(i)) + "\n";
					}
					else
						break;
				}

				SpannableStringBuilder ssb = new SpannableStringBuilder(strLRC);
				ssb.setSpan(new ForegroundColorSpan(Color.parseColor(main.getSp().getString("btnLRCHighlightlFontColor", "#FFFF00"))), strLRCTemp.length() - map.get(CurrTime).length() - 1, strLRCTemp
						.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 前语句高亮

				// 发送消息更新界面
				Message msg = new Message();

				int ClearlyLineNumber = GetSentenceLines(map.get(CurrTime));
				if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
					msg.what = -main.getTxtLRC().getLineHeight() * (index + ClearlyLineNumber - 1 + LineCount) + 80; // 横屏偏移80dip
				else
					msg.what = -main.getTxtLRC().getLineHeight() * (index + ClearlyLineNumber - 1 + LineCount) + 200; // 竖屏偏移200dip

				strCurrLRCSentence = map.get(CurrTime); // 获取当前这句歌词内容，供Widget使用

				msg.obj = ssb;
				msg.arg2 = main.getMs().getCurrIndex();
				main.getHs().getHdlLRCSync().sendMessage(msg);

				LastIndex = index; // 记录本句歌词的序号
			}
		}
	}

	/* 获取一行字符串自动换行后的行数 */
	public int GetSentenceLines(String Sentence)
	{
		// 获取字符串宽度
		Paint FontPaint = new Paint();
		FontPaint.setTextSize(main.getTxtLRC().getTextSize());
		float FontWidth = FontPaint.measureText(Sentence);

		// 计算行数
		int txtLRCWidth = main.getTxtLRC().getWidth() - 10;
		int ClearlyLineNumber = (int) Math.floor(FontWidth / txtLRCWidth);
		float RemainLine = FontWidth % txtLRCWidth;

		if (ClearlyLineNumber == 0 && RemainLine == 0) // 空行也算一行
			ClearlyLineNumber = 1;

		if (RemainLine != 0) // 有余数则需要额外的一行
			ClearlyLineNumber += 1;

		return ClearlyLineNumber;
	}

	/* 读取 LRC 文件 */
	private void ReadLRC()
	{
		try
		{
			File f = new File(strLRCPath); // 指定文件
			if (!f.exists())
			{
				IsLyricExist = false;
				strLRC = main.getResources().getString(R.string.lrcservice_no_lyric_found);
			}
			else
			{
				IsLyricExist = true;
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				BufferedReader br = new BufferedReader(new InputStreamReader(is, GetCharset(f)));
				String strTemp = "";
				while ((strTemp = br.readLine()) != null)
				{// 如果文件没有读完则继续
					strTemp = AnalyzeLRC(strTemp); // 逐行分析 LRC
				}
				br.close();
				is.close();

				Collections.sort(lstTimeStamp);
				strLRC = Map2String();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/* 逐行解析 LRC 文本 */
	private String AnalyzeLRC(String LRCText)
	{
		try
		{
			int pos1 = LRCText.indexOf("["); // 第一个标记开始符
			int pos2 = LRCText.indexOf("]"); // 第一个标记结束符

			// 分析当前行是否正确（第一位应为标记开头“[”）
			if (pos1 == 0 && pos2 != -1)
			{// 正确
				Long time[] = new Long[GetPossiblyTagCount(LRCText)];
				time[0] = TimeToLong(LRCText.substring(pos1 + 1, pos2)); // 记录第一次的时间
				if (time[0] == -1) // 不正确的时间
					return ""; // LRCText

				String strLineRemaining = LRCText;
				int i = 1;
				while (pos1 == 0 && pos2 != -1)
				{
					strLineRemaining = strLineRemaining.substring(pos2 + 1); // 剩下的内容
					pos1 = strLineRemaining.indexOf("[");
					pos2 = strLineRemaining.indexOf("]");
					if (pos2 != -1)
					{
						time[i] = TimeToLong(strLineRemaining.substring(pos1 + 1, pos2));
						if (time[i] == -1) // 不正确的时间
							return ""; // LRCText
						i++;
					}
				}

				for (int j = 0; j < time.length; j++)
				{
					if (time[j] != null)
					{
						map.put(time[j], strLineRemaining);
						lstTimeStamp.add(time[j]);
					}
				}

				return strLineRemaining;
			}
			else
				return "";
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/* 将Map中的歌词转换为文本 */
	private String Map2String()
	{
		List<Map.Entry<Long, String>> lstMap = new ArrayList<Map.Entry<Long, String>>(map.entrySet());

		Collections.sort(lstMap, new Comparator<Map.Entry<Long, String>>()
		{
			public int compare(Map.Entry<Long, String> o1, Map.Entry<Long, String> o2)
			{
				String strO1 = String.format("%1$08d", o1.getKey());
				String strO2 = String.format("%1$08d", o2.getKey());

				return strO1.compareTo(strO2);
			}
		});

		String strRet = "";

		for (int i = 0; i < lstMap.size(); i++)
		{
			String strTemp = lstMap.get(i).toString();
			strRet += strTemp.substring(strTemp.indexOf("=") + 1) + "\n";
		}

		return strRet;
	}

	/* 将时间转换为毫秒数 */
	public long TimeToLong(String Time)
	{
		try
		{
			String[] s1 = Time.split(":");
			int min = Integer.parseInt(s1[0]);
			String[] s2 = s1[1].split("\\.");
			int sec = Integer.parseInt(s2[0]);
			int mill = 0;
			if (s2.length > 1)
				mill = Integer.parseInt(s2[1]);

			return min * 60 * 1000 + sec * 1000 + mill * 10;
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	/* 将毫秒数转换为时间 */
	public String IntegerToTime(int Time)
	{
		try
		{
			int min = (int) Math.floor(Time / (60 * 1000));
			int sec = (int) (((int) (Time % (60 * 1000))) / 1000);

			String strMin = String.format("%1$02d", min);
			String strSec = String.format("%1$02d", sec);

			return strMin + ":" + strSec;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/* 通过“[”和“]”判断可能的标签总数 */
	private int GetPossiblyTagCount(String Line)
	{
		String strCount1[] = Line.split("\\[");
		String strCount2[] = Line.split("\\]");

		if (strCount1.length == 0 && strCount2.length == 0)
			return 1;
		else if (strCount1.length > strCount2.length)
			return strCount1.length;
		else
			return strCount2.length;
	}

	/* 获取指定文件的字符集 */
	public String GetCharset(File file)
	{
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try
		{
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE)
			{
				charset = "UTF-16LE";
				checked = true;
			}
			else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF)
			{
				charset = "UTF-16BE";
				checked = true;
			}
			else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF)
			{
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked)
			{
				int loc = 0;
				while ((read = bis.read()) != -1)
				{
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
						break;
					if (0xC0 <= read && read <= 0xDF)
					{
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) // 双字节(0xC0-0xDF),(0x80-xBF)也可能在GB编码内
							continue;
						else
							break;
					}
					else if (0xE0 <= read && read <= 0xEF)
					{// 也有可能出错，但是几率较小
						read = bis.read();
						if (0x80 <= read && read <= 0xBF)
						{
							read = bis.read();
							if (0x80 <= read && read <= 0xBF)
							{
								charset = "UTF-8";
								break;
							}
							else
								break;
						}
						else
							break;
					}
				}
			}
			bis.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return charset;
	}

	public String getStrLRCPath()
	{
		return strLRCPath;
	}

	public void setStrLRCPath(String strLRCPath)
	{
		this.strLRCPath = strLRCPath;

		// 还原txtLRC布局参数
		AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // 获取scrLRC尺寸参数
		layLRC.y = 0;
		layLRC.height = LayoutParams.WRAP_CONTENT;
		main.getTxtLRC().setLayoutParams(layLRC);
		main.getTxtLRC().setText(main.getResources().getString(R.string.lrcservice_loading_lrc));

		/* 加载歌词线程 */
		new Thread()
		{
			public void run()
			{
				map.clear();
				lstTimeStamp.clear();

				ReadLRC();
				LastIndex = 0; // 还原歌词滚动参数

				main.getHs().getHdlLoadLRC().sendEmptyMessage(0);
			};
		}.start();
	}

	public String getStrLRC()
	{
		return strLRC;
	}

	public void setStrLRC(String strLRC)
	{
		this.strLRC = strLRC;
	}

	public Map<Long, String> getMap()
	{
		return map;
	}

	public void setMap(Map<Long, String> map)
	{
		this.map = map;
	}

	public List<Long> getLstTimeStamp()
	{
		return lstTimeStamp;
	}

	public void setLstTimeStamp(List<Long> lstTimeStamp)
	{
		this.lstTimeStamp = lstTimeStamp;
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}

	public int getLastIndex()
	{
		return LastIndex;
	}

	public void setLastIndex(int lastIndex)
	{
		LastIndex = lastIndex;
	}

	public boolean isCanRefreshLRC()
	{
		return CanRefreshLRC;
	}

	public void setCanRefreshLRC(boolean canRefreshLRC)
	{
		CanRefreshLRC = canRefreshLRC;
	}

	public boolean isIsLyricExist()
	{
		return IsLyricExist;
	}

	public void setIsLyricExist(boolean isLyricExist)
	{
		IsLyricExist = isLyricExist;
	}

	public String getStrCurrLRCSentence()
	{
		return strCurrLRCSentence;
	}

	public void setStrCurrLRCSentence(String strCurrLRCSentence)
	{
		this.strCurrLRCSentence = strCurrLRCSentence;
	}
}