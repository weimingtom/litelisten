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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MenuDialog
{
	private static int ScreenOrientation;
	private static PopupWindow pw;
	private static View view;
	private static scrMain main;
	private static LinearLayout layMenu;
	private static Button btnSwitcher;
	private static Button btnAdvanced;
	private static Button btnSettings;
	private static Button btnHelp;
	private static GridView grdSwitcher;
	private static GridView grdAdvanced;
	private static GridView grdSettings;
	private static GridView grdHelp;

	/* 获取控件实例 */
	public static void GetInstance()
	{
		layMenu = (LinearLayout) view.findViewById(R.id.layMenu);
		btnSwitcher = (Button) view.findViewById(R.id.btnSwitcher);
		btnAdvanced = (Button) view.findViewById(R.id.btnAdvanced);
		btnSettings = (Button) view.findViewById(R.id.btnSettings);
		btnHelp = (Button) view.findViewById(R.id.btnHelp);
		grdSwitcher = (GridView) view.findViewById(R.id.grdSwitcher);
		grdAdvanced = (GridView) view.findViewById(R.id.grdAdvanced);
		grdSettings = (GridView) view.findViewById(R.id.grdSettings);
		grdHelp = (GridView) view.findViewById(R.id.grdHelp);
	}

	/* 开关菜单 */
	public static void SetSwitcherMenu()
	{
		List<Map<String, Object>> lstMenuItem = new ArrayList<Map<String, Object>>(); // 菜单功能列表
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ItemText", main.getString(R.string.scrmain_play_mode));
		if (main.getSt().getPlayMode().equals("0"))
			map.put("ItemIcon", R.drawable.menu_play_mode_close);
		else if (main.getSt().getPlayMode().equals("1"))
			map.put("ItemIcon", R.drawable.menu_play_mode_repeat_all);
		else if (main.getSt().getPlayMode().equals("2"))
			map.put("ItemIcon", R.drawable.menu_play_mode_pause_current);
		else if (main.getSt().getPlayMode().equals("3"))
			map.put("ItemIcon", R.drawable.menu_play_mode_repeat_current);
		else if (main.getSt().getPlayMode().equals("4"))
			map.put("ItemIcon", R.drawable.menu_play_mode_shuffle);
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_show_lrc);
		map.put("ItemText", main.getString(R.string.scrmain_extend_menu_show_lyric));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_desk_lrc);
		if (main.getSt().getDeskLRCStatus())
		{
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_desk_lrc_hide));
			main.getFl().setVisibility(View.VISIBLE);
		}
		else
		{
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_desk_lrc_show));
			main.getFl().setVisibility(View.INVISIBLE);
			main.getNm().cancel(scrMain.getLrcNotifyId());
		}
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_keep_screen_on);
		if (main.getSt().getKeepScreenOn())
		{
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_keep_screen_on_false));
			main.getLayActivity().setKeepScreenOn(true);
			main.getFl().setKeepScreenOn(true);
		}
		else
		{
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_keep_screen_on_true));
			main.getLayActivity().setKeepScreenOn(false);
			main.getFl().setKeepScreenOn(false);
		}
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_volume);
		map.put("ItemText", main.getString(R.string.scrmain_extend_menu_volume));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		if (main.getSt().getOrderBy().equals("asc"))
		{
			map.put("ItemIcon", R.drawable.menu_order_desc);
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_order_desc));
		}
		else
		{
			map.put("ItemIcon", R.drawable.menu_order_asc);
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_order_asc));
		}
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_exit);
		map.put("ItemText", main.getString(R.string.scrmain_extend_menu_exit));
		lstMenuItem.add(map);

		SimpleAdapter adapter = new SimpleAdapter(main, lstMenuItem, R.layout.grid_menu, new String[] { "ItemIcon", "ItemText" }, new int[] { R.id.imgMenu, R.id.txtMenu });
		grdSwitcher.setAdapter(adapter);
	}

	/* 扩展菜单 */
	public static void SetAdvancedMenu()
	{
		List<Map<String, Object>> lstMenuItem = new ArrayList<Map<String, Object>>(); // 菜单功能列表
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_search);
		map.put("ItemText", main.getString(R.string.scrmain_extend_menu_search));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_ringtone);
		map.put("ItemText", main.getString(R.string.scrmain_extend_menu_ringtone));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		if (main.isIsShowingFavourite())
		{// 最爱
			map.put("ItemIcon", R.drawable.menu_list);
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_list));
		}
		else
		{// 列表
			map.put("ItemIcon", R.drawable.menu_favourite);
			map.put("ItemText", main.getString(R.string.scrmain_extend_menu_favourite));
		}
		lstMenuItem.add(map);

		SimpleAdapter adapter = new SimpleAdapter(main, lstMenuItem, R.layout.grid_menu, new String[] { "ItemIcon", "ItemText" }, new int[] { R.id.imgMenu, R.id.txtMenu });
		grdAdvanced.setAdapter(adapter);
	}

	/* 设置菜单 */
	public static void SetSettingsMenu()
	{
		List<Map<String, Object>> lstMenuItem = new ArrayList<Map<String, Object>>(); // 菜单功能列表
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_settings);
		map.put("ItemText", main.getString(R.string.scrmain_extend_menu_settings));
		lstMenuItem.add(map);

		SimpleAdapter adapter = new SimpleAdapter(main, lstMenuItem, R.layout.grid_menu, new String[] { "ItemIcon", "ItemText" }, new int[] { R.id.imgMenu, R.id.txtMenu });
		grdSettings.setAdapter(adapter);
	}

	/* 帮助菜单 */
	public static void SetHelpMenu()
	{
		List<Map<String, Object>> lstMenuItem = new ArrayList<Map<String, Object>>(); // 菜单功能列表
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_feedback);
		map.put("ItemText", main.getString(R.string.scrmain_extend_menu_feedback));
		lstMenuItem.add(map);

		SimpleAdapter adapter = new SimpleAdapter(main, lstMenuItem, R.layout.grid_menu, new String[] { "ItemIcon", "ItemText" }, new int[] { R.id.imgMenu, R.id.txtMenu });
		grdHelp.setAdapter(adapter);
	}

	/* 设置按钮事件 */
	public static void SetButtonEvent()
	{
		layMenu.setOnKeyListener(new OnKeyListener()
		{
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU)
					pw.dismiss();

				return false;
			}
		});

		btnSwitcher.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				grdSwitcher.setVisibility(View.VISIBLE);
				grdAdvanced.setVisibility(View.GONE);
				grdSettings.setVisibility(View.GONE);
				grdHelp.setVisibility(View.GONE);

				if (ScreenOrientation == 1 || ScreenOrientation == 3)
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_land_top_highlight);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_land_middle_normal_1);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_land_middle_normal_2);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_land_bottom_normal);
				}
				else
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_port_left_highlight);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_port_middle_normal);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_port_middle_normal);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_port_right_normal);
				}
			}
		});

		btnAdvanced.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				grdSwitcher.setVisibility(View.GONE);
				grdAdvanced.setVisibility(View.VISIBLE);
				grdSettings.setVisibility(View.GONE);
				grdHelp.setVisibility(View.GONE);

				if (ScreenOrientation == 1 || ScreenOrientation == 3)
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_land_top_normal);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_land_middle_highlight_1);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_land_middle_normal_2);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_land_bottom_normal);
				}
				else
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_port_left_normal);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_port_middle_highlight);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_port_middle_normal);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_port_right_normal);
				}
			}
		});

		btnSettings.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				grdSwitcher.setVisibility(View.GONE);
				grdAdvanced.setVisibility(View.GONE);
				grdSettings.setVisibility(View.VISIBLE);
				grdHelp.setVisibility(View.GONE);

				if (ScreenOrientation == 1 || ScreenOrientation == 3)
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_land_top_normal);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_land_middle_normal_1);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_land_middle_highlight_2);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_land_bottom_normal);
				}
				else
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_port_left_normal);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_port_middle_normal);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_port_middle_highlight);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_port_right_normal);
				}
			}
		});

		btnHelp.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				grdSwitcher.setVisibility(View.GONE);
				grdAdvanced.setVisibility(View.GONE);
				grdSettings.setVisibility(View.GONE);
				grdHelp.setVisibility(View.VISIBLE);

				if (ScreenOrientation == 1 || ScreenOrientation == 3)
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_land_top_normal);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_land_middle_normal_1);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_land_middle_normal_2);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_land_bottom_highlight);
				}
				else
				{
					btnSwitcher.setBackgroundResource(R.drawable.btn_control_panel_port_left_normal);
					btnAdvanced.setBackgroundResource(R.drawable.btn_control_panel_port_middle_normal);
					btnSettings.setBackgroundResource(R.drawable.btn_control_panel_port_middle_normal);
					btnHelp.setBackgroundResource(R.drawable.btn_control_panel_port_right_highlight);
				}
			}
		});

		/* 开关菜单 */
		grdSwitcher.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				boolean CouldHide = true;

				switch (arg2)
				{
					case 0:
						CouldHide = false;
						ImageView imgPlayMode = (ImageView) arg1.findViewById(R.id.imgMenu);
						String index = main.getSt().getPlayMode(); // 0－顺序播放；1－全部循环；2－单曲暂停；3－单曲循环；4－随机播放
						Editor edtPlayMode = main.getSp().edit();

						if (index.equals("0"))
						{
							edtPlayMode.putString("PlayMode", "1");
							main.getSt().setPlayMode("1");
							imgPlayMode.setImageResource(R.drawable.menu_play_mode_repeat_all);

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.scrmain_play_mode_repeat_all);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.scrmain_play_mode_repeat_all, Toast.LENGTH_SHORT));
						}
						else if (index.equals("1"))
						{
							edtPlayMode.putString("PlayMode", "2");
							main.getSt().setPlayMode("2");
							imgPlayMode.setImageResource(R.drawable.menu_play_mode_pause_current);

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.scrmain_play_mode_pause_current);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.scrmain_play_mode_pause_current, Toast.LENGTH_SHORT));
						}
						else if (index.equals("2"))
						{
							edtPlayMode.putString("PlayMode", "3");
							main.getSt().setPlayMode("3");
							imgPlayMode.setImageResource(R.drawable.menu_play_mode_repeat_current);

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.scrmain_play_mode_repeat_current);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.scrmain_play_mode_repeat_current, Toast.LENGTH_SHORT));
						}
						else if (index.equals("3"))
						{
							edtPlayMode.putString("PlayMode", "4");
							main.getSt().setPlayMode("4");
							imgPlayMode.setImageResource(R.drawable.menu_play_mode_shuffle);

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.scrmain_play_mode_shuffle);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.scrmain_play_mode_shuffle, Toast.LENGTH_SHORT));
						}
						else if (index.equals("4"))
						{
							edtPlayMode.putString("PlayMode", "0");
							main.getSt().setPlayMode("0");
							imgPlayMode.setImageResource(R.drawable.menu_play_mode_close);

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.scrmain_play_mode_close);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.scrmain_play_mode_close, Toast.LENGTH_SHORT));
						}

						edtPlayMode.commit();
						main.getToast().show();

						break;
					case 1:
						main.List2LRCSwitcher();
						break;
					case 2:
						TextView txtDeskLyric = (TextView) arg1.findViewById(R.id.txtMenu);
						Editor edtDeskLRCStatus = main.getSp().edit();
						if (main.getSt().getDeskLRCStatus())
						{
							txtDeskLyric.setText(R.string.scrmain_extend_menu_desk_lrc_show);
							edtDeskLRCStatus.putBoolean("DeskLRCStatus", false);
							main.getSt().setDeskLRCStatus(false);
							main.getNm().cancel(scrMain.getLrcNotifyId());

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.float_lrc_deactivation);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.float_lrc_deactivation, Toast.LENGTH_SHORT));
						}
						else
						{
							txtDeskLyric.setText(R.string.scrmain_extend_menu_desk_lrc_hide);
							edtDeskLRCStatus.putBoolean("DeskLRCStatus", true);
							main.getSt().setDeskLRCStatus(true);
							main.CallFloatLRCNotify(main.getSt().getFloatLRCLocked());

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.float_lrc_activiation);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.float_lrc_activiation, Toast.LENGTH_SHORT));
						}
						edtDeskLRCStatus.commit();
						main.getToast().show();

						break;
					case 3:
						TextView txtScrOn = (TextView) arg1.findViewById(R.id.txtMenu);
						Editor edtKeepScreenOn = main.getSp().edit();
						if (!main.getSt().getKeepScreenOn())
						{
							main.getLayActivity().setKeepScreenOn(true);
							txtScrOn.setText(R.string.scrmain_extend_menu_keep_screen_on_false);
							edtKeepScreenOn.putBoolean("KeepScreenOn", true);
							main.getSt().setKeepScreenOn(true);

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.scrmain_extend_menu_keep_screen_on_true);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.scrmain_extend_menu_keep_screen_on_true, Toast.LENGTH_SHORT));
						}
						else
						{
							main.getLayActivity().setKeepScreenOn(false);
							txtScrOn.setText(R.string.scrmain_extend_menu_keep_screen_on_true);
							edtKeepScreenOn.putBoolean("KeepScreenOn", false);
							main.getSt().setKeepScreenOn(false);

							if (main.getToast() != null)
							{
								main.getToast().setText(R.string.scrmain_extend_menu_keep_screen_on_false);
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, R.string.scrmain_extend_menu_keep_screen_on_false, Toast.LENGTH_SHORT));
						}
						edtKeepScreenOn.commit();
						main.getToast().show();

						break;
					case 4:
						main.getVd().ShowDialog(main, main.getSt().getLanguage(), main.getSt().getUseAnimation(), main.getLayActivity());
						main.getVd().setCountDown(5); // 显示长度5秒的音量条

						break;
					case 5:
						TextView txtOrder = (TextView) arg1.findViewById(R.id.txtMenu);
						ImageView imgMenu = (ImageView) arg1.findViewById(R.id.imgMenu);
						Editor edtOrderBy = main.getSp().edit();
						if (main.getSt().getOrderBy().equals("asc"))
						{
							txtOrder.setText(R.string.scrmain_extend_menu_order_asc);
							imgMenu.setImageResource(R.drawable.menu_order_asc);
							edtOrderBy.putString("OrderBy", "desc");
							main.getSt().setOrderBy("desc");
						}
						else
						{
							txtOrder.setText(R.string.scrmain_extend_menu_order_desc);
							imgMenu.setImageResource(R.drawable.menu_order_desc);
							edtOrderBy.putString("OrderBy", "asc");
							main.getSt().setOrderBy("asc");
						}

						edtOrderBy.commit();
						main.SetMusicListByDB();

						break;
					case 6:
						Editor edtLastKeyword = main.getSp().edit();
						edtLastKeyword.putString("LastKeyword", "");
						edtLastKeyword.putBoolean("Started", false); // 是否启动标志，给Widget判断
						main.getSt().setLastKeyword("");
						main.getSt().setStarted(false);
						edtLastKeyword.commit();
						main.getNm().cancelAll();
						System.exit(0);

						break;
				}

				if (CouldHide)
					pw.dismiss();
			}
		});

		/* 扩展菜单 */
		grdAdvanced.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				switch (arg2)
				{
					case 0:
						main.SearchBoxSwitcher();
						break;
					case 1:
						Map<String, Object> map = main.getLstSong().get(main.getMs().getCurrIndex());
						String strMusicPath = (String) map.get("MusicPath");

						if (main.SetAsRingtone(strMusicPath))
						{
							if (main.getToast() != null)
							{
								main.getToast().setText(main.getString(R.string.scrmain_ringtone_successful));
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, main.getString(R.string.scrmain_ringtone_successful), Toast.LENGTH_SHORT));
						}
						else
						{
							if (main.getToast() != null)
							{
								main.getToast().setText(main.getString(R.string.scrmain_ringtone_set_refused));
								main.getToast().setDuration(Toast.LENGTH_SHORT);
							}
							else
								main.setToast(Toast.makeText(main, main.getString(R.string.scrmain_ringtone_set_refused), Toast.LENGTH_SHORT));
						}
						main.getToast().show();

						break;
					case 2:
						TextView txtFavourite = (TextView) arg1.findViewById(R.id.txtMenu);
						ImageView imgFavourite = (ImageView) arg1.findViewById(R.id.imgMenu);

						if (main.isIsShowingFavourite())
						{// 最爱-->列表
							txtFavourite.setText(R.string.scrmain_extend_menu_favourite);
							imgFavourite.setImageResource(R.drawable.menu_favourite);

							main.setIsShowingFavourite(false);
						}
						else
						{// 列表-->最爱
							// 清空关键词
							Editor edt = main.getSp().edit();
							edt.putString("LastKeyword", "");
							edt.commit();
							main.getTxtKeyword().setText("");

							txtFavourite.setText(R.string.scrmain_extend_menu_list);
							imgFavourite.setImageResource(R.drawable.menu_list);

							main.setIsShowingFavourite(true);
						}

						main.SetMusicListByDB();

						break;
				}

				pw.dismiss();
			}
		});

		/* 设置菜单 */
		grdSettings.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				switch (arg2)
				{
					case 0:
						main.startActivity(new Intent(main, scrSettings.class));
						break;
				}

				pw.dismiss();
			}
		});

		/* 帮助菜单 */
		grdHelp.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				switch (arg2)
				{
					case 0:
						TextDialog.ShowMessage(main, main.getSt().getLanguage(), main.getSt().getUseAnimation(), main.getLayActivity(), R.string.scrmain_extend_menu_feedback,
								R.string.scrmain_feedback_hint, 15, "", 18, new OnClickListener()
								{
									public void onClick(View v)
									{
										String strMessage = TextDialog.getEdtMessage().getText().toString().trim();

										if (strMessage != null && !strMessage.equals(""))
										{
											// 获取手机串号等信息并发送
											TelephonyManager tm = (TelephonyManager) main.getSystemService(Context.TELEPHONY_SERVICE);

											// 获取当前时间
											java.util.Date date = new java.util.Date();
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
											String strDateTime = sdf.format(date);

											String strURL = "http://www.littledai.com/LiteListen/SendTicket.php?imei={imei}&locale={locale}&sdk={sdk}&release={release}&model={model}&message={message}&submit_time={submit_time}";
											strURL = strURL.replace("{imei}", java.net.URLEncoder.encode(tm.getDeviceId())).replace("{locale}",
													java.net.URLEncoder.encode(main.getResources().getConfiguration().locale.toString())).replace("{sdk}",
													java.net.URLEncoder.encode(Build.VERSION.SDK)).replace("{release}", java.net.URLEncoder.encode(Build.VERSION.RELEASE)).replace("{model}",
													java.net.URLEncoder.encode(Build.MODEL)).replace("{message}", java.net.URLEncoder.encode(strMessage)).replace("{submit_time}",
													java.net.URLEncoder.encode(strDateTime)); // 将变量转换成URL格式

											if (main.getToast() != null)
											{
												main.getToast().setText(main.getString(R.string.scrmain_feedback_successful));
												main.getToast().setDuration(Toast.LENGTH_SHORT);
											}
											else
												main.setToast(Toast.makeText(main, main.getString(R.string.scrmain_feedback_successful), Toast.LENGTH_SHORT));

											String strHint = main.getString(R.string.scrmain_feedback_successful);

											if (Common.CallURLPost(strURL, 10000))
												TextDialog.getPw().dismiss(); // 成功后关闭对话框
											else
												strHint = main.getString(R.string.scrmain_feedback_failure);

											if (main.getToast() != null)
											{
												main.getToast().setText(strHint);
												main.getToast().setDuration(Toast.LENGTH_SHORT);
											}
											else
												main.setToast(Toast.makeText(main, strHint, Toast.LENGTH_SHORT));
										}
										else
										{
											if (main.getToast() != null)
											{
												main.getToast().setText(main.getString(R.string.scrmain_feedback_blank));
												main.getToast().setDuration(Toast.LENGTH_SHORT);
											}
											else
												main.setToast(Toast.makeText(main, main.getString(R.string.scrmain_feedback_blank), Toast.LENGTH_SHORT));
										}

										main.getToast().show();
									}
								});

						break;
				}

				pw.dismiss();
			}
		});
	}

	public static void ShowDialog(scrMain main)
	{
		if (!main.getSt().getLanguage().equals("3"))
		{
			Configuration config = main.getResources().getConfiguration(); // 获得设置对象

			if (main.getSt().getLanguage().equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
			else if (main.getSt().getLanguage().equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // 繁体中文
			else if (main.getSt().getLanguage().equals("2"))
				config.locale = Locale.US; // 美式英语

			main.getResources().updateConfiguration(config, main.getResources().getDisplayMetrics());
		}

		MenuDialog.main = main;
		LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.popup_main_menu, null, false); // 显示前再次更换布局
		ScreenOrientation = main.getWindowManager().getDefaultDisplay().getOrientation();

		GetInstance();
		SetButtonEvent();
		SetSwitcherMenu();
		SetAdvancedMenu();
		SetSettingsMenu();
		SetHelpMenu();

		pw = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		pw.setBackgroundDrawable(new BitmapDrawable()); // 响应返回键必须的语句

		if (main.getSt().getUseAnimation())
			pw.setAnimationStyle(R.style.DialogAnimation);
		pw.showAtLocation(main.getLayActivity(), Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL, 0, 0); // 显示在底部
	}

	public static int getScreenOrientation()
	{
		return ScreenOrientation;
	}

	public static void setScreenOrientation(int screenOrientation)
	{
		ScreenOrientation = screenOrientation;
	}

	public static PopupWindow getPw()
	{
		return pw;
	}

	public static void setPw(PopupWindow pw)
	{
		MenuDialog.pw = pw;
	}

	public static View getView()
	{
		return view;
	}

	public static void setView(View view)
	{
		MenuDialog.view = view;
	}

	public static scrMain getMain()
	{
		return main;
	}

	public static void setMain(scrMain main)
	{
		MenuDialog.main = main;
	}

	public static LinearLayout getLayMenu()
	{
		return layMenu;
	}

	public static void setLayMenu(LinearLayout layMenu)
	{
		MenuDialog.layMenu = layMenu;
	}

	public static Button getBtnSwitcher()
	{
		return btnSwitcher;
	}

	public static void setBtnSwitcher(Button btnSwitcher)
	{
		MenuDialog.btnSwitcher = btnSwitcher;
	}

	public static Button getBtnAdvanced()
	{
		return btnAdvanced;
	}

	public static void setBtnAdvanced(Button btnAdvanced)
	{
		MenuDialog.btnAdvanced = btnAdvanced;
	}

	public static Button getBtnSettings()
	{
		return btnSettings;
	}

	public static void setBtnSettings(Button btnSettings)
	{
		MenuDialog.btnSettings = btnSettings;
	}

	public static Button getBtnHelp()
	{
		return btnHelp;
	}

	public static void setBtnHelp(Button btnHelp)
	{
		MenuDialog.btnHelp = btnHelp;
	}

	public static GridView getGrdSwitcher()
	{
		return grdSwitcher;
	}

	public static void setGrdSwitcher(GridView grdSwitcher)
	{
		MenuDialog.grdSwitcher = grdSwitcher;
	}

	public static GridView getGrdAdvanced()
	{
		return grdAdvanced;
	}

	public static void setGrdAdvanced(GridView grdAdvanced)
	{
		MenuDialog.grdAdvanced = grdAdvanced;
	}

	public static GridView getGrdSettings()
	{
		return grdSettings;
	}

	public static void setGrdSettings(GridView grdSettings)
	{
		MenuDialog.grdSettings = grdSettings;
	}

	public static GridView getGrdHelp()
	{
		return grdHelp;
	}

	public static void setGrdHelp(GridView grdHelp)
	{
		MenuDialog.grdHelp = grdHelp;
	}
}