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

package com.galapk.litelisten;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileAdapterNew extends BaseAdapter
{
	private scrSettings main;
	private List<Map<String, String>> lstFile; // 文件列表

	public FileAdapterNew(scrSettings main, List<Map<String, String>> lstFile)
	{
		this.main = main;
		this.lstFile = lstFile;
	}

	public int getCount()
	{
		return lstFile.size();
	}

	public Object getItem(int arg0)
	{
		return arg0;
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		if ((position < 0 || lstFile.size() <= 0) && position >= lstFile.size())
			return null;

		if (convertView == null)
			convertView = LayoutInflater.from(main).inflate(R.layout.list_file, null);

		LinearLayout layFileList = (LinearLayout) convertView.findViewById(R.id.layFileList);
		ImageView imgFileIcon = (ImageView) convertView.findViewById(R.id.imgFileIcon);
		TextView txtFilePath = (TextView) convertView.findViewById(R.id.txtFilePath);

		Map<String, String> map = new HashMap<String, String>();
		map = lstFile.get(position);
		String strPath = map.get("ShowPath");
		txtFilePath.setText(strPath);

		if (strPath.length() > 4 && strPath.substring(strPath.length() - 4).toLowerCase().equals(".lrc")) // LRC文件
			imgFileIcon.setBackgroundResource(R.drawable.file_list_lyrics);
		else
			imgFileIcon.setBackgroundResource(R.drawable.file_list_directory);

		if (main.getSelectedFileIndex() != position)
			layFileList.setBackgroundResource(R.drawable.bg_list_music_height);
		else
		{
			if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
				layFileList.setBackgroundResource(R.drawable.bg_land_list_highlight);
			else
				layFileList.setBackgroundResource(R.drawable.bg_port_list_highlight);
		}

		return convertView;
	}

	public scrSettings getMain()
	{
		return main;
	}

	public void setMain(scrSettings main)
	{
		this.main = main;
	}

	public List<Map<String, String>> getLstFile()
	{
		return lstFile;
	}

	public void setLstFile(List<Map<String, String>> lstFile)
	{
		this.lstFile = lstFile;
	}
}