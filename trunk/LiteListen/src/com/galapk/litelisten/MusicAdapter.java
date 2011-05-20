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

import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter
{
	private scrMain main;
	private List<Map<String, Object>> lstSong; // 列表

	public MusicAdapter(scrMain main, List<Map<String, Object>> lstSong)
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
		if ((position < 0 || lstSong.size() <= 0) && position >= lstSong.size())
			return null;

		if (convertView == null)
			convertView = LayoutInflater.from(main).inflate(R.layout.list_music, null);

		RelativeLayout layMusicList = (RelativeLayout) convertView.findViewById(R.id.layMusicList);
		ImageView imgAlbum = (ImageView) convertView.findViewById(R.id.imgAlbum);
		TextView txtSongTitle = (TextView) convertView.findViewById(R.id.txtSongTitle);
		TextView txtSongInfo = (TextView) convertView.findViewById(R.id.txtSongInfo);
		ImageButton btnListPlay = (ImageButton) convertView.findViewById(R.id.btnListPlay);

		txtSongTitle.setText((String) lstSong.get(position).get("Title"));
		txtSongInfo.setText((String) lstSong.get(position).get("SongInfo"));

		if (main.getMs().getPlayerStatus() == MusicService.STATUS_PLAY && main.getMs().getCurrIndex() == position)
		{
			imgAlbum.setBackgroundResource(R.drawable.album_playing);
			txtSongTitle.setTextColor(Color.parseColor("#FF9900"));
			txtSongTitle.setTextSize(Float.parseFloat(main.getSp().getString("txtListFontSize", "18")));
			if (main.getSp().getBoolean("chkListFontShadow", true))
				txtSongTitle.setShadowLayer(0.5f, 0.5f, 1, Color.parseColor("#000000"));
		}
		else if (main.getMs().getPlayerStatus() == MusicService.STATUS_PAUSE && main.getMs().getCurrIndex() == position)
		{
			imgAlbum.setBackgroundResource(R.drawable.album_paused);
			txtSongTitle.setTextColor(Color.parseColor("#FF9900"));
			txtSongTitle.setTextSize(Float.parseFloat(main.getSp().getString("txtListFontSize", "18")));
			if (main.getSp().getBoolean("chkListFontShadow", true))
				txtSongTitle.setShadowLayer(0.5f, 0.5f, 1, Color.parseColor("#000000"));
		}
		else
		{
			imgAlbum.setBackgroundResource(R.drawable.album_normal);
			txtSongTitle.setTextColor(Color.parseColor(main.getSp().getString("btnListFontColor", "#FFFFFF")));
			txtSongTitle.setTextSize(Float.parseFloat(main.getSp().getString("txtListFontSize", "18")));
			if (main.getSp().getBoolean("chkListFontShadow", true))
				txtSongTitle.setShadowLayer(0.5f, 0.5f, 1, Color.parseColor(main.getSp().getString("btnListFontShadowColor", "#000000")));
		}

		if (main.getSelectedItemIndex() != position)
		{
			layMusicList.setBackgroundResource(R.drawable.bg_list_music_height);
			btnListPlay.setVisibility(View.GONE);
			txtSongInfo.clearAnimation();
		}
		else
		{
			imgAlbum.setBackgroundResource(R.drawable.album_selected);
			btnListPlay.setVisibility(View.VISIBLE);

			// 如果超长则播放动画滚动
			float CurrWidth = Common.GetTextWidth(txtSongInfo.getText().toString(), txtSongInfo.getTextSize());
			if (CurrWidth > main.getDm().widthPixels - 165)
			{
				LinearLayout.LayoutParams laySongInfo = (LinearLayout.LayoutParams) txtSongInfo.getLayoutParams();
				laySongInfo.width = (int) CurrWidth;
				txtSongInfo.setLayoutParams(laySongInfo);

				Animation anim = new TranslateAnimation(0, -(CurrWidth - main.getDm().widthPixels + 165), 0, 0);
				anim.setDuration(2500);
				anim.setStartOffset(1000);
				anim.setRepeatCount(100);
				anim.setRepeatMode(Animation.REVERSE);
				txtSongInfo.startAnimation(anim);
			}

			// 列表项播放按钮
			btnListPlay.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					main.getMs().Play(main.getSelectedItemIndex());
				}
			});

			if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
				layMusicList.setBackgroundResource(R.drawable.bg_land_list_highlight);
			else
				layMusicList.setBackgroundResource(R.drawable.bg_port_list_highlight);
		}

		return convertView;
	}

	public scrMain getMain()
	{
		return main;
	}

	public void setMain(scrMain main)
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