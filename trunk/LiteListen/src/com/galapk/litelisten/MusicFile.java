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

import java.io.File;
import java.util.List;

public class MusicFile
{
	// ����һ��Ŀ¼�µ�����ָ����չ�����ļ�����ѡ���Ƿ�������ļ���
	public void GetFiles(List<String> lstFile, String Path, String Extension, boolean IsIterative, boolean SkipNomedia)
	{
		File[] files = new File(Path).listFiles();

		if (SkipNomedia && files != null)
		{
			// ���.nomedia�ļ�
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile() && files[i].getName().indexOf(".nomedia") != -1)
					return;
			}

			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile())
				{
					if (files[i].getPath().substring(files[i].getPath().length() - Extension.length()).equals(Extension))
						lstFile.add(files[i].getPath());

					if (!IsIterative)
						break;
				}
				else if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1)
					GetFiles(lstFile, files[i].getPath(), Extension, IsIterative, SkipNomedia);
			}
		}
	}
}