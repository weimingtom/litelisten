package com.littledai.top5friends;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PYProvider
{
	public static String GetPYFull(String strChinese)
	{
		String strRet="";  //返回拼音代码
		char chrChinese[]=strChinese.toCharArray();  //将字符串分解为数组
		for(int i=0;i<chrChinese.length;i++)
		{
			String[] strTemp=PinyinHelper.toHanyuPinyinStringArray(chrChinese[i]);
			if(strTemp!=null)
			{
				if(strTemp[0].length()>=2)
					strTemp[0]=strTemp[0].substring(0,strTemp[0].length()-1);  //截除最后一位表示音调的数字
				strRet+=strTemp[0]+" ";  //以空格分开多个拼音
			}
		}
		strRet=strRet.trim();
		
		return strRet;
	}
	
	public static String GetPYSimple(String strPYFull)
	{
		String strRet="";  //返回简拼代码
		String strTemp[]=strPYFull.split(" ");  //以空格分开拼音串
		
		for(int i=0;i<strTemp.length;i++)
		{
			strRet+=strTemp[i].substring(0,1);  //取每个拼音的首字母
		}
		
		return strRet;
	}
}