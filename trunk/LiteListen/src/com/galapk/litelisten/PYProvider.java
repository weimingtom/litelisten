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

import net.sourceforge.pinyin4j.PinyinHelper;

public class PYProvider
{
	public String GetPYFull(String strChinese)
	{
		if (strChinese == null)
			return "";

		String strRet = ""; // ����ƴ������
		char chrChinese[] = strChinese.toCharArray(); // ���ַ����ֽ�Ϊ����
		for (int i = 0; i < chrChinese.length; i++)
		{
			if (("~!@#$%^&*()_+{}|:\"<>?`-=[];',./").indexOf(chrChinese[i]) == -1)
			{
				if (("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ").indexOf(chrChinese[i]) != -1)
					strRet += chrChinese[i];
				else
				{
					String[] strTemp = PinyinHelper.toHanyuPinyinStringArray(chrChinese[i]);
					if (strTemp != null)
					{
						if (strTemp[0].length() >= 2)
							strTemp[0] = strTemp[0].substring(0, strTemp[0].length() - 1); // �س����һλ��ʾ����������
						strRet += strTemp[0] + " "; // �Կո�ֿ����ƴ��
					}
				}
			}
		}

		return strRet.trim();
	}

	public String GetPYSimple(String strPYFull)
	{
		if (strPYFull == null || strPYFull.equals(""))
			return "";

		String strRet = ""; // ���ؼ�ƴ����
		String strTemp[] = strPYFull.split(" "); // �Կո�ֿ�ƴ����

		for (int i = 0; i < strTemp.length; i++)
		{
			if (!strTemp[i].equals(""))
				strRet += strTemp[i].substring(0, 1); // ȡÿ��ƴ��������ĸ
		}

		return strRet;
	}
}