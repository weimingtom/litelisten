package com.littledai.top5friends;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PYProvider
{
	public static String GetPYFull(String strChinese)
	{
		String strRet="";  //����ƴ������
		char chrChinese[]=strChinese.toCharArray();  //���ַ����ֽ�Ϊ����
		for(int i=0;i<chrChinese.length;i++)
		{
			String[] strTemp=PinyinHelper.toHanyuPinyinStringArray(chrChinese[i]);
			if(strTemp!=null)
			{
				if(strTemp[0].length()>=2)
					strTemp[0]=strTemp[0].substring(0,strTemp[0].length()-1);  //�س����һλ��ʾ����������
				strRet+=strTemp[0]+" ";  //�Կո�ֿ����ƴ��
			}
		}
		strRet=strRet.trim();
		
		return strRet;
	}
	
	public static String GetPYSimple(String strPYFull)
	{
		String strRet="";  //���ؼ�ƴ����
		String strTemp[]=strPYFull.split(" ");  //�Կո�ֿ�ƴ����
		
		for(int i=0;i<strTemp.length;i++)
		{
			strRet+=strTemp[i].substring(0,1);  //ȡÿ��ƴ��������ĸ
		}
		
		return strRet;
	}
}