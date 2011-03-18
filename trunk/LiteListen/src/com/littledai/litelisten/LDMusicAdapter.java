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

import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LDMusicAdapter extends BaseAdapter
{
	private srcMain main;
	private List<Map<String, Object>> lstSong; // 列表

	public LDMusicAdapter(srcMain main, List<Map<String, Object>> lstSong)
	{
		this.main = main;
		this.lstSong = lstSong;
	}

	public int getCount()
	{
		return lstSong.size();
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
		if (position < 0)
			return null;

		if (convertView == null)
			convertView = LayoutInflater.from(main).inflate(R.layout.list_music, null);

		ImageView imgAlbum = (ImageView) convertView.findViewById(R.id.imgAlbum);
		TextView txtSongTitle = (TextView) convertView.findViewById(R.id.txtSongTitle);
		TextView txtSongInfo = (TextView) convertView.findViewById(R.id.txtSongInfo);
		TextView txtArtist = (TextView) convertView.findViewById(R.id.txtArtist);
		TextView txtAlbum = (TextView) convertView.findViewById(R.id.txtAlbum);
		TextView txtMusicPath = (TextView) convertView.findViewById(R.id.txtMusicPath);
		TextView txtLRCPath = (TextView) convertView.findViewById(R.id.txtLRCPath);
		TextView txtComment = (TextView) convertView.findViewById(R.id.txtComment);
		TextView txtYear = (TextView) convertView.findViewById(R.id.txtYear);
		TextView txtGenre = (TextView) convertView.findViewById(R.id.txtGenre);
		TextView txtTrack = (TextView) convertView.findViewById(R.id.txtTrack);

		txtSongTitle.setText((String) lstSong.get(position).get("Title"));
		txtSongInfo.setText((String) lstSong.get(position).get("SongInfo"));
		txtArtist.setText((String) lstSong.get(position).get("Artist"));
		txtAlbum.setText((String) lstSong.get(position).get("Album"));
		txtMusicPath.setText((String) lstSong.get(position).get("MusicPath"));
		txtLRCPath.setText((String) lstSong.get(position).get("LRCPath"));
		txtComment.setText((String) lstSong.get(position).get("Comment"));
		txtYear.setText((String) lstSong.get(position).get("Year"));
		txtGenre.setText((String) lstSong.get(position).get("Genre"));
		txtTrack.setText((String) lstSong.get(position).get("Track"));

		if (position == main.getSelectedItemIndex())
		{
			imgAlbum.setBackgroundResource(R.drawable.album_selected);
			txtSongTitle.setTextColor(Color.parseColor("#FF6600"));
			txtSongTitle.setShadowLayer(1, 1, 1, Color.parseColor("#000000"));
		}
		else
		{
			imgAlbum.setBackgroundResource(R.drawable.album_normal);
			txtSongTitle.setTextColor(Color.parseColor(main.getSp().getString("btnListFontColor", "#FFFFFF")));
			if (main.getSp().getBoolean("chkListFontShadow", true))
				txtSongTitle.setShadowLayer(1, 1, 1, Color.parseColor(main.getSp().getString("btnListFontShadowColor", "#0099FF")));
		}

		notifyDataSetChanged();

		return convertView;
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}

	public List<Map<String, Object>> getLstSong()
	{
		return lstSong;
	}

	public void setLstSong(List<Map<String, Object>> lstSong)
	{
		this.lstSong = lstSong;
	}
}