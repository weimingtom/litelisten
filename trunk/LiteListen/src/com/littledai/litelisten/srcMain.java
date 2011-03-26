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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v1.ID3V1_1Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressWarnings("deprecation")
public class srcMain extends Activity
{
	private static final int ANIMATION_TIME = 500; // 动画时长
	private static final int SPLASH_TIME = 3000; // 启动画面时长

	private List<Map<String, Object>> lstSong = new ArrayList<Map<String, Object>>(); // 播放列表
	private int ScreenOrantation = 0;// 屏幕方向
	private int CurrentShown = 0; // 0－播放列表；1－歌词信息
	private int SelectedItemIndex = -1; // 选中的歌曲序号
	private boolean IsTouchToSeek = false; // 判断当前是否由用户拖动滑块
	private boolean IsMusicRefreshing = false;
	private boolean IsKeepScreenOn = false; // 当前是否保持屏幕常亮
	private SharedPreferences sp = null;
	private boolean IsSplashThreadAlive = false; // 显示Splash的线程是否存活

	/* 定义控件和自定义类 */
	private ImageButton btnLast;
	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPause;
	private ImageButton btnPlayMode;
	private ImageButton btnLRC;
	private ImageButton btnHighlightPlay;
	private ImageButton btnSearch;
	private TextView txtTitle;
	private TextView txtTime;
	private TextView txtScanHint;
	private TextView txtLRC;
	private TextView txtKeyword;
	private LinearLayout layExtendMenu;
	private LinearLayout layControlPanel;
	private LinearLayout laySearch;
	private LinearLayout layMusicSeek;
	private LinearLayout layHighlight;
	private LinearLayout layMain;
	private LinearLayout laySplash;
	private AbsoluteLayout layBody;
	private ListView lstMusic;
	private GridView grdMenu;
	private SeekBar skbMusic;
	private LRCService ls;
	private MusicService ms;
	private MP3Tags mt;
	private MessageDialog dlg;
	private DBProvider db;
	private PYProvider py;
	private HandlerService hs;
	private MusicAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/* 设置窗口样式，必须按照顺序 */
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 无标题栏
		setContentView(R.layout.scr_main);

		IsSplashThreadAlive = true;
		ScreenOrantation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation();

		ls = new LRCService(this);
		ms = new MusicService(this);
		dlg = new MessageDialog(srcMain.this);
		db = new DBProvider(this);
		hs = new HandlerService(this);
		mt = new MP3Tags(this);
		py = new PYProvider();
		hs = new HandlerService(this);
		sp = getSharedPreferences("com.littledai.litelisten_preferences", 0); // 读取配置文件

		// 清除上次程序运行的历史记录
		Editor edt = sp.edit();
		edt.putString("LastKeyword", "");
		edt.putBoolean("Started", true); // 是否启动标志，给Widget判断
		edt.commit();

		FindViews();
		ListernerBinding();

		/* 设置耳机键盘监听 */
		ControlsReceiver ctrlReceiver = new ControlsReceiver(this);

		IntentFilter ittFilterButton = new IntentFilter(Intent.ACTION_MEDIA_BUTTON); // 控制键
		registerReceiver(ctrlReceiver, ittFilterButton);

		IntentFilter ittFilterPlug = new IntentFilter(Intent.ACTION_HEADSET_PLUG); // 控制键
		registerReceiver(ctrlReceiver, ittFilterPlug);

		IntentFilter ittFilterBluetooth = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED); // 蓝牙断开
		registerReceiver(ctrlReceiver, ittFilterBluetooth);

		new Thread()
		{
			public void run()
			{
				while (true)
				{
					try
					{
						if (sp.getInt("MusicControl", 3) == 0)
						{
							ms.Last();
							Editor edt = sp.edit();
							edt.putInt("MusicControl", 3);
							edt.commit();
						}
						else if (sp.getInt("MusicControl", 3) == 1)
						{
							ms.PlayPause();
							Editor edt = sp.edit();
							edt.putInt("MusicControl", 3);
							edt.commit();
						}
						else if (sp.getInt("MusicControl", 3) == 2)
						{
							ms.Next(false);
							Editor edt = sp.edit();
							edt.putInt("MusicControl", 3);
							edt.commit();
						}

						sleep(1000);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();

		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(SPLASH_TIME);
					hs.getHdlShowMain().sendEmptyMessage(0);
					IsSplashThreadAlive = false;
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	/* 横竖屏切换不执行onCreate() */
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.scr_main);

		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		ScreenOrantation = display.getOrientation();
		dlg.ChangeLayout();

		FindViews();
		ListernerBinding();
		onResume();

		if (!IsSplashThreadAlive)
			laySplash.setVisibility(View.GONE);

		if (IsMusicRefreshing)
			txtScanHint.setVisibility(View.VISIBLE); // 显示提示标签

		txtTitle.setText(ms.getStrShownTitle());

		// 设置播放/暂停按钮
		if (ms.getStrPlayerStatus() == MusicService.STATUS_PLAY)
		{
			btnPlay.setVisibility(View.GONE);
			btnPause.setVisibility(View.VISIBLE);
		}
		else
		{
			btnPlay.setVisibility(View.VISIBLE);
			btnPause.setVisibility(View.GONE);
		}

		// 保持转换前的状态
		AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // 获取scrLRC尺寸参数
		AbsoluteLayout.LayoutParams layMusic = (AbsoluteLayout.LayoutParams) lstMusic.getLayoutParams(); // 获取lstMusic尺寸参数

		if (CurrentShown == 1 && (ScreenOrantation == 1 || ScreenOrantation == 3))
		{// 横屏歌词
			layLRC.x = 0;
			layMusic.x = -552;
			txtLRC.setLayoutParams(layLRC);
			lstMusic.setLayoutParams(layMusic);
		}
		else if (CurrentShown == 1 && ScreenOrantation == 0)
		{// 竖屏歌词
			layLRC.x = 0;
			layMusic.x = -320;
			txtLRC.setLayoutParams(layLRC);
			lstMusic.setLayoutParams(layMusic);
		}
		if (CurrentShown == 0 && (ScreenOrantation == 1 || ScreenOrantation == 3))
		{// 横屏列表
			layMusic.x = 0;
			layLRC.x = 552;
			lstMusic.setLayoutParams(layMusic);
			txtLRC.setLayoutParams(layLRC);
		}
		else if (CurrentShown == 0 && ScreenOrantation == 0)
		{// 竖屏列表
			layMusic.x = 0;
			layLRC.x = 320;
			lstMusic.setLayoutParams(layMusic);
			txtLRC.setLayoutParams(layLRC);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		SetLanguage();
		SetMusicListByDB();
		SetMenuList();
		SetPlayMode();
		SetFonts();
		SetBackground();

		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				hs.getHdlSetStartupLanguage().sendEmptyMessage(0);
			}
		}.start();
	}

	/* 歌曲信息入库 */
	public void SetMusicInfoToDB()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					hs.getHdlShowScanHint().sendEmptyMessage(0);
					db.DBDelete("music_info", "");
					lstSong.clear();
					hs.getHdlAdapterClearHandler().sendEmptyMessage(0);

					MusicFile lf = new MusicFile();
					lf.GetFiles(sp.getString("txtMusicPath", Environment.getExternalStorageDirectory().toString()), ".mp3", sp.getBoolean("chkIncludeSubDirectories", true), sp.getBoolean(
							"chkIngnoreDirectory", true));
					List<String> lstFile = lf.getLstFile();

					if (lstFile.size() > 0)
					{
						for (int i = 0; i < lstFile.size(); i++)
						{
							hs.getHdlUpdateScanHint().sendEmptyMessage(i);

							Map<String, Object> mapInfo = GetMusicID3((String) lstFile.get(i), (String) lstFile.get(i).substring(0, lstFile.get(i).lastIndexOf(".mp3"))); // 获取读到的MP3属性
							mapInfo.put("MusicPath", lstFile.get(i));
							mapInfo.put("LRCPath", lstFile.get(i).substring(0, lstFile.get(i).lastIndexOf(".mp3")) + ".lrc");

							// 更新数据库
							String strTitle = (String) mapInfo.get("Title");
							if (strTitle != null && strTitle.indexOf("'") != -1)
								strTitle = strTitle.replace("'", "''");

							String strArtist = (String) mapInfo.get("Artist");
							if (strArtist != null && strArtist.indexOf("'") != -1)
								strArtist = strArtist.replace("'", "''");

							String strAlbum = (String) mapInfo.get("Album");
							if (strAlbum != null && strAlbum.indexOf("'") != -1)
								strAlbum = strAlbum.replace("'", "''");

							String strYear = (String) mapInfo.get("Year");
							if (strYear != null && strYear.indexOf("'") != -1)
								strYear = strYear.replace("'", "''");

							String strGenre = (String) mapInfo.get("Genre");
							if (strGenre != null && strGenre.indexOf("'") != -1)
								strGenre = strGenre.replace("'", "''");

							String strTrack = (String) mapInfo.get("Track");
							if (strTrack != null && strTrack.indexOf("'") != -1)
								strTrack = strTrack.replace("'", "''");

							String strComment = (String) mapInfo.get("Comment");
							if (strComment != null && strComment.indexOf("'") != -1)
								strComment = strComment.replace("'", "''");

							String strMusicPath = (String) mapInfo.get("MusicPath");
							if (strMusicPath != null && strMusicPath.indexOf("'") != -1)
								strMusicPath = strMusicPath.replace("'", "''");

							String strLRCPath = (String) mapInfo.get("LRCPath");
							if (strLRCPath != null && strLRCPath.indexOf("'") != -1)
								strLRCPath = strLRCPath.replace("'", "''");

							String strSongInfo = (String) mapInfo.get("SongInfo");
							if (strSongInfo != null && strSongInfo.indexOf("'") != -1)
								strSongInfo = strSongInfo.replace("'", "''");

							db.DBInsert("music_info", "'" + strTitle + "','" + strArtist + "','" + strAlbum + "','" + strYear + "','" + strGenre + "','" + strTrack + "','" + strComment + "','"
									+ py.GetPYFull(strTitle) + "','" + py.GetPYSimple(py.GetPYFull(strTitle)) + "','" + py.GetPYFull(strArtist) + "','" + py.GetPYSimple(py.GetPYFull(strArtist))
									+ "','" + strMusicPath + "','" + strLRCPath + "','" + strSongInfo + "','0','0'");

							lstSong.add(mapInfo);
						}

						SetMusicListByDB();
					}

					hs.getHdlHideScanHint().sendEmptyMessage(0);
					IsMusicRefreshing = false;
				}
				catch (Exception e)
				{
					e.printStackTrace();

					hs.getHdlHideScanHint().sendEmptyMessage(0);
					IsMusicRefreshing = false;
				}
			}
		}.start();

		IsMusicRefreshing = true;
	}

	/* 从数据库获取歌曲信息 */
	public void SetMusicListByDB()
	{
		new Thread()
		{
			public void run()
			{
				while (IsMusicRefreshing)
				{// 等待入库线程完成
					try
					{
						sleep(1000);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				IsMusicRefreshing = true;
				lstSong.clear();
				Cursor cur = null;
				String Keyword = sp.getString("LastKeyword", ""); // 上次搜索的关键词

				// 决定排序方式
				String index = sp.getString("lstListOrder", "1");
				if (index.equals("0"))
				{
					cur = db.GetDBInstance(true).query(
							"music_info",
							null,
							"title like '%" + Keyword + "%' or artist like '%" + Keyword + "%' or album like '%" + Keyword + "%' or year like '%" + Keyword + "%' or genre like '%" + Keyword
									+ "%' or comment like '%" + Keyword + "%' or title_py like '%" + Keyword + "%' or title_simple_py like '%" + Keyword + "%' or artist_py like '%" + Keyword
									+ "%' or artist_simple_py like '%" + Keyword + "%' or song_info like '%" + Keyword + "%'", null, null, null, "title_simple_py, artist_simple_py");
				}
				else if (index.equals("1"))
				{
					cur = db.GetDBInstance(true).query(
							"music_info",
							null,
							"title like '%" + Keyword + "%' or artist like '%" + Keyword + "%' or album like '%" + Keyword + "%' or year like '%" + Keyword + "%' or genre like '%" + Keyword
									+ "%' or comment like '%" + Keyword + "%' or title_py like '%" + Keyword + "%' or title_simple_py like '%" + Keyword + "%' or artist_py like '%" + Keyword
									+ "%' or artist_simple_py like '%" + Keyword + "%' or song_info like '%" + Keyword + "%'", null, null, null, "artist_simple_py, title_simple_py");
				}
				else if (index.equals("2"))
				{
					cur = db.GetDBInstance(true).query(
							"music_info",
							null,
							"title like '%" + Keyword + "%' or artist like '%" + Keyword + "%' or album like '%" + Keyword + "%' or year like '%" + Keyword + "%' or genre like '%" + Keyword
									+ "%' or comment like '%" + Keyword + "%' or title_py like '%" + Keyword + "%' or title_simple_py like '%" + Keyword + "%' or artist_py like '%" + Keyword
									+ "%' or artist_simple_py like '%" + Keyword + "%' or song_info like '%" + Keyword + "%'", null, null, null, null);
				}

				while (cur.moveToNext())
				{
					// 更新界面
					Map<String, Object> mapItem = new HashMap<String, Object>();
					mapItem.put("Title", cur.getString(0));
					mapItem.put("SongInfo", cur.getString(13));
					mapItem.put("MusicPath", cur.getString(11));
					mapItem.put("LRCPath", cur.getString(12));
					mapItem.put("Artist", cur.getString(1));
					mapItem.put("Album", cur.getString(2));
					mapItem.put("Genre", cur.getString(4));
					mapItem.put("Year", cur.getString(3));
					mapItem.put("Track", cur.getString(5));

					Message msg = new Message();
					msg.obj = mapItem;
					lstSong.add(mapItem);
				}

				cur.close();
				IsMusicRefreshing = false;

				adapter = new MusicAdapter(srcMain.this, lstSong);
				Message msg = new Message();
				msg.obj = adapter;
				hs.getHdlAdapterUpdateHandler().sendMessage(msg);
			}
		}.start();
	}

	/* 设置专辑图标 */
	public void SetAlbumIcon()
	{
		adapter.getView(ms.getCurrIndex(), null, lstMusic);
		adapter.notifyDataSetChanged();
		lstMusic.setSelectionFromTop(ms.getCurrIndex(), (int) sp.getFloat("LastMusicListY", 0)); // 恢复刚才的位置
	}

	/* 设置播放模式 */
	public void SetPlayMode()
	{
		String index = sp.getString("lstPlayMode", "1"); // 0－顺序播放；1－全部循环；2－单曲暂停；3－单曲循环；4－随机播放

		if (index.equals("0"))
			btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_close);
		else if (index.equals("1"))
			btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_repeat_all);
		else if (index.equals("2"))
			btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_pause_current);
		else if (index.equals("3"))
			btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_repeat_current);
		else if (index.equals("4"))
			btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_shuffle);
	}

	/* 设置程序语言 */
	public void SetLanguage()
	{
		String index = sp.getString("lstLanguage", "3");
		if (!index.equals("3"))
		{
			Configuration config = getResources().getConfiguration(); // 获得设置对象

			if (index.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
			else if (index.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // 繁体中文
			else if (index.equals("2"))
				config.locale = Locale.US; // 美式英语

			getResources().updateConfiguration(config, null);
		}
	}

	/* 设置背景图片 */
	public void SetBackground()
	{
		Bitmap bmpBackground = null;

		if (ScreenOrantation == 1 || ScreenOrantation == 3)
		{
			String index = sp.getString("lstBackgroundLand", "0");
			File f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_land.png");
			if (index.equals("1") && f.isFile() && f.exists())
			{
				bmpBackground = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_land.png");
				layBody.setBackgroundDrawable(ImageEffect.GetDrawable(bmpBackground));
			}
		}
		else
		{
			String index = sp.getString("lstBackgroundPort", "0");
			File f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_port.png");
			if (index.equals("1") && f.isFile() && f.exists())
			{
				bmpBackground = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_port.png");
				layBody.setBackgroundDrawable(ImageEffect.GetDrawable(bmpBackground));
			}
		}
	}

	/* 设置字体 */
	public void SetFonts()
	{
		txtLRC.setTextSize(Float.parseFloat(sp.getString("txtLRCFontSize", "18")));
		txtLRC.setTextColor(Color.parseColor(sp.getString("btnLRCNormalFontColor", "#FFFFFF")));
		if (sp.getBoolean("chkLRCFontShadow", true))
			txtLRC.setShadowLayer(2, 1, 1, Color.parseColor(sp.getString("btnLRCFontShadowColor", "#0099FF")));
		else
			txtLRC.setShadowLayer(2, 1, 1, Color.TRANSPARENT);
	}

	/* 获取控件实体 */
	public void FindViews()
	{
		btnLast = (ImageButton) findViewById(R.id.btnLast);
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPause = (ImageButton) findViewById(R.id.btnPause);
		btnPlayMode = (ImageButton) findViewById(R.id.btnPlayMode);
		btnHighlightPlay = (ImageButton) findViewById(R.id.btnHighlightPlay);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnLRC = (ImageButton) findViewById(R.id.btnLRC);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtLRC = (TextView) findViewById(R.id.txtLRC);
		txtKeyword = (TextView) findViewById(R.id.txtKeyword);
		txtScanHint = (TextView) findViewById(R.id.txtScanHint);
		layExtendMenu = (LinearLayout) findViewById(R.id.layExtendMenu);
		laySplash = (LinearLayout) findViewById(R.id.laySplash);
		layControlPanel = (LinearLayout) findViewById(R.id.layControlPanel);
		laySearch = (LinearLayout) findViewById(R.id.laySearch);
		layMusicSeek = (LinearLayout) findViewById(R.id.layMusicSeek);
		layHighlight = (LinearLayout) findViewById(R.id.layHighlight);
		layMain = (LinearLayout) findViewById(R.id.layMain);
		layBody = (AbsoluteLayout) findViewById(R.id.layBody);
		skbMusic = (SeekBar) findViewById(R.id.skbMusic);
		lstMusic = (ListView) findViewById(R.id.lstMusic);
		grdMenu = (GridView) findViewById(R.id.grdMenu);
	}

	/* 设置菜单列表 */
	public void SetMenuList()
	{
		List<Map<String, Object>> lstMenuItem = new ArrayList<Map<String, Object>>(); // 菜单功能列表
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_settings);
		map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_settings));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_refresh);
		map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_refresh_list));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_keep_screen_on);
		if (sp.getBoolean("KeepScreenOn", false))
		{
			map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_keep_screen_on_false));
			lstMusic.setKeepScreenOn(true);
		}
		else
		{
			map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_keep_screen_on_true));
			lstMusic.setKeepScreenOn(false);
		}
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_search);
		map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_search));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_wait);
		map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_wait));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_wait);
		map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_wait));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_wait);
		map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_wait));
		lstMenuItem.add(map);

		// 横屏多一项菜单
		if (ScreenOrantation == 1 || ScreenOrantation == 3)
		{
			map = new HashMap<String, Object>();
			map.put("ItemIcon", R.drawable.menu_wait);
			map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_wait));
			lstMenuItem.add(map);
		}

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_exit);
		map.put("ItemText", getResources().getString(R.string.srcmain_extend_menu_exit));
		lstMenuItem.add(map);

		SimpleAdapter adapter = new SimpleAdapter(this, lstMenuItem, R.layout.grid_menu, new String[] { "ItemIcon", "ItemText" }, new int[] { R.id.imgMenu, R.id.txtMenu });
		grdMenu.setAdapter(adapter);
	}

	/* 设置播放列表项目内容 */
	public Map<String, Object> GetMusicID3(String path, String oldname)
	{
		Map<String, Object> map = new HashMap<String, Object>();

		ID3Tag ID3All = (ID3Tag) mt.ReadID3(path);
		if (ID3All != null)
		{
			if (ID3All instanceof ID3V2_3_0Tag)
			{
				ID3V2_3_0Tag ID3v2 = (ID3V2_3_0Tag) ID3All;
				map.put("Title", ID3v2.getTitle());
				map.put("SongInfo", ID3v2.getArtist() + " - " + ID3v2.getAlbum());
				map.put("Artist", ID3v2.getArtist());
				map.put("Album", ID3v2.getAlbum());
				map.put("Comment", ID3v2.getComment());
				try
				{
					map.put("Year", String.valueOf(ID3v2.getYear()));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					map.put("Track", String.valueOf(ID3v2.getTrackNumber()));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				String strGenre = ID3v2.getGenre();
				if (strGenre.indexOf("((") != -1 && strGenre.lastIndexOf("") != -1)
				{
					try
					{
						// 148 个流派（80 个基本流派和 68 个扩展流派）
						String Genre[] = { "Blues", "ClassicRock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "NewAge", "Oldies", "Other", "Pop", "R&B", "Rap",
								"Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "DeathMetal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk",
								"Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "SoundClip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative",
								"InstrumentalPop", "InstrumentalRock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "SouthernRock", "Comedy",
								"Cult", "Gangsta", "Top", "ChristianRap", "Pop/Funk", "Jungle", "NativeAmerican", "Cabaret", "NewWave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi",
								"Tribal", "AcidPunk", "AcidJazz", "Polka", "Retro", "Musical", "Rock&Roll", "HardRock", "Folk", "Folk-Rock", "NationalFolk", "Swing", "FastFusion", "Bebob", "Latin",
								"Revival", "Celtic", "Bluegrass", "Avantgarde", "GothicRock", "ProgessiveRock", "PsychedelicRock", "SymphonicRock", "SlowRock", "BigBand", "Chorus", "EasyListening",
								"Acoustic", "Humour", "Speech", "Chanson", "Opera", "ChamberMusic", "Sonata", "Symphony", "BootyBass", "Primus", "PornGroove", "Satire", "SlowJam", "Club", "Tango",
								"Samba", "Folklore", "Ballad", "PowerBallad", "RhythmicSoul", "Freestyle", "Duet", "PunkRock", "DrumSolo", "Acapella", "Euro-House", "DanceHall", "Goa", "Drum&Bass",
								"Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "PolskPunk", "Beat", "ChristianGangstaRap", "HeavyMetal", "BlackMetal", "Crossover",
								"ContemporaryChristian", "ChristianRock", "Merengue", "Salsa", "TrashMetal", "Anime", "JPop", "Synthpop" };

						strGenre = Genre[Integer.parseInt(strGenre.substring(strGenre.indexOf("((") + 2, strGenre.lastIndexOf("") - 1))];
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				map.put("Genre", strGenre);
			}
			else if (ID3All instanceof ID3V1_1Tag)
			{
				ID3V1_1Tag ID3v1_1 = (ID3V1_1Tag) ID3All;
				map.put("Title", ID3v1_1.getTitle());
				map.put("SongInfo", ID3v1_1.getArtist() + " - " + ID3v1_1.getAlbum());
				map.put("Artist", ID3v1_1.getArtist());
				map.put("Album", ID3v1_1.getAlbum());
				map.put("Comment", ID3v1_1.getComment());
				map.put("Year", ID3v1_1.getYear());
				map.put("Track", String.valueOf(ID3v1_1.getAlbumTrack()));
				map.put("Genre", String.valueOf(ID3v1_1.getGenre()));
			}
			else if (ID3All instanceof ID3V1_0Tag)
			{
				ID3V1_0Tag ID3v1_0 = (ID3V1_0Tag) ID3All;
				map.put("Title", ID3v1_0.getTitle());
				map.put("SongInfo", ID3v1_0.getArtist() + " - " + ID3v1_0.getAlbum());
				map.put("Artist", ID3v1_0.getArtist());
				map.put("Album", ID3v1_0.getAlbum());
				map.put("Comment", ID3v1_0.getComment());
				map.put("Year", ID3v1_0.getYear());
				map.put("Track", getResources().getString(R.string.srcmain_no_track_number));
				map.put("Genre", String.valueOf(ID3v1_0.getGenre()));
			}
		}
		else
		{
			map.put("Title", oldname.substring(oldname.lastIndexOf("/") + 1));
			map.put("SongInfo", getResources().getString(R.string.srcmain_no_id3_tag_found));
			map.put("Artist", "");
			map.put("Album", "");
			map.put("Comment", "");
			map.put("Year", "");
			map.put("Track", "");
			map.put("Genre", "");
		}

		return map;
	}

	/* 搜索框切换 */
	public void SearchBoxSwitcher()
	{
		AbsoluteLayout.LayoutParams layParSearch = (AbsoluteLayout.LayoutParams) laySearch.getLayoutParams(); // 获取scrLRC尺寸参数

		// 托盘消失动画
		Animation animHide = new TranslateAnimation(0, 0, 65, 0);
		animHide.setDuration(ANIMATION_TIME);
		animHide.setInterpolator(new DecelerateInterpolator());

		// 托盘显示动画
		Animation animShow = new TranslateAnimation(0, 0, -65, 0);
		animShow.setDuration(ANIMATION_TIME);
		animShow.setInterpolator(new DecelerateInterpolator());

		if (layParSearch.y == -65)
		{// 调用显示
			layParSearch.y = 0;
			laySearch.setLayoutParams(layParSearch);

			if (sp.getBoolean("chkUseAnimation", true))
				laySearch.startAnimation(animShow);
		}
		else if (layParSearch.y == 0)
		{// 调用隐藏
			layParSearch.y = -65;
			laySearch.setLayoutParams(layParSearch);

			if (sp.getBoolean("chkUseAnimation", true))
				laySearch.startAnimation(animHide);
		}
	}

	/* 列表到歌词切换 */
	public void List2LRCSwitcher()
	{
		if (CurrentShown == 0)
		{
			AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // 获取scrLRC尺寸参数
			AbsoluteLayout.LayoutParams layMusic = (AbsoluteLayout.LayoutParams) lstMusic.getLayoutParams(); // 获取lstMusic尺寸参数
			AbsoluteLayout.LayoutParams layMusicHighlight = (AbsoluteLayout.LayoutParams) layHighlight.getLayoutParams(); // 获取layHighlight尺寸参数

			Animation anim = null;

			if (ScreenOrantation == 1 || ScreenOrantation == 3)
			{
				layMusic.x = -552;
				layMusicHighlight.x = -552;
				anim = new TranslateAnimation(552, 0, 0, 0);
			}
			else
			{
				layMusic.x = -320;
				layMusicHighlight.x = -320;
				anim = new TranslateAnimation(320, 0, 0, 0);
			}

			layLRC.x = 0;
			CurrentShown = 1;

			txtLRC.setLayoutParams(layLRC);
			lstMusic.setLayoutParams(layMusic);
			layHighlight.setLayoutParams(layMusicHighlight);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				anim.setDuration(ANIMATION_TIME);
				anim.setInterpolator(new DecelerateInterpolator());

				txtLRC.startAnimation(anim);
				lstMusic.startAnimation(anim);

				if (layHighlight.getVisibility() == View.VISIBLE)
					layHighlight.startAnimation(anim);
			}
		}
	}

	public void LRC2ListSwitcher()
	{
		if (CurrentShown == 1)
		{
			AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // 获取scrLRC尺寸参数
			AbsoluteLayout.LayoutParams layMusic = (AbsoluteLayout.LayoutParams) lstMusic.getLayoutParams(); // 获取lstMusic尺寸参数
			AbsoluteLayout.LayoutParams layMusicHighlight = (AbsoluteLayout.LayoutParams) layHighlight.getLayoutParams(); // 获取layHighlight尺寸参数

			Animation anim = null;

			if (ScreenOrantation == 1 || ScreenOrantation == 3)
			{
				layLRC.x = 552;
				anim = new TranslateAnimation(-552, 0, 0, 0);
			}
			else
			{
				layLRC.x = 320;
				anim = new TranslateAnimation(-320, 0, 0, 0);
			}

			layMusic.x = 0;
			layMusicHighlight.x = 0;
			CurrentShown = 0;

			lstMusic.setLayoutParams(layMusic);
			txtLRC.setLayoutParams(layLRC);
			layHighlight.setLayoutParams(layMusicHighlight);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				anim.setDuration(ANIMATION_TIME);
				anim.setInterpolator(new DecelerateInterpolator());

				txtLRC.startAnimation(anim);
				lstMusic.startAnimation(anim);
				if (layHighlight.getVisibility() == View.VISIBLE)
					layHighlight.startAnimation(anim);
			}
		}
	}

	/* 进度条切换控制条 */
	public void Progress2ControlSwitcher()
	{
		if (layMusicSeek.getVisibility() == View.VISIBLE)
		{
			layMusicSeek.setVisibility(View.GONE);
			layControlPanel.setVisibility(View.VISIBLE);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				// 托盘消失动画
				Animation animHide = new TranslateAnimation(0, 0, 0, 70);
				animHide.setDuration(ANIMATION_TIME);
				animHide.setInterpolator(new DecelerateInterpolator());

				// 托盘显示动画
				Animation animShow = new AlphaAnimation(0, 1);
				animShow.setDuration(ANIMATION_TIME);
				animShow.setInterpolator(new DecelerateInterpolator());

				layMusicSeek.startAnimation(animHide);
				layControlPanel.startAnimation(animShow);
			}
		}
	}

	/* 控制条切换进度条 */
	public void Control2ProgressSwitcher()
	{
		if (layControlPanel.getVisibility() == View.VISIBLE)
		{
			layControlPanel.setVisibility(View.GONE);
			layMusicSeek.setVisibility(View.VISIBLE);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				// 托盘消失动画
				Animation animHide = new TranslateAnimation(0, 0, 0, 70);
				animHide.setDuration(ANIMATION_TIME);
				animHide.setInterpolator(new DecelerateInterpolator());

				// 托盘显示动画
				Animation animShow = new AlphaAnimation(0, 1);
				animShow.setDuration(ANIMATION_TIME);
				animShow.setInterpolator(new DecelerateInterpolator());

				layControlPanel.startAnimation(animHide);
				layMusicSeek.startAnimation(animShow);
			}
		}
	}

	/* 控制条/进度条切换 */
	public void ControlProgressSwitcher()
	{
		// 托盘消失动画
		Animation animHide = new TranslateAnimation(0, 0, 0, 70);
		animHide.setDuration(ANIMATION_TIME);
		animHide.setInterpolator(new DecelerateInterpolator());

		// 托盘显示动画
		Animation animShow = new AlphaAnimation(0, 1);
		animShow.setDuration(ANIMATION_TIME);
		animShow.setInterpolator(new DecelerateInterpolator());

		if (layControlPanel.getVisibility() == View.VISIBLE)
		{
			layControlPanel.setVisibility(View.GONE);
			layMusicSeek.setVisibility(View.VISIBLE);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				layControlPanel.startAnimation(animHide);
				layMusicSeek.startAnimation(animShow);
			}
		}
		else
		{
			layMusicSeek.setVisibility(View.GONE);
			layControlPanel.setVisibility(View.VISIBLE);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				layMusicSeek.startAnimation(animHide);
				layControlPanel.startAnimation(animShow);
			}
		}
	}

	/* 绑定控件事件监听 */
	private void ListernerBinding()
	{
		/* 上一首 */
		btnLast.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Last();
			}
		});

		/* 播放 */
		btnPlay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Play(ms.getCurrIndex());
			}
		});

		/* 暂停 */
		btnPause.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Pause();
			}
		});

		/* 下一首 */
		btnNext.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Next(false);
			}
		});

		/* 播放模式 */
		btnPlayMode.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				String index = sp.getString("lstPlayMode", "1"); // 0－顺序播放；1－全部循环；2－单曲暂停；3－单曲循环；4－随机播放
				Editor edt = sp.edit();

				if (index.equals("0"))
				{
					edt.putString("lstPlayMode", "1");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_repeat_all);
					Toast.makeText(srcMain.this, getResources().getString(R.string.srcmain_play_mode_repeat_all), Toast.LENGTH_SHORT).show();
				}
				else if (index.equals("1"))
				{
					edt.putString("lstPlayMode", "2");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_pause_current);
					Toast.makeText(srcMain.this, getResources().getString(R.string.srcmain_play_mode_pause_current), Toast.LENGTH_SHORT).show();
				}
				else if (index.equals("2"))
				{
					edt.putString("lstPlayMode", "3");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_repeat_current);
					Toast.makeText(srcMain.this, getResources().getString(R.string.srcmain_play_mode_repeat_current), Toast.LENGTH_SHORT).show();
				}
				else if (index.equals("3"))
				{
					edt.putString("lstPlayMode", "4");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_shuffle);
					Toast.makeText(srcMain.this, getResources().getString(R.string.srcmain_play_mode_shuffle), Toast.LENGTH_SHORT).show();
				}
				else if (index.equals("4"))
				{
					edt.putString("lstPlayMode", "0");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_close);
					Toast.makeText(srcMain.this, getResources().getString(R.string.srcmain_play_mode_close), Toast.LENGTH_SHORT).show();
				}

				edt.commit();
			}
		});

		/* 显示歌词 */
		btnLRC.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				List2LRCSwitcher();
			}
		});

		/* 高亮框上的快捷播放按钮 */
		btnHighlightPlay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Play(SelectedItemIndex);
			}
		});

		/* 搜索按钮 */
		btnSearch.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Editor edt = sp.edit();
				edt.putString("LastKeyword", txtKeyword.getText().toString());
				edt.commit();

				SetMusicListByDB();
				txtKeyword.setText("");
				SearchBoxSwitcher();
				txtKeyword.clearFocus();
			}
		});

		/* 选中列表 */
		lstMusic.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				SelectedItemIndex = arg2; // 更新当前选中的序号

				int Pos[] = { -1, -1 };
				arg1.getLocationOnScreen(Pos);

				AbsoluteLayout.LayoutParams layMusicHighlight = (AbsoluteLayout.LayoutParams) layHighlight.getLayoutParams(); // 获取layHighlight尺寸参数
				int OldY = layMusicHighlight.y;

				// 记录歌曲列表滚动高度（需要减去偏移量，且横竖屏不同）

				Editor edt = sp.edit();
				if (ScreenOrantation == 1 || ScreenOrantation == 3)
					edt.putFloat("LastMusicListY", (float) (Pos[1] - layHighlight.getHeight() - 8));
				else
					edt.putFloat("LastMusicListY", (float) (Pos[1] - layHighlight.getHeight() * 1.5 - 2));

				if (sp.getFloat("LastMusicListY", 0) < 0)
				{
					edt.putFloat("LastMusicListY", 0);
					lstMusic.setSelectionFromTop(arg2, 0); // 回到顶端
				}
				edt.commit();

				layMusicHighlight.y = (int) sp.getFloat("LastMusicListY", 0);
				layHighlight.setLayoutParams(layMusicHighlight);

				if (sp.getBoolean("chkUseAnimation", true) && layHighlight.getVisibility() == View.VISIBLE) // 只有显示时才播放动画
				{
					Animation anim = new TranslateAnimation(0, 0, -(layMusicHighlight.y - OldY), 0);
					anim.setDuration(200);
					anim.setInterpolator(new DecelerateInterpolator());
					layHighlight.startAnimation(anim);
				}

				layHighlight.setVisibility(View.VISIBLE);
			}
		});

		/* 菜单项单击 */
		grdMenu.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				switch (arg2)
				{
					case 0:
						startActivity(new Intent(srcMain.this, SettingService.class));
						break;
					case 1:
						if (!IsMusicRefreshing)
						{
							dlg.ShowDialog(getResources().getString(R.string.srcmain_create_play_list_title), getResources().getString(R.string.srcmain_create_play_list_message),
									new OnClickListener()
									{
										public void onClick(View v)
										{
											SetMusicInfoToDB();
											dlg.CloseDialog();
										}
									}, new OnClickListener()
									{
										public void onClick(View v)
										{
											dlg.CloseDialog();
										}
									});
						}
						else
						{
							dlg.ShowDialog(getResources().getString(R.string.srcmain_create_play_list_title), getResources().getString(R.string.srcmain_indexing), new OnClickListener()
							{
								public void onClick(View v)
								{
									dlg.CloseDialog();
								}
							}, null);
						}

						break;
					case 2:
						TextView tv = (TextView) arg1.findViewById(R.id.txtMenu);

						if (!sp.getBoolean("KeepScreenOn", false))
						{
							lstMusic.setKeepScreenOn(true);
							tv.setText(R.string.srcmain_extend_menu_keep_screen_on_false);
							Editor edt = sp.edit();
							edt.putBoolean("KeepScreenOn", true);
							edt.commit();
							Toast.makeText(srcMain.this, getResources().getString(R.string.srcmain_extend_menu_keep_screen_on_true), Toast.LENGTH_SHORT).show();
						}
						else
						{
							lstMusic.setKeepScreenOn(false);
							tv.setText(R.string.srcmain_extend_menu_keep_screen_on_true);
							Editor edt = sp.edit();
							edt.putBoolean("KeepScreenOn", false);
							edt.commit();
							Toast.makeText(srcMain.this, getResources().getString(R.string.srcmain_extend_menu_keep_screen_on_false), Toast.LENGTH_SHORT).show();
						}

						break;
					case 3:
						SearchBoxSwitcher();
						break;
					case 7:
						if (ScreenOrantation == 1 || ScreenOrantation == 3)
							;
						else
						{
							Editor edt = sp.edit();
							edt.putString("LastKeyword", "");
							edt.putBoolean("Started", false); // 是否启动标志，给Widget判断
							edt.commit();
							System.exit(0);

							break;
						}
					case 8:
						if (ScreenOrantation == 1 || ScreenOrantation == 3)
							System.exit(0);
						else
							;

						break;
				}
				HideExtendPanel();
			}
		});

		/* 歌词列表触摸 */
		txtLRC.setOnTouchListener(new OnTouchListener()
		{
			// 暂时支持最多两点（文字缩放）
			float DownPosX[] = { -1, -1 };
			float DownPosY[] = { -1, -1 };

			boolean IsMoved = false; // 是否经过ACTION_MOVE事件
			boolean Switch2List = false; // 是否需要将歌词切换到播放列表
			float LastDistance = -1; // 上一次两指间的距离
			int FingerDownPosY = -1; // 手指按下时歌词的Y坐标

			private float GetFingerDistance(float PosX1, float PosY1, float PosX2, float PosY2)
			{
				return (float) Math.sqrt((PosX1 - PosX2) * (PosX1 - PosX2) + (PosY1 - PosY2) * (PosY1 - PosY2));
			}

			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN)
				{// 获取两点的坐标
					for (int i = 0; i < 2; i++)
					{
						DownPosX[i] = event.getX(i);
						DownPosY[i] = event.getY(i);
					}

					AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // 获取scrLRC尺寸参数
					FingerDownPosY = layLRC.y;
					LastDistance = GetFingerDistance(DownPosX[0], DownPosY[0], DownPosX[1], DownPosY[1]);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{// 按下歌词显示/隐藏进度条

					if (Switch2List)
					{
						Switch2List = false;
						IsMoved = false;
						LRC2ListSwitcher();
					}
					else if (IsMoved) // Move过不执行托盘变化
						IsMoved = false;
					else
						ControlProgressSwitcher();

					for (int i = 0; i < 2; i++)
					{
						DownPosX[i] = -1;
						DownPosY[i] = -1;
					}

					LastDistance = -1;
					FingerDownPosY = -1;

					ls.setCanRefreshLRC(true);

					// 设置字体大小
					Editor edt = sp.edit();
					edt.putString("txtLRCFontSize", String.valueOf(txtLRC.getTextSize()));
					edt.commit();
				}
				else if (event.getAction() == MotionEvent.ACTION_MOVE)
				{
					if (DownPosY[1] == -1 && DownPosX[1] == -1)
					{
						// 获取垂直/水平方向手指移动绝对值
						float AbsX = Math.abs(event.getX(0) - DownPosX[0]);
						float AbsY = Math.abs(event.getY(0) - DownPosY[0]);

						// 通过绝对值大小来判定手势方向
						if (AbsX > AbsY)
						{// 横向切换页面
							if (event.getX(0) - DownPosX[0] > 150)
								Switch2List = true;
						}
						else
						{// 纵向（含恰好相等的情况）滚动歌词
							AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // 获取scrLRC尺寸参数
							layLRC.y += (int) (event.getY(0) - DownPosY[0]);
							txtLRC.setLayoutParams(layLRC);
						}
					}
					else
					{
						if (LastDistance != -1 && FingerDownPosY != -1)
						{
							float Distance = GetFingerDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1)) - LastDistance;
							float TextSize = (float) (txtLRC.getTextSize() + Distance * 0.1);
							if (TextSize >= 15 && TextSize <= 35)
							{
								AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // 获取scrLRC尺寸参数
								layLRC.y = FingerDownPosY;
								txtLRC.setLayoutParams(layLRC);
								txtLRC.setTextSize(TextSize);
							}
						}

						for (int i = 0; i < 2; i++)
						{
							DownPosX[i] = event.getX(i);
							DownPosY[i] = event.getY(i);
						}

						LastDistance = GetFingerDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

						AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // 获取scrLRC尺寸参数
						FingerDownPosY = layLRC.y;
					}

					IsMoved = true; // Move过的标记
				}
				else if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					ls.setCanRefreshLRC(false);

					DownPosX[0] = event.getX();
					DownPosY[0] = event.getY();
				}

				return true; // 继续回传，否则ACTION_DOWN后接收不到其它事件
			}
		});

		/* 进度条拖动控制 */
		skbMusic.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			// 停止拖动恢复进度条
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				ms.setCanRefreshSeekBar(true);
				IsTouchToSeek = false;
			}

			// 拖动时停止更新进度条
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				ms.setCanRefreshSeekBar(false);
				IsTouchToSeek = true;
			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (IsTouchToSeek)
					ms.getMp().seekTo(progress);
			}
		});

		/* 歌曲列表长按 */
		lstMusic.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				Map<String, Object> mapItem = lstSong.get(arg2); // 获取当前项的全部Map内容

				String strMessage = "标题：" + mapItem.get("Title") + "\n" + "艺术家：" + mapItem.get("Artist") + "\n" + "专辑：" + mapItem.get("Album") + "\n" + "年份：" + mapItem.get("Year") + "\n" + "流派："
						+ mapItem.get("Genre") + "\n" + "音轨号：" + mapItem.get("Track") + "\n" + "备注：" + mapItem.get("Comment");

				dlg.ShowDialog((String) mapItem.get("Title"), strMessage, new View.OnClickListener()
				{
					public void onClick(View v)
					{
						dlg.CloseDialog();
					}
				}, null);

				return false;
			}
		});

		/* 歌曲列表触摸 */
		lstMusic.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_MOVE)
					layHighlight.setVisibility(View.INVISIBLE);

				return false;
			}
		});

		/* 关键词按键监听 */
		txtKeyword.setOnKeyListener(new OnKeyListener()
		{
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_ENTER)
				{// 搜索
					AbsoluteLayout.LayoutParams layParSearch = (AbsoluteLayout.LayoutParams) laySearch.getLayoutParams();
					if (layParSearch.y == 0)
					{
						Editor edt = sp.edit();
						edt.putString("LastKeyword", txtKeyword.getText().toString());
						edt.commit();

						SetMusicListByDB();
						txtKeyword.setText("");
						SearchBoxSwitcher();
						txtKeyword.clearFocus();
					}
				}

				return false;
			}
		});
	}

	/* 显示扩展托盘 */
	public void ShowExtendPanel()
	{
		Animation anim = null;

		if (layExtendMenu.getVisibility() == View.GONE)
		{
			layExtendMenu.setVisibility(View.VISIBLE);
			anim = new AlphaAnimation(0, 1);
		}

		if (sp.getBoolean("chkUseAnimation", true))
		{
			anim.setDuration(ANIMATION_TIME);
			layExtendMenu.startAnimation(anim);
		}
	}

	/* 隐藏扩展托盘 */
	public void HideExtendPanel()
	{
		Animation anim = null;

		if (layExtendMenu.getVisibility() == View.VISIBLE)
		{
			layExtendMenu.setVisibility(View.GONE);
			anim = new AlphaAnimation(1, 0);
		}

		if (sp.getBoolean("chkUseAnimation", true))
		{
			anim.setDuration(ANIMATION_TIME);
			layExtendMenu.startAnimation(anim);
		}
	}

	/* 扩展托盘显示/隐藏 */
	public void ExtendPanelSwitcher()
	{
		Animation anim = null;

		if (layExtendMenu.getVisibility() == View.VISIBLE)
		{
			layExtendMenu.setVisibility(View.GONE);
			anim = new AlphaAnimation(1, 0);
		}
		else
		{
			layExtendMenu.setVisibility(View.VISIBLE);
			anim = new AlphaAnimation(0, 1);
		}

		if (sp.getBoolean("chkUseAnimation", true))
		{
			anim.setDuration(ANIMATION_TIME);
			layExtendMenu.startAnimation(anim);
		}
	}

	/* 按键动作 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			LRC2ListSwitcher();
			Progress2ControlSwitcher();

			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			ExtendPanelSwitcher();

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	public List<Map<String, Object>> getLstSong()
	{
		return lstSong;
	}

	public void setLstSong(List<Map<String, Object>> lstSong)
	{
		this.lstSong = lstSong;
	}

	public int getScreenOrantation()
	{
		return ScreenOrantation;
	}

	public void setScreenOrantation(int screenOrantation)
	{
		ScreenOrantation = screenOrantation;
	}

	public int getCurrentShown()
	{
		return CurrentShown;
	}

	public void setCurrentShown(int currentShown)
	{
		CurrentShown = currentShown;
	}

	public int getSelectedItemIndex()
	{
		return SelectedItemIndex;
	}

	public void setSelectedItemIndex(int selectedItemIndex)
	{
		SelectedItemIndex = selectedItemIndex;
	}

	public boolean isIsTouchToSeek()
	{
		return IsTouchToSeek;
	}

	public void setIsTouchToSeek(boolean isTouchToSeek)
	{
		IsTouchToSeek = isTouchToSeek;
	}

	public boolean isIsMusicRefreshing()
	{
		return IsMusicRefreshing;
	}

	public void setIsMusicRefreshing(boolean isMusicRefreshing)
	{
		IsMusicRefreshing = isMusicRefreshing;
	}

	public boolean isIsKeepScreenOn()
	{
		return IsKeepScreenOn;
	}

	public void setIsKeepScreenOn(boolean isKeepScreenOn)
	{
		IsKeepScreenOn = isKeepScreenOn;
	}

	public SharedPreferences getSp()
	{
		return sp;
	}

	public void setSp(SharedPreferences sp)
	{
		this.sp = sp;
	}

	public ImageButton getBtnLast()
	{
		return btnLast;
	}

	public void setBtnLast(ImageButton btnLast)
	{
		this.btnLast = btnLast;
	}

	public ImageButton getBtnPlay()
	{
		return btnPlay;
	}

	public void setBtnPlay(ImageButton btnPlay)
	{
		this.btnPlay = btnPlay;
	}

	public ImageButton getBtnNext()
	{
		return btnNext;
	}

	public void setBtnNext(ImageButton btnNext)
	{
		this.btnNext = btnNext;
	}

	public ImageButton getBtnPause()
	{
		return btnPause;
	}

	public void setBtnPause(ImageButton btnPause)
	{
		this.btnPause = btnPause;
	}

	public ImageButton getBtnPlayMode()
	{
		return btnPlayMode;
	}

	public void setBtnPlayMode(ImageButton btnPlayMode)
	{
		this.btnPlayMode = btnPlayMode;
	}

	public ImageButton getBtnLRC()
	{
		return btnLRC;
	}

	public void setBtnLRC(ImageButton btnLRC)
	{
		this.btnLRC = btnLRC;
	}

	public ImageButton getBtnHighlightPlay()
	{
		return btnHighlightPlay;
	}

	public void setBtnHighlightPlay(ImageButton btnHighlightPlay)
	{
		this.btnHighlightPlay = btnHighlightPlay;
	}

	public TextView getTxtTitle()
	{
		return txtTitle;
	}

	public void setTxtTitle(TextView txtTitle)
	{
		this.txtTitle = txtTitle;
	}

	public TextView getTxtTime()
	{
		return txtTime;
	}

	public void setTxtTime(TextView txtTime)
	{
		this.txtTime = txtTime;
	}

	public TextView getTxtScanHint()
	{
		return txtScanHint;
	}

	public void setTxtScanHint(TextView txtScanHint)
	{
		this.txtScanHint = txtScanHint;
	}

	public TextView getTxtLRC()
	{
		return txtLRC;
	}

	public void setTxtLRC(TextView txtLRC)
	{
		this.txtLRC = txtLRC;
	}

	public LinearLayout getLayExtendMenu()
	{
		return layExtendMenu;
	}

	public void setLayExtendMenu(LinearLayout layExtendMenu)
	{
		this.layExtendMenu = layExtendMenu;
	}

	public LinearLayout getLayControlPanel()
	{
		return layControlPanel;
	}

	public void setLayControlPanel(LinearLayout layControlPanel)
	{
		this.layControlPanel = layControlPanel;
	}

	public LinearLayout getLayMusicSeek()
	{
		return layMusicSeek;
	}

	public void setLayMusicSeek(LinearLayout layMusicSeek)
	{
		this.layMusicSeek = layMusicSeek;
	}

	public LinearLayout getLayHighlight()
	{
		return layHighlight;
	}

	public void setLayHighlight(LinearLayout layHighlight)
	{
		this.layHighlight = layHighlight;
	}

	public AbsoluteLayout getLayBody()
	{
		return layBody;
	}

	public void setLayBody(AbsoluteLayout layBody)
	{
		this.layBody = layBody;
	}

	public ListView getLstMusic()
	{
		return lstMusic;
	}

	public void setLstMusic(ListView lstMusic)
	{
		this.lstMusic = lstMusic;
	}

	public GridView getGrdMenu()
	{
		return grdMenu;
	}

	public void setGrdMenu(GridView grdMenu)
	{
		this.grdMenu = grdMenu;
	}

	public SeekBar getSkbMusic()
	{
		return skbMusic;
	}

	public void setSkbMusic(SeekBar skbMusic)
	{
		this.skbMusic = skbMusic;
	}

	public LRCService getLs()
	{
		return ls;
	}

	public void setLs(LRCService ls)
	{
		this.ls = ls;
	}

	public MusicService getMs()
	{
		return ms;
	}

	public void setMs(MusicService ms)
	{
		this.ms = ms;
	}

	public MP3Tags getMt()
	{
		return mt;
	}

	public void setMt(MP3Tags mt)
	{
		this.mt = mt;
	}

	public MessageDialog getDlg()
	{
		return dlg;
	}

	public void setDlg(MessageDialog dlg)
	{
		this.dlg = dlg;
	}

	public DBProvider getDb()
	{
		return db;
	}

	public void setDb(DBProvider db)
	{
		this.db = db;
	}

	public PYProvider getPy()
	{
		return py;
	}

	public void setPy(PYProvider py)
	{
		this.py = py;
	}

	public HandlerService getHs()
	{
		return hs;
	}

	public void setHs(HandlerService hs)
	{
		this.hs = hs;
	}

	public LinearLayout getLayMain()
	{
		return layMain;
	}

	public void setLayMain(LinearLayout layMain)
	{
		this.layMain = layMain;
	}

	public LinearLayout getLaySplash()
	{
		return laySplash;
	}

	public void setLaySplash(LinearLayout laySplash)
	{
		this.laySplash = laySplash;
	}

	public static int getAnimationTime()
	{
		return ANIMATION_TIME;
	}

	public boolean isIsSplashThreadAlive()
	{
		return IsSplashThreadAlive;
	}

	public void setIsSplashThreadAlive(boolean isSplashThreadAlive)
	{
		IsSplashThreadAlive = isSplashThreadAlive;
	}

	public ImageButton getBtnSearch()
	{
		return btnSearch;
	}

	public void setBtnSearch(ImageButton btnSearch)
	{
		this.btnSearch = btnSearch;
	}

	public TextView getTxtKeyword()
	{
		return txtKeyword;
	}

	public void setTxtKeyword(TextView txtKeyword)
	{
		this.txtKeyword = txtKeyword;
	}

	public LinearLayout getLaySearch()
	{
		return laySearch;
	}

	public void setLaySearch(LinearLayout laySearch)
	{
		this.laySearch = laySearch;
	}

	public static int getSplashTime()
	{
		return SPLASH_TIME;
	}

	public MusicAdapter getAdapter()
	{
		return adapter;
	}

	public void setAdapter(MusicAdapter adapter)
	{
		this.adapter = adapter;
	}
}