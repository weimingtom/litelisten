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
	private static final int ANIMATION_TIME = 500; // ����ʱ��
	private static final int SPLASH_TIME = 3000; // ��������ʱ��

	private List<Map<String, Object>> lstSong = new ArrayList<Map<String, Object>>(); // �����б�
	private int ScreenOrantation = 0;// ��Ļ����
	private int CurrentShown = 0; // 0�������б�1�������Ϣ
	private int SelectedItemIndex = -1; // ѡ�еĸ������
	private boolean IsTouchToSeek = false; // �жϵ�ǰ�Ƿ����û��϶�����
	private boolean IsMusicRefreshing = false;
	private boolean IsKeepScreenOn = false; // ��ǰ�Ƿ񱣳���Ļ����
	private SharedPreferences sp = null;
	private boolean IsSplashThreadAlive = false; // ��ʾSplash���߳��Ƿ���

	/* ����ؼ����Զ����� */
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

		/* ���ô�����ʽ�����밴��˳�� */
		requestWindowFeature(Window.FEATURE_NO_TITLE); // �ޱ�����
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
		sp = getSharedPreferences("com.littledai.litelisten_preferences", 0); // ��ȡ�����ļ�

		// ����ϴγ������е���ʷ��¼
		Editor edt = sp.edit();
		edt.putString("LastKeyword", "");
		edt.putBoolean("Started", true); // �Ƿ�������־����Widget�ж�
		edt.commit();

		FindViews();
		ListernerBinding();

		/* ���ö������̼��� */
		ControlsReceiver ctrlReceiver = new ControlsReceiver(this);

		IntentFilter ittFilterButton = new IntentFilter(Intent.ACTION_MEDIA_BUTTON); // ���Ƽ�
		registerReceiver(ctrlReceiver, ittFilterButton);

		IntentFilter ittFilterPlug = new IntentFilter(Intent.ACTION_HEADSET_PLUG); // ���Ƽ�
		registerReceiver(ctrlReceiver, ittFilterPlug);

		IntentFilter ittFilterBluetooth = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED); // �����Ͽ�
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

	/* �������л���ִ��onCreate() */
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
			txtScanHint.setVisibility(View.VISIBLE); // ��ʾ��ʾ��ǩ

		txtTitle.setText(ms.getStrShownTitle());

		// ���ò���/��ͣ��ť
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

		// ����ת��ǰ��״̬
		AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // ��ȡscrLRC�ߴ����
		AbsoluteLayout.LayoutParams layMusic = (AbsoluteLayout.LayoutParams) lstMusic.getLayoutParams(); // ��ȡlstMusic�ߴ����

		if (CurrentShown == 1 && (ScreenOrantation == 1 || ScreenOrantation == 3))
		{// �������
			layLRC.x = 0;
			layMusic.x = -552;
			txtLRC.setLayoutParams(layLRC);
			lstMusic.setLayoutParams(layMusic);
		}
		else if (CurrentShown == 1 && ScreenOrantation == 0)
		{// �������
			layLRC.x = 0;
			layMusic.x = -320;
			txtLRC.setLayoutParams(layLRC);
			lstMusic.setLayoutParams(layMusic);
		}
		if (CurrentShown == 0 && (ScreenOrantation == 1 || ScreenOrantation == 3))
		{// �����б�
			layMusic.x = 0;
			layLRC.x = 552;
			lstMusic.setLayoutParams(layMusic);
			txtLRC.setLayoutParams(layLRC);
		}
		else if (CurrentShown == 0 && ScreenOrantation == 0)
		{// �����б�
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

	/* ������Ϣ��� */
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

							Map<String, Object> mapInfo = GetMusicID3((String) lstFile.get(i), (String) lstFile.get(i).substring(0, lstFile.get(i).lastIndexOf(".mp3"))); // ��ȡ������MP3����
							mapInfo.put("MusicPath", lstFile.get(i));
							mapInfo.put("LRCPath", lstFile.get(i).substring(0, lstFile.get(i).lastIndexOf(".mp3")) + ".lrc");

							// �������ݿ�
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

	/* �����ݿ��ȡ������Ϣ */
	public void SetMusicListByDB()
	{
		new Thread()
		{
			public void run()
			{
				while (IsMusicRefreshing)
				{// �ȴ�����߳����
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
				String Keyword = sp.getString("LastKeyword", ""); // �ϴ������Ĺؼ���

				// ��������ʽ
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
					// ���½���
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

	/* ����ר��ͼ�� */
	public void SetAlbumIcon()
	{
		adapter.getView(ms.getCurrIndex(), null, lstMusic);
		adapter.notifyDataSetChanged();
		lstMusic.setSelectionFromTop(ms.getCurrIndex(), (int) sp.getFloat("LastMusicListY", 0)); // �ָ��ղŵ�λ��
	}

	/* ���ò���ģʽ */
	public void SetPlayMode()
	{
		String index = sp.getString("lstPlayMode", "1"); // 0��˳�򲥷ţ�1��ȫ��ѭ����2��������ͣ��3������ѭ����4���������

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

	/* ���ó������� */
	public void SetLanguage()
	{
		String index = sp.getString("lstLanguage", "3");
		if (!index.equals("3"))
		{
			Configuration config = getResources().getConfiguration(); // ������ö���

			if (index.equals("0"))
				config.locale = Locale.SIMPLIFIED_CHINESE; // ��������
			else if (index.equals("1"))
				config.locale = Locale.TRADITIONAL_CHINESE; // ��������
			else if (index.equals("2"))
				config.locale = Locale.US; // ��ʽӢ��

			getResources().updateConfiguration(config, null);
		}
	}

	/* ���ñ���ͼƬ */
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

	/* �������� */
	public void SetFonts()
	{
		txtLRC.setTextSize(Float.parseFloat(sp.getString("txtLRCFontSize", "18")));
		txtLRC.setTextColor(Color.parseColor(sp.getString("btnLRCNormalFontColor", "#FFFFFF")));
		if (sp.getBoolean("chkLRCFontShadow", true))
			txtLRC.setShadowLayer(2, 1, 1, Color.parseColor(sp.getString("btnLRCFontShadowColor", "#0099FF")));
		else
			txtLRC.setShadowLayer(2, 1, 1, Color.TRANSPARENT);
	}

	/* ��ȡ�ؼ�ʵ�� */
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

	/* ���ò˵��б� */
	public void SetMenuList()
	{
		List<Map<String, Object>> lstMenuItem = new ArrayList<Map<String, Object>>(); // �˵������б�
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

		// ������һ��˵�
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

	/* ���ò����б���Ŀ���� */
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
						// 148 �����ɣ�80 ���������ɺ� 68 ����չ���ɣ�
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

	/* �������л� */
	public void SearchBoxSwitcher()
	{
		AbsoluteLayout.LayoutParams layParSearch = (AbsoluteLayout.LayoutParams) laySearch.getLayoutParams(); // ��ȡscrLRC�ߴ����

		// ������ʧ����
		Animation animHide = new TranslateAnimation(0, 0, 65, 0);
		animHide.setDuration(ANIMATION_TIME);
		animHide.setInterpolator(new DecelerateInterpolator());

		// ������ʾ����
		Animation animShow = new TranslateAnimation(0, 0, -65, 0);
		animShow.setDuration(ANIMATION_TIME);
		animShow.setInterpolator(new DecelerateInterpolator());

		if (layParSearch.y == -65)
		{// ������ʾ
			layParSearch.y = 0;
			laySearch.setLayoutParams(layParSearch);

			if (sp.getBoolean("chkUseAnimation", true))
				laySearch.startAnimation(animShow);
		}
		else if (layParSearch.y == 0)
		{// ��������
			layParSearch.y = -65;
			laySearch.setLayoutParams(layParSearch);

			if (sp.getBoolean("chkUseAnimation", true))
				laySearch.startAnimation(animHide);
		}
	}

	/* �б�����л� */
	public void List2LRCSwitcher()
	{
		if (CurrentShown == 0)
		{
			AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // ��ȡscrLRC�ߴ����
			AbsoluteLayout.LayoutParams layMusic = (AbsoluteLayout.LayoutParams) lstMusic.getLayoutParams(); // ��ȡlstMusic�ߴ����
			AbsoluteLayout.LayoutParams layMusicHighlight = (AbsoluteLayout.LayoutParams) layHighlight.getLayoutParams(); // ��ȡlayHighlight�ߴ����

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
			AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // ��ȡscrLRC�ߴ����
			AbsoluteLayout.LayoutParams layMusic = (AbsoluteLayout.LayoutParams) lstMusic.getLayoutParams(); // ��ȡlstMusic�ߴ����
			AbsoluteLayout.LayoutParams layMusicHighlight = (AbsoluteLayout.LayoutParams) layHighlight.getLayoutParams(); // ��ȡlayHighlight�ߴ����

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

	/* �������л������� */
	public void Progress2ControlSwitcher()
	{
		if (layMusicSeek.getVisibility() == View.VISIBLE)
		{
			layMusicSeek.setVisibility(View.GONE);
			layControlPanel.setVisibility(View.VISIBLE);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				// ������ʧ����
				Animation animHide = new TranslateAnimation(0, 0, 0, 70);
				animHide.setDuration(ANIMATION_TIME);
				animHide.setInterpolator(new DecelerateInterpolator());

				// ������ʾ����
				Animation animShow = new AlphaAnimation(0, 1);
				animShow.setDuration(ANIMATION_TIME);
				animShow.setInterpolator(new DecelerateInterpolator());

				layMusicSeek.startAnimation(animHide);
				layControlPanel.startAnimation(animShow);
			}
		}
	}

	/* �������л������� */
	public void Control2ProgressSwitcher()
	{
		if (layControlPanel.getVisibility() == View.VISIBLE)
		{
			layControlPanel.setVisibility(View.GONE);
			layMusicSeek.setVisibility(View.VISIBLE);

			if (sp.getBoolean("chkUseAnimation", true))
			{
				// ������ʧ����
				Animation animHide = new TranslateAnimation(0, 0, 0, 70);
				animHide.setDuration(ANIMATION_TIME);
				animHide.setInterpolator(new DecelerateInterpolator());

				// ������ʾ����
				Animation animShow = new AlphaAnimation(0, 1);
				animShow.setDuration(ANIMATION_TIME);
				animShow.setInterpolator(new DecelerateInterpolator());

				layControlPanel.startAnimation(animHide);
				layMusicSeek.startAnimation(animShow);
			}
		}
	}

	/* ������/�������л� */
	public void ControlProgressSwitcher()
	{
		// ������ʧ����
		Animation animHide = new TranslateAnimation(0, 0, 0, 70);
		animHide.setDuration(ANIMATION_TIME);
		animHide.setInterpolator(new DecelerateInterpolator());

		// ������ʾ����
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

	/* �󶨿ؼ��¼����� */
	private void ListernerBinding()
	{
		/* ��һ�� */
		btnLast.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Last();
			}
		});

		/* ���� */
		btnPlay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Play(ms.getCurrIndex());
			}
		});

		/* ��ͣ */
		btnPause.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Pause();
			}
		});

		/* ��һ�� */
		btnNext.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Next(false);
			}
		});

		/* ����ģʽ */
		btnPlayMode.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				String index = sp.getString("lstPlayMode", "1"); // 0��˳�򲥷ţ�1��ȫ��ѭ����2��������ͣ��3������ѭ����4���������
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

		/* ��ʾ��� */
		btnLRC.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				List2LRCSwitcher();
			}
		});

		/* �������ϵĿ�ݲ��Ű�ť */
		btnHighlightPlay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ms.Play(SelectedItemIndex);
			}
		});

		/* ������ť */
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

		/* ѡ���б� */
		lstMusic.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				SelectedItemIndex = arg2; // ���µ�ǰѡ�е����

				int Pos[] = { -1, -1 };
				arg1.getLocationOnScreen(Pos);

				AbsoluteLayout.LayoutParams layMusicHighlight = (AbsoluteLayout.LayoutParams) layHighlight.getLayoutParams(); // ��ȡlayHighlight�ߴ����
				int OldY = layMusicHighlight.y;

				// ��¼�����б�����߶ȣ���Ҫ��ȥƫ�������Һ�������ͬ��

				Editor edt = sp.edit();
				if (ScreenOrantation == 1 || ScreenOrantation == 3)
					edt.putFloat("LastMusicListY", (float) (Pos[1] - layHighlight.getHeight() - 8));
				else
					edt.putFloat("LastMusicListY", (float) (Pos[1] - layHighlight.getHeight() * 1.5 - 2));

				if (sp.getFloat("LastMusicListY", 0) < 0)
				{
					edt.putFloat("LastMusicListY", 0);
					lstMusic.setSelectionFromTop(arg2, 0); // �ص�����
				}
				edt.commit();

				layMusicHighlight.y = (int) sp.getFloat("LastMusicListY", 0);
				layHighlight.setLayoutParams(layMusicHighlight);

				if (sp.getBoolean("chkUseAnimation", true) && layHighlight.getVisibility() == View.VISIBLE) // ֻ����ʾʱ�Ų��Ŷ���
				{
					Animation anim = new TranslateAnimation(0, 0, -(layMusicHighlight.y - OldY), 0);
					anim.setDuration(200);
					anim.setInterpolator(new DecelerateInterpolator());
					layHighlight.startAnimation(anim);
				}

				layHighlight.setVisibility(View.VISIBLE);
			}
		});

		/* �˵���� */
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
							edt.putBoolean("Started", false); // �Ƿ�������־����Widget�ж�
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

		/* ����б��� */
		txtLRC.setOnTouchListener(new OnTouchListener()
		{
			// ��ʱ֧��������㣨�������ţ�
			float DownPosX[] = { -1, -1 };
			float DownPosY[] = { -1, -1 };

			boolean IsMoved = false; // �Ƿ񾭹�ACTION_MOVE�¼�
			boolean Switch2List = false; // �Ƿ���Ҫ������л��������б�
			float LastDistance = -1; // ��һ����ָ��ľ���
			int FingerDownPosY = -1; // ��ָ����ʱ��ʵ�Y����

			private float GetFingerDistance(float PosX1, float PosY1, float PosX2, float PosY2)
			{
				return (float) Math.sqrt((PosX1 - PosX2) * (PosX1 - PosX2) + (PosY1 - PosY2) * (PosY1 - PosY2));
			}

			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN)
				{// ��ȡ���������
					for (int i = 0; i < 2; i++)
					{
						DownPosX[i] = event.getX(i);
						DownPosY[i] = event.getY(i);
					}

					AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // ��ȡscrLRC�ߴ����
					FingerDownPosY = layLRC.y;
					LastDistance = GetFingerDistance(DownPosX[0], DownPosY[0], DownPosX[1], DownPosY[1]);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{// ���¸����ʾ/���ؽ�����

					if (Switch2List)
					{
						Switch2List = false;
						IsMoved = false;
						LRC2ListSwitcher();
					}
					else if (IsMoved) // Move����ִ�����̱仯
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

					// ���������С
					Editor edt = sp.edit();
					edt.putString("txtLRCFontSize", String.valueOf(txtLRC.getTextSize()));
					edt.commit();
				}
				else if (event.getAction() == MotionEvent.ACTION_MOVE)
				{
					if (DownPosY[1] == -1 && DownPosX[1] == -1)
					{
						// ��ȡ��ֱ/ˮƽ������ָ�ƶ�����ֵ
						float AbsX = Math.abs(event.getX(0) - DownPosX[0]);
						float AbsY = Math.abs(event.getY(0) - DownPosY[0]);

						// ͨ������ֵ��С���ж����Ʒ���
						if (AbsX > AbsY)
						{// �����л�ҳ��
							if (event.getX(0) - DownPosX[0] > 150)
								Switch2List = true;
						}
						else
						{// ���򣨺�ǡ����ȵ�������������
							AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // ��ȡscrLRC�ߴ����
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
								AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // ��ȡscrLRC�ߴ����
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

						AbsoluteLayout.LayoutParams layLRC = (AbsoluteLayout.LayoutParams) txtLRC.getLayoutParams(); // ��ȡscrLRC�ߴ����
						FingerDownPosY = layLRC.y;
					}

					IsMoved = true; // Move���ı��
				}
				else if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					ls.setCanRefreshLRC(false);

					DownPosX[0] = event.getX();
					DownPosY[0] = event.getY();
				}

				return true; // �����ش�������ACTION_DOWN����ղ��������¼�
			}
		});

		/* �������϶����� */
		skbMusic.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			// ֹͣ�϶��ָ�������
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				ms.setCanRefreshSeekBar(true);
				IsTouchToSeek = false;
			}

			// �϶�ʱֹͣ���½�����
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

		/* �����б��� */
		lstMusic.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				Map<String, Object> mapItem = lstSong.get(arg2); // ��ȡ��ǰ���ȫ��Map����

				String strMessage = "���⣺" + mapItem.get("Title") + "\n" + "�����ң�" + mapItem.get("Artist") + "\n" + "ר����" + mapItem.get("Album") + "\n" + "��ݣ�" + mapItem.get("Year") + "\n" + "���ɣ�"
						+ mapItem.get("Genre") + "\n" + "����ţ�" + mapItem.get("Track") + "\n" + "��ע��" + mapItem.get("Comment");

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

		/* �����б��� */
		lstMusic.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_MOVE)
					layHighlight.setVisibility(View.INVISIBLE);

				return false;
			}
		});

		/* �ؼ��ʰ������� */
		txtKeyword.setOnKeyListener(new OnKeyListener()
		{
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_ENTER)
				{// ����
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

	/* ��ʾ��չ���� */
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

	/* ������չ���� */
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

	/* ��չ������ʾ/���� */
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

	/* �������� */
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