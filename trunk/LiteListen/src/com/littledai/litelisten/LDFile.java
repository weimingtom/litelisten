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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LDFile
{
	private List<String> lstFile = new ArrayList<String>();

	// 搜索一个目录下的所有指定扩展名的文件，可选择是否遍历子文件夹
	public void GetFiles(String Path, String Extension, boolean IsIterative, boolean SkipNomedia)
	{
		File[] files = new File(Path).listFiles();

		if (SkipNomedia)
		{// 检查.nomedia文件
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile() && files[i].getName().indexOf(".nomedia") != -1)
					return;
			}
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
				GetFiles(files[i].getPath(), Extension, IsIterative, SkipNomedia);
		}
	}

	public List<String> getLstFile()
	{
		return lstFile;
	}

	public void setLstFile(List<String> lstFile)
	{
		this.lstFile = lstFile;
	}
}