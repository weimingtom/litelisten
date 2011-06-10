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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
public class scrMain extends Activity
{
	private int OldHeight = -1;
	private static final int ANIMATION_TIME = 500; // 动画时长
	private static final int SPLASH_TIME = 3000; // 启动画面时长
	private static final int MUSIC_NOTIFY_ID = 1; // 音乐信息通知序号
	private static final int LRC_NOTIFY_ID = 2; // 浮动歌词锁定通知序号

	private List<Map<String, Object>> lstSong = new ArrayList<Map<String, Object>>(); // 播放列表
	private List<Map<String, String>> lstLRCFile = new ArrayList<Map<String, String>>(); // 文件列表
	private scrMain main; // 把自己复制成变量供线程内使用
	private int ScreenOrantation = 0;// 屏幕方向
	private int CurrentShown = 0; // 0－播放列表；1－歌词信息
	private int SelectedItemIndex = 0; // 选中的歌曲序号
	private int SelectedFileIndex = 0; // 选中的文件序号
	private boolean IsTouchToSeek = false; // 判断当前是否由用户拖动滑块
	private boolean IsKeepScreenOn = false; // 当前是否保持屏幕常亮
	private SharedPreferences sp = null;
	private boolean IsStartup = true; // 显示应用程序是否刚启动
	private boolean IsRefreshing = false; // 显示是否正在读取音乐列表
	private Toast toast = null; // 全局的Toast
	private int VerifyCode = 0; // 歌曲刷新的校验码
	private boolean IsShowingFavourite = false; // 是否显示最爱歌曲
	private boolean IsPlayingExternal = false; // 是否播放外部文件
	private boolean IsStartedUp = false; // 显示程序是否启动完成
	private boolean IsLRCMoved = false; // 歌词是否经过手指移动
	private boolean IsForceHideFloatLRC = false; // 是否强制隐藏桌面歌词

	/* 定义控件和自定义类 */
	private ImageButton btnLast;
	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPause;
	private ImageButton btnPlayMode;
	private ImageButton btnLRC;
	private ImageButton btnVolume;
	private ImageButton btnSearch;
	private Button btnFileOK;
	private Button btnFileCancel;
	private TextView txtTitle;
	private TextView txtTimeCurrent;
	private TextView txtTimeTotal;
	private TextView txtLRC;
	private TextView txtKeyword;
	private TextView txtCurrentPath;
	private LinearLayout layActivity;
	private LinearLayout layControlPanel;
	private RelativeLayout laySearch;
	private RelativeLayout layMain;
	private LinearLayout laySplash;
	private RelativeLayout layBody;
	private RelativeLayout layFileSelector;
	private ListView lstMusic;
	private ListView lstFile;
	private GridView grdMenu;
	private SeekBar skbMusic;
	private SeekBar skbVolume;
	private LinearLayout layLyricController;
	private LRCService ls;
	private MusicService ms;
	private DBProvider db;
	private PYProvider py;
	private HandlerService hs;
	private MusicAdapter adapter;
	private FileAdapterForMain fAdapter;
	private NotificationManager nm;
	private WindowManager wm;
	private FloatLRC fl;
	private WindowManager.LayoutParams layWM;
	private AudioManager am;
	private DisplayMetrics dm;
	private SettingProvider st;
	private SQLiteDatabase sd;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 设置窗口样式，必须按照顺序
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 无标题栏
		setContentView(R.layout.scr_main);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
	}

	/* 桌面小部件控制器监听线程 */
	public void WidgetsListener()
	{
		new Thread()
		{
			public void run()
			{
				while (true)
				{
					try
					{
						if (sp.getInt("MusicControl", 3) == 0)
							hs.getHdlPlayLast().sendEmptyMessage(0);
						else if (sp.getInt("MusicControl", 3) == 0)
							hs.getHdlPlayPause().sendEmptyMessage(0);
						else if (sp.getInt("MusicControl", 3) == 0)
							hs.getHdlPlayNext().sendEmptyMessage(0);

						Editor edt = sp.edit();
						edt.putInt("MusicControl", 3);
						edt.commit();

						sleep(1000);
					}
					catch (Exception e)
					{
						if (e.getMessage() != null)
							Log.w(Common.LOGCAT_TAG, e.getMessage());
						else
							e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/* 设置标题 */
	public void SetCurrentTitle(String title)
	{
		txtTitle.setText(title);

		// 如果超长则播放动画滚动
		float CurrWidth = Common.GetTextWidth(title, txtTitle.getTextSize());
		AbsoluteLayout.LayoutParams layTitle = (AbsoluteLayout.LayoutParams) txtTitle.getLayoutParams();

		if (CurrWidth > dm.widthPixels - 165)
		{
			layTitle.width = (int) CurrWidth;

			Animation anim = new TranslateAnimation(0, -(CurrWidth - dm.widthPixels + 165), 0, 0);
			anim.setDuration((long) (CurrWidth * 15));
			anim.setStartOffset(2500);
			anim.setRepeatCount(100);
			anim.setInterpolator(new LinearInterpolator());
			anim.setRepeatMode(Animation.REVERSE);
			txtTitle.startAnimation(anim);
		}
		else
		{
			layTitle.width = LayoutParams.FILL_PARENT;
			txtTitle.clearAnimation();
		}

		txtTitle.setLayoutParams(layTitle);
	}

	/* 屏幕方向切换 */
	public void DirectionSwitch()
	{
		ScreenOrantation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
		FindViews();
		ListernerBinding();
		Intent intent = getIntent();

		if (IsStartup)
		{
			main = this;
			sp = getSharedPreferences("com.galapk.litelisten_preferences", Context.MODE_PRIVATE); // 读取配置文件
			st = new SettingProvider(this);
			ls = new LRCService(this);
			ms = new MusicService(this);
			db = new DBProvider(this);
			sd = db.getWritableDatabase();
			hs = new HandlerService(this);
			py = new PYProvider();
			hs = new HandlerService(this);
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			wm = (WindowManager) getApplicationContext().getSystemService("window");
			layWM = new WindowManager.LayoutParams();
			fl = new FloatLRC(this); // 浮动歌词布局
			am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
			dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);

			// 清除上次程序运行的历史记录
			Editor edt = sp.edit();
			edt.putString("LastKeyword", "");
			edt.putBoolean("Started", true); // 是否启动标志，给Widget判断
			st.setLastKeyword("");
			st.setStarted(true);
			edt.commit();

			// 消息监听器
			ActionReceiver ar = new ActionReceiver(this);
			IntentFilter ittFilterButton = new IntentFilter(Intent.ACTION_MEDIA_BUTTON); // 控制键
			registerReceiver(ar, ittFilterButton);
			IntentFilter ittFilterPlug = new IntentFilter(Intent.ACTION_HEADSET_PLUG); // 耳机插拔
			registerReceiver(ar, ittFilterPlug);
			IntentFilter ittFilterBluetooth = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED); // 蓝牙断开
			registerReceiver(ar, ittFilterBluetooth);
			IntentFilter ittFilterLRCLock = new IntentFilter(IntentConst.INTENT_ACTION_FLOAT_LRC_LOCK); // 锁定歌词
			registerReceiver(ar, ittFilterLRCLock);
			IntentFilter ittFilterLRCUnlock = new IntentFilter(IntentConst.INTENT_ACTION_FLOAT_LRC_UNLOCK); // 解锁歌词
			registerReceiver(ar, ittFilterLRCUnlock);
			IntentFilter ittFilterNotifyNext = new IntentFilter(IntentConst.INTENT_ACTION_NOTIFICATION_NEXT); // 通知播放下一首
			registerReceiver(ar, ittFilterNotifyNext);
			IntentFilter ittFilterLRCShow = new IntentFilter(IntentConst.INTENT_ACTION_FLOAT_LRC_SHOW); // 显示浮动歌词
			registerReceiver(ar, ittFilterLRCShow);
			IntentFilter ittFilterLRCHide = new IntentFilter(IntentConst.INTENT_ACTION_FLOAT_LRC_HIDE); // 关闭浮动歌词
			registerReceiver(ar, ittFilterLRCHide);

			// 电话状态监听
			PhoneListener pl = new PhoneListener(this); // 我们派生的类
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			tm.listen(pl, PhoneStateListener.LISTEN_CALL_STATE);

			if (IsStartup && !IsRefreshing && !(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW))) // 如果外部调用则不刷新列表
			{
				SetMusicToList();

				// 初次安装发送设备信息到服务器
				new Thread()
				{
					public void run()
					{
						if (!main.getSp().getBoolean("SentInfo", false))
						{
							TelephonyManager tm = (TelephonyManager) main.getSystemService(Context.TELEPHONY_SERVICE); // 获取手机串号等信息并发送

							// 生成链接
							String strURL = "http://www.littledai.com/LiteListen/SetDevInfo.php?imei={imei}&locale={locale}&sdk={sdk}&release={release}&model={model}";
							strURL = strURL.replace("{imei}", java.net.URLEncoder.encode(tm.getDeviceId())).replace("{locale}",
									java.net.URLEncoder.encode(main.getResources().getConfiguration().locale.toString())).replace("{sdk}", java.net.URLEncoder.encode(Build.VERSION.SDK)).replace(
									"{release}", java.net.URLEncoder.encode(Build.VERSION.RELEASE)).replace("{model}", java.net.URLEncoder.encode(Build.MODEL));

							if (Common.CallURLPost(strURL, 10000))
							{// 打上成功标记
								Editor edt = main.getSp().edit();
								edt.putBoolean("SentInfo", true);
								edt.commit();
							}
						}
					}
				};
			}
			WidgetsListener();
			CreateFloatLRC();
			CallMusicNotify(getString(R.string.global_app_name), R.drawable.icon);
			CallFloatLRCNotify(st.getFloatLRCLocked());
			fl.SetLRC(R.drawable.album_normal, getString(R.string.global_app_name), Color.WHITE, getString(R.string.global_desk_lrc_show), Color.WHITE, null, 1);
			IsStartup = false;
		}
		else
		{
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			if (IsStartedUp)
				laySplash.setVisibility(View.GONE);
			SetCurrentTitle(ms.getStrShownTitle());

			// 设置播放/暂停按钮
			if (ms.getPlayerStatus() == MusicService.STATUS_PLAY)
			{
				btnPlay.setVisibility(View.GONE);
				btnPause.setVisibility(View.VISIBLE);
			}
			else
			{
				btnPlay.setVisibility(View.VISIBLE);
				btnPause.setVisibility(View.GONE);
			}

			if (CurrentShown == 0)
				lstMusic.setVisibility(View.VISIBLE);
			else
				lstMusic.setVisibility(View.GONE);

			if (ms.getPlayerStatus() == MusicService.STATUS_STOP)
				fl.SetLRC(R.drawable.icon, getString(R.string.global_app_name), Color.WHITE, getString(R.string.global_desk_lrc_show), Color.WHITE, null, 1);
			fl.setVisibility(View.INVISIBLE);
			layWM.width = dm.widthPixels;
		}

		// 设置音量条参数
		skbVolume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		skbVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		SetCurrentTitle(ms.getStrShownTitle());

		SetLanguage();
		SetMenuList();
		SetPlayMode();
		SetFonts();
		SetBackground();

		Editor edt = sp.edit();
		edt.putBoolean("IsRunBackground", false);
		st.setIsRunBackground(false);
		edt.commit();

		// 设置外部调用
		if (!IsPlayingExternal && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW))
		{
			laySplash.setVisibility(View.GONE); // 不显示启动画面
			String strMusicFilePath = Uri.parse(intent.getDataString()).getPath(); // 解析地址

			Map<String, Object> mapInfo = MusicTag.GetMusicInfo(main, strMusicFilePath, strMusicFilePath.substring(0, strMusicFilePath.lastIndexOf(".")));

			List<Map<String, Object>> lstSongTemp = new ArrayList<Map<String, Object>>(); // 播放列表
			lstSongTemp.add(mapInfo);
			lstSong = lstSongTemp;

			adapter = new MusicAdapter(scrMain.this, lstSong);
			lstMusic.setAdapter(adapter);
			ms.Play(0);
		}
		else if (!IsPlayingExternal)
		{
			if (IsRefreshing)
				lstMusic.setAdapter(adapter);
			else if (!IsStartup && !IsRefreshing)
				SetMusicListByDB();
		}

		RefreshLanguage();
		fl.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onNewIntent(Intent intent)
	{
		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW))
			IsPlayingExternal = true;
		else if (intent.getAction() != null && intent.getAction().equals(IntentConst.INTENT_ACTION_PREFERENCE_REFRESH))
			st.RefreshSettings(intent); // 刷新设置参数
	}

	/* 刷新语言线程 */
	public void RefreshLanguage()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(100);
				}
				catch (Exception e)
				{
					if (e.getMessage() != null)
						Log.w(Common.LOGCAT_TAG, e.getMessage());
					else
						e.printStackTrace();
				}
				hs.getHdlSetStartupLanguage().sendEmptyMessage(0);
			}
		}.start();
	}

	/* 创建浮动歌词秀 */
	public void CreateFloatLRC()
	{
		layWM.type = 2003; // 置于最顶层，一般为2002
		layWM.format = 1; // 透明背景
		if (st.getFloatLRCLocked())
			layWM.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		else
			layWM.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
		layWM.gravity = Gravity.LEFT | Gravity.TOP;
		layWM.x = 0;
		layWM.y = st.getFloatLRCPos();
		layWM.width = dm.widthPixels;
		layWM.height = 80;

		wm.addView(fl, layWM);
		fl.setVisibility(View.INVISIBLE);
	}

	/* 锁定/解锁歌词秀 */
	public void LockFloatLRC(boolean NeedLocked)
	{
		if (NeedLocked)
			layWM.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		else
			layWM.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;

		wm.updateViewLayout(fl, layWM);
		CallFloatLRCNotify(NeedLocked);

		Editor edt = sp.edit();
		edt.putBoolean("FloatLRCLocked", NeedLocked);
		st.setFloatLRCLocked(NeedLocked);
		edt.commit();
	}

	/* 显示音乐信息通知 */
	public void CallMusicNotify(String Title, int NotifyIconResource)
	{
		Intent intent = null;
		PendingIntent pdItent = null;
		Notification notification = null;

		if (st.getNotifyAction().equals("0"))
		{// 显示主界面
			intent = new Intent(this, scrMain.class);
			pdItent = PendingIntent.getActivity(this, 0, intent, 0);

			notification = new Notification(NotifyIconResource, Title, System.currentTimeMillis());
			notification.setLatestEventInfo(this, Title, getString(R.string.notification_show), pdItent);
		}
		else
		{// 播放下一首
			intent = new Intent(IntentConst.INTENT_ACTION_NOTIFICATION_NEXT);
			pdItent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			notification = new Notification(NotifyIconResource, Title, System.currentTimeMillis());
			notification.setLatestEventInfo(this, Title, getString(R.string.notification_play), pdItent);
		}

		notification.flags = Notification.FLAG_ONGOING_EVENT;
		nm.notify(MUSIC_NOTIFY_ID, notification);
	}

	/* 显示桌面歌词锁定通知 */
	public void CallFloatLRCNotify(boolean IsLocked)
	{
		Notification notification = null;
		Intent intent = null;
		PendingIntent pdItent = null;
		if (IsLocked)
		{
			intent = new Intent(IntentConst.INTENT_ACTION_FLOAT_LRC_UNLOCK);
			pdItent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification = new Notification(R.drawable.float_lrc_locked, getString(R.string.float_lrc_locked), System.currentTimeMillis());
			notification.setLatestEventInfo(this, getString(R.string.float_lrc), getString(R.string.float_lrc_unlock), pdItent);
			notification.flags = Notification.FLAG_ONGOING_EVENT;
		}
		else
		{
			intent = new Intent(IntentConst.INTENT_ACTION_FLOAT_LRC_LOCK);
			pdItent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification = new Notification(R.drawable.float_lrc_unlock, getString(R.string.float_lrc_unlocked), System.currentTimeMillis());
			notification.setLatestEventInfo(this, getString(R.string.float_lrc), getString(R.string.float_lrc_lock), pdItent);
			notification.flags = Notification.FLAG_ONGOING_EVENT;
		}

		nm.notify(LRC_NOTIFY_ID, notification);
	}

	/* 横竖屏切换不执行onCreate() */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.scr_main);
		onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();

		Editor edt = sp.edit();
		edt.putBoolean("IsRunBackground", true);
		st.setIsRunBackground(true);
		edt.commit();

		if (IsForceHideFloatLRC) // 强制隐藏
		{
			fl.setVisibility(View.INVISIBLE);
			IsForceHideFloatLRC = false; // 还原，每个操作仅能使用一次
		}
		else if (st.getDeskLRCStatus())
			fl.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		DirectionSwitch();
	}

	/* 刷新文件列表 */
	public void SetFileList(String path)
	{
		SelectedFileIndex = 0;
		txtCurrentPath.setText(path);
		File[] files = new File(path).listFiles();
		List<Map<String, String>> lstFileTemp = new ArrayList<Map<String, String>>();

		if (!path.equals("/sdcard"))
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("ShowPath", getString(R.string.scrmain_file_list));
			map.put("AbsolutePath", path.substring(0, path.lastIndexOf("/")));
			lstFileTemp.add(map);
		}

		for (int i = 0; i < files.length; i++)
		{
			// 忽略点文件
			if (files[i].getName().indexOf(".") == 0)
				continue;

			// 忽略其他类型的文件
			if (files[i].isFile() && !files[i].getName().substring(files[i].getName().length() - 4).toLowerCase().equals(".lrc"))
				continue;

			Map<String, String> map = new HashMap<String, String>();
			map.put("ShowPath", files[i].getName());
			map.put("AbsolutePath", files[i].getAbsolutePath());
			lstFileTemp.add(map);
		}

		lstLRCFile = lstFileTemp;
		fAdapter = new FileAdapterForMain(this, lstLRCFile);
		lstFile.setAdapter(fAdapter);
	}

	/* 将歌曲添加到列表 */
	public void SetMusicToList()
	{
		new Thread()
		{
			public void run()
			{
				IsRefreshing = true;
				List<String> lstFile = new ArrayList<String>();
				MusicFile.GetFiles(lstFile, st.getMusicPath(), ".mp3", st.getIncludeSubDirectory(), st.getIgnoreDirectory(), Long.parseLong(st.getIgnoreSize()) * 1024);
				MusicFile.GetFiles(lstFile, st.getMusicPath(), ".wma", st.getIncludeSubDirectory(), st.getIgnoreDirectory(), Long.parseLong(st.getIgnoreSize()) * 1024);
				lstSong = new ArrayList<Map<String, Object>>();

				if (lstFile.size() > 0)
				{
					VerifyCode = Common.GetRandomIndex(111111, 999999); // 获取本次列表刷新时的校验码

					// 将整个数据库操作当作一个事物
					sd.beginTransaction();
					for (int i = 0; i < lstFile.size(); i++)
					{
						String strFileName = (String) lstFile.get(i).substring(0, lstFile.get(i).lastIndexOf("."));

						Map<String, Object> mapInfo = new HashMap<String, Object>();
						mapInfo.put("MusicPath", lstFile.get(i));
						mapInfo.put("LRCPath", lstFile.get(i).substring(0, lstFile.get(i).lastIndexOf(".")) + ".lrc");
						mapInfo.put("Title", strFileName.substring(strFileName.lastIndexOf("/") + 1));
						mapInfo.put("ID3Checked", "0");

						String strMusicPath = lstFile.get(i).trim();
						if (strMusicPath != null && strMusicPath.indexOf("'") != -1)
							strMusicPath = strMusicPath.replace("'", "''");

						Cursor cur = sd.query("music_info", null, "id3_checked='1' and music_path='" + strMusicPath + "'", null, null, null, null);
						if (cur.moveToFirst())
						{
							mapInfo.put("Title", cur.getString(0));
							mapInfo.put("SongInfo", cur.getString(13));
							mapInfo.put("MusicPath", strMusicPath);
							mapInfo.put("LRCPath", cur.getString(12));
							mapInfo.put("Artist", cur.getString(1));
							mapInfo.put("Album", cur.getString(2));
							mapInfo.put("Genre", cur.getString(4));
							mapInfo.put("Year", cur.getString(3));
							mapInfo.put("Track", cur.getString(5));
							mapInfo.put("ID3Checked", "1");

							sd.execSQL("update music_info set verify_code='" + VerifyCode + "' where music_path='" + strMusicPath + "';");
						}
						cur.close();
						lstSong.add(mapInfo);
					}
					sd.setTransactionSuccessful(); // 设置事物处理成功标志，否则会回滚
					sd.endTransaction(); // 处理完成

					hs.getHdlAdapterBinding().sendEmptyMessage(0);
				}

				IsStartedUp = true;
				hs.getHdlShowMain().sendEmptyMessage(0);
				hs.getHdlShowUpdateLog().sendEmptyMessage(0);
				hs.getHdlCheckForUpdate().sendEmptyMessage(0);
			}
		}.start();
	}

	/* 更新ID3标签信息 */
	public void RefreshID3()
	{
		new Thread()
		{
			public void run()
			{
				sd.beginTransaction();
				for (int i = 0; i < lstSong.size(); i++)
				{
					Map<String, Object> mapInfo = new HashMap<String, Object>();
					mapInfo = lstSong.get(i);
					String strMusicPath = (String) mapInfo.get("MusicPath");
					String strID3Check = (String) mapInfo.get("ID3Checked");
					if (strID3Check != null && strID3Check.equals("0"))
					{
						mapInfo = MusicTag.GetMusicInfo(main, strMusicPath, strMusicPath.substring(0, strMusicPath.lastIndexOf("."))); // 获取读到的MP3属性

						// 更新数据库
						String strTitle = (String) mapInfo.get("Title");
						if (strTitle != null)
						{
							strTitle = strTitle.trim();
							if (strTitle.indexOf("'") != -1)
								strTitle = strTitle.replace("'", "''").trim();
						}

						String strArtist = (String) mapInfo.get("Artist");
						if (strArtist != null)
						{
							strArtist = strArtist.trim();
							if (strArtist.indexOf("'") != -1)
								strArtist = strArtist.replace("'", "''").trim();
						}

						String strAlbum = (String) mapInfo.get("Album");
						if (strAlbum != null)
						{
							strAlbum = strAlbum.trim();
							if (strAlbum.indexOf("'") != -1)
								strAlbum = strAlbum.replace("'", "''").trim();
						}

						String strYear = (String) mapInfo.get("Year");
						if (strYear != null)
						{
							strYear = strYear.trim();
							if (strYear.indexOf("'") != -1)
								strYear = strYear.replace("'", "''").trim();
						}

						String strGenre = (String) mapInfo.get("Genre");
						if (strGenre != null)
						{
							strGenre = strGenre.trim();
							if (strGenre.indexOf("'") != -1)
								strGenre = strGenre.replace("'", "''").trim();
						}

						String strTrack = (String) mapInfo.get("Track");
						if (strTrack != null)
						{
							strTrack = strTrack.trim();
							if (strTrack.indexOf("'") != -1)
								strTrack = strTrack.replace("'", "''").trim();
						}

						String strComment = (String) mapInfo.get("Comment");
						if (strComment != null)
						{
							strComment = strComment.trim();
							if (strComment.indexOf("'") != -1)
								strComment = strComment.replace("'", "''").trim();
						}

						strMusicPath = (String) mapInfo.get("MusicPath");
						if (strMusicPath != null)
						{
							strMusicPath = strMusicPath.trim();
							if (strMusicPath.indexOf("'") != -1)
								strMusicPath = strMusicPath.replace("'", "''").trim();
						}

						String strLRCPath = (String) mapInfo.get("LRCPath");
						if (strLRCPath != null)
						{
							strLRCPath = strLRCPath.trim();
							if (strLRCPath.indexOf("'") != -1)
								strLRCPath = strLRCPath.replace("'", "''").trim();
						}

						String strSongInfo = (String) mapInfo.get("SongInfo");
						if (strSongInfo != null)
						{
							strSongInfo = strSongInfo.trim();
							if (strSongInfo.indexOf("'") != -1)
								strSongInfo = strSongInfo.replace("'", "''").trim();
						}

						try
						{// 插表的时候可能会出现字段中的非法字符
							sd.execSQL("delete from music_info where music_path='" + strMusicPath + "'");
							sd.execSQL("insert into music_info values('" + strTitle + "','" + strArtist + "','" + strAlbum + "','" + strYear + "','" + strGenre + "','" + strTrack + "','" + strComment
									+ "','" + py.GetPYFull(strTitle) + "','" + py.GetPYSimple(py.GetPYFull(strTitle)) + "','" + py.GetPYFull(strArtist) + "','"
									+ py.GetPYSimple(py.GetPYFull(strArtist)) + "','" + strMusicPath + "','" + strLRCPath + "','" + strSongInfo + "','0','0','" + (String) mapInfo.get("ID3Checked")
									+ "','" + VerifyCode + "');");
						}
						catch (Exception e)
						{
							if (e.getMessage() != null)
								Log.w(Common.LOGCAT_TAG, e.getMessage());
							else
								e.printStackTrace();
						}

						lstSong.set(i, mapInfo);
					}

					hs.getHdlRefreshAdapter().sendEmptyMessage(0);
				}

				sd.setTransactionSuccessful(); // 设置事物处理成功标志，否则会回滚
				sd.endTransaction(); // 处理完成

				SetMusicListByDB();
				IsRefreshing = false;
			}
		}.start();
	}

	/* 从数据库获取歌曲信息 */
	public void SetMusicListByDB()
	{
		new Thread()
		{
			public void run()
			{
				sd.execSQL("delete from music_info where verify_code<>'" + VerifyCode + "';");

				Cursor cur = null;
				List<Map<String, Object>> lstSongTemp = new ArrayList<Map<String, Object>>(); // 用局部变量去接收map中的数据，否则会报错
				String Keyword = st.getLastKeyword(); // 上次搜索的关键词
				String index = st.getListSortOrder();
				String strOrderBy = st.getOrderBy(); // 决定排序方式

				String strParOrderBy = "";

				if (index.equals("0"))
					strParOrderBy = "title_simple_py " + strOrderBy + ", artist_simple_py " + strOrderBy;
				else if (index.equals("1"))
					strParOrderBy = "artist_simple_py " + strOrderBy + ", title_simple_py " + strOrderBy;
				else if (index.equals("2"))
					strParOrderBy = "";

				if (IsShowingFavourite)
					strParOrderBy = "play_times desc, " + strParOrderBy;

				cur = sd.query("music_info", null, "title like '%" + Keyword + "%' or artist like '%" + Keyword + "%' or album like '%" + Keyword + "%' or year like '%" + Keyword
						+ "%' or genre like '%" + Keyword + "%' or comment like '%" + Keyword + "%' or title_py like '%" + Keyword + "%' or title_simple_py like '%" + Keyword
						+ "%' or artist_py like '%" + Keyword + "%' or artist_simple_py like '%" + Keyword + "%' or song_info like '%" + Keyword + "%'", null, null, null, strParOrderBy);

				int i = 0; // 游标计数器
				while (cur.moveToNext())
				{
					if (IsShowingFavourite)
					{// 最爱歌曲
						if (i >= Integer.parseInt(st.getFavoriteMax()))
							break;
						else
							i++;
					}

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
					lstSongTemp.add(mapItem);
				}

				cur.close();
				lstSong = lstSongTemp; // 将局部变量赋值给全局变量

				adapter = new MusicAdapter(scrMain.this, lstSong);
				Message msg = new Message();
				msg.obj = adapter;
				hs.getHdlAdapterUpdateHandler().sendMessage(msg);
				IsStartup = false;
			}
		}.start();
	}

	/* 设置专辑图标 */
	public void SetAlbumIcon()
	{
		// 防止初始化或列表为空时出错
		if (adapter == null || ms == null || ms.getCurrIndex() >= adapter.getCount())
			return;

		adapter.getView(ms.getCurrIndex(), null, lstMusic);
		adapter.notifyDataSetChanged();

		// 如果不在显示范围内，将当前播放的歌曲显示在第一位
		if (ms.getPlayerStatus() == MusicService.STATUS_PLAY)
		{
			if (ms.getCurrIndex() < lstMusic.getFirstVisiblePosition() || ms.getCurrIndex() > lstMusic.getLastVisiblePosition())
				lstMusic.setSelectionFromTop(ms.getCurrIndex(), 0); // 恢复刚才的位置
		}
	}

	/* 设置播放模式 */
	public void SetPlayMode()
	{
		String index = st.getPlayMode(); // 0－顺序播放；1－全部循环；2－单曲暂停；3－单曲循环；4－随机播放

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
		String index = st.getLanguage();

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
			String index = st.getBackgroundLand();
			File f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_land.png");
			if (index.equals("1") && f.isFile() && f.exists())
			{
				bmpBackground = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_land.png");
				bmpBackground = ImageEffect.CombinePictures(bmpBackground, ImageEffect.GetBitmap(getResources(), R.drawable.bg_land_default_framework), 800, 480, 0, 0, 240);
				layActivity.setBackgroundDrawable(ImageEffect.GetDrawable(bmpBackground));
			}
			else
				layActivity.setBackgroundResource(R.drawable.bg_land_default);
		}
		else
		{
			String index = st.getBackgroundPort();
			File f = new File(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_port.png");
			if (index.equals("1") && f.isFile() && f.exists())
			{
				bmpBackground = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/LiteListen/" + "background_port.png");
				bmpBackground = ImageEffect.CombinePictures(bmpBackground, ImageEffect.GetBitmap(getResources(), R.drawable.bg_port_default_framework), 480, 800, 0, 0, 240);
				layActivity.setBackgroundDrawable(ImageEffect.GetDrawable(bmpBackground));
			}
			else
				layActivity.setBackgroundResource(R.drawable.bg_port_default);
		}
	}

	/* 设置字体 */
	public void SetFonts()
	{
		txtLRC.setTextSize(Float.parseFloat(st.getLRCFontSize()));
		txtLRC.setTextColor(Color.parseColor(st.getLRCFontColorNormal()));
		if (st.getLRCFontShadow())
			txtLRC.setShadowLayer(1, 1, 1, Color.parseColor(st.getLRCFontShadowColor()));
		else
			txtLRC.setShadowLayer(1, 1, 1, Color.TRANSPARENT);
	}

	/* 获取控件实体 */
	public void FindViews()
	{
		btnLast = (ImageButton) findViewById(R.id.btnLast);
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPause = (ImageButton) findViewById(R.id.btnPause);
		btnPlayMode = (ImageButton) findViewById(R.id.btnPlayMode);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnFileOK = (Button) findViewById(R.id.btnFileOK);
		btnFileCancel = (Button) findViewById(R.id.btnFileCancel);
		btnLRC = (ImageButton) findViewById(R.id.btnLRC);
		btnVolume = (ImageButton) findViewById(R.id.btnVolume);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTimeTotal = (TextView) findViewById(R.id.txtTimeTotal);
		txtTimeCurrent = (TextView) findViewById(R.id.txtTimeCurrent);
		txtLRC = (TextView) findViewById(R.id.txtLRC);
		txtKeyword = (TextView) findViewById(R.id.txtKeyword);
		txtCurrentPath = (TextView) findViewById(R.id.txtCurrentPath);
		layActivity = (LinearLayout) findViewById(R.id.layActivity);
		laySplash = (LinearLayout) findViewById(R.id.laySplash);
		layControlPanel = (LinearLayout) findViewById(R.id.layControlPanel);
		laySearch = (RelativeLayout) findViewById(R.id.laySearch);
		layMain = (RelativeLayout) findViewById(R.id.layMain);
		layBody = (RelativeLayout) findViewById(R.id.layBody);
		layFileSelector = (RelativeLayout) findViewById(R.id.layFileSelector);
		skbMusic = (SeekBar) findViewById(R.id.skbMusic);
		skbVolume = (SeekBar) findViewById(R.id.skbVolume);
		lstMusic = (ListView) findViewById(R.id.lstMusic);
		lstFile = (ListView) findViewById(R.id.lstFile);
		grdMenu = (GridView) findViewById(R.id.grdMenu);
		layLyricController = (LinearLayout) findViewById(R.id.layLyricController);
	}

	/* 设置菜单列表 */
	public void SetMenuList()
	{
		List<Map<String, Object>> lstMenuItem = new ArrayList<Map<String, Object>>(); // 菜单功能列表
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_settings);
		map.put("ItemText", getString(R.string.scrmain_extend_menu_settings));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_desk_lrc);
		if (st.getDeskLRCStatus())
		{
			map.put("ItemText", getString(R.string.scrmain_extend_menu_desk_lrc_hide));
			fl.setVisibility(View.VISIBLE);
		}
		else
		{
			map.put("ItemText", getString(R.string.scrmain_extend_menu_desk_lrc_show));
			fl.setVisibility(View.INVISIBLE);
			nm.cancel(LRC_NOTIFY_ID);
		}
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_keep_screen_on);
		if (st.getKeepScreenOn())
		{
			map.put("ItemText", getString(R.string.scrmain_extend_menu_keep_screen_on_false));
			layActivity.setKeepScreenOn(true);
			fl.setKeepScreenOn(true);
		}
		else
		{
			map.put("ItemText", getString(R.string.scrmain_extend_menu_keep_screen_on_true));
			layActivity.setKeepScreenOn(false);
			fl.setKeepScreenOn(false);
		}
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_search);
		map.put("ItemText", getString(R.string.scrmain_extend_menu_search));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		if (st.getOrderBy().equals("asc"))
		{
			map.put("ItemIcon", R.drawable.menu_order_desc);
			map.put("ItemText", getString(R.string.scrmain_extend_menu_order_desc));
		}
		else
		{
			map.put("ItemIcon", R.drawable.menu_order_asc);
			map.put("ItemText", getString(R.string.scrmain_extend_menu_order_asc));
		}
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_feedback);
		map.put("ItemText", getString(R.string.scrmain_extend_menu_feedback));
		lstMenuItem.add(map);

		map = new HashMap<String, Object>();
		if (IsShowingFavourite)
		{// 最爱
			map.put("ItemIcon", R.drawable.menu_list);
			map.put("ItemText", getString(R.string.scrmain_extend_menu_list));
		}
		else
		{// 列表
			map.put("ItemIcon", R.drawable.menu_favourite);
			map.put("ItemText", getString(R.string.scrmain_extend_menu_favourite));
		}
		lstMenuItem.add(map);

		// 横屏多一项菜单
		if (ScreenOrantation == 1 || ScreenOrantation == 3)
		{
			map = new HashMap<String, Object>();
			map.put("ItemIcon", R.drawable.menu_wait);
			map.put("ItemText", getString(R.string.scrmain_extend_menu_wait));
			lstMenuItem.add(map);
		}

		map = new HashMap<String, Object>();
		map.put("ItemIcon", R.drawable.menu_exit);
		map.put("ItemText", getString(R.string.scrmain_extend_menu_exit));
		lstMenuItem.add(map);

		SimpleAdapter adapter = new SimpleAdapter(this, lstMenuItem, R.layout.grid_menu, new String[] { "ItemIcon", "ItemText" }, new int[] { R.id.imgMenu, R.id.txtMenu });
		grdMenu.setAdapter(adapter);
	}

	/* 搜索框切换 */
	public void SearchBoxSwitcher()
	{
		Animation anim = null; // 动画效果

		if (laySearch.getVisibility() == View.GONE)
		{// 调用显示
			laySearch.setVisibility(View.VISIBLE);
			anim = new AlphaAnimation(0, 1);
		}
		else
		{// 调用隐藏
			laySearch.setVisibility(View.GONE);
			anim = new AlphaAnimation(1, 0);
		}

		anim.setDuration(ANIMATION_TIME);
		if (st.getUseAnimation())
			laySearch.startAnimation(anim);
	}

	/* 音量框切换 */
	public void VolumeBoxSwitcher()
	{
		Animation animShow = new AlphaAnimation(0, 1);
		Animation animHide = new AlphaAnimation(1, 0);
		animShow.setDuration(ANIMATION_TIME);
		animHide.setDuration(ANIMATION_TIME);

		if (skbVolume.getVisibility() == View.GONE)
		{// 调用显示
			if (ScreenOrantation == 1 || ScreenOrantation == 3)
				layControlPanel.setVisibility(View.GONE);
			skbVolume.setVisibility(View.VISIBLE);

			if (st.getUseAnimation())
			{
				skbVolume.startAnimation(animShow);
				if (ScreenOrantation == 1 || ScreenOrantation == 3)
					layControlPanel.startAnimation(animHide);
			}
		}
		else
		{// 调用隐藏
			if (ScreenOrantation == 1 || ScreenOrantation == 3)
				layControlPanel.setVisibility(View.VISIBLE);
			skbVolume.setVisibility(View.GONE);

			if (st.getUseAnimation())
			{
				skbVolume.startAnimation(animHide);
				if (ScreenOrantation == 1 || ScreenOrantation == 3)
					layControlPanel.startAnimation(animShow);
			}
		}
	}

	/* 列表到歌词切换 */
	public void List2LRCSwitcher()
	{
		if (CurrentShown == 0)
		{
			txtLRC.setVisibility(View.VISIBLE);
			layLyricController.setVisibility(View.VISIBLE);
			lstMusic.setVisibility(View.GONE);
			layFileSelector.setVisibility(View.GONE);

			CurrentShown = 1;

			if (st.getUseAnimation())
			{
				Animation animShow = new TranslateAnimation(dm.widthPixels, 0, 0, 0);
				animShow.setDuration(ANIMATION_TIME);
				animShow.setInterpolator(new DecelerateInterpolator());

				Animation animHide = new TranslateAnimation(0, -dm.widthPixels, 0, 0);
				animHide.setDuration(ANIMATION_TIME);
				animHide.setInterpolator(new DecelerateInterpolator());

				layLyricController.startAnimation(animShow);
				lstMusic.startAnimation(animHide);
			}
		}
	}

	/* 歌词到列表切换 */
	public void LRC2ListSwitcher()
	{
		if (CurrentShown == 1)
		{
			txtLRC.setVisibility(View.GONE);
			layLyricController.setVisibility(View.GONE);
			lstMusic.setVisibility(View.VISIBLE);
			layFileSelector.setVisibility(View.GONE);

			CurrentShown = 0;

			if (st.getUseAnimation())
			{
				Animation animShow = new TranslateAnimation(-dm.widthPixels, 0, 0, 0);
				animShow.setDuration(ANIMATION_TIME);
				animShow.setInterpolator(new DecelerateInterpolator());

				Animation animHide = new TranslateAnimation(0, dm.widthPixels, 0, 0);
				animHide.setDuration(ANIMATION_TIME);
				animHide.setInterpolator(new DecelerateInterpolator());

				layLyricController.startAnimation(animHide);
				lstMusic.startAnimation(animShow);
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
				String index = st.getPlayMode(); // 0－顺序播放；1－全部循环；2－单曲暂停；3－单曲循环；4－随机播放
				Editor edt = sp.edit();

				if (index.equals("0"))
				{
					edt.putString("PlayMode", "1");
					st.setPlayMode("1");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_repeat_all);

					if (toast != null)
					{
						toast.setText(R.string.scrmain_play_mode_repeat_all);
						toast.setDuration(Toast.LENGTH_SHORT);
					}
					else
						toast = Toast.makeText(scrMain.this, R.string.scrmain_play_mode_repeat_all, Toast.LENGTH_SHORT);
				}
				else if (index.equals("1"))
				{
					edt.putString("PlayMode", "2");
					st.setPlayMode("2");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_pause_current);

					if (toast != null)
					{
						toast.setText(R.string.scrmain_play_mode_pause_current);
						toast.setDuration(Toast.LENGTH_SHORT);
					}
					else
						toast = Toast.makeText(scrMain.this, R.string.scrmain_play_mode_pause_current, Toast.LENGTH_SHORT);
				}
				else if (index.equals("2"))
				{
					edt.putString("PlayMode", "3");
					st.setPlayMode("3");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_repeat_current);

					if (toast != null)
					{
						toast.setText(R.string.scrmain_play_mode_repeat_current);
						toast.setDuration(Toast.LENGTH_SHORT);
					}
					else
						toast = Toast.makeText(scrMain.this, R.string.scrmain_play_mode_repeat_current, Toast.LENGTH_SHORT);
				}
				else if (index.equals("3"))
				{
					edt.putString("PlayMode", "4");
					st.setPlayMode("4");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_shuffle);

					if (toast != null)
					{
						toast.setText(R.string.scrmain_play_mode_shuffle);
						toast.setDuration(Toast.LENGTH_SHORT);
					}
					else
						toast = Toast.makeText(scrMain.this, R.string.scrmain_play_mode_shuffle, Toast.LENGTH_SHORT);
				}
				else if (index.equals("4"))
				{
					edt.putString("PlayMode", "0");
					st.setPlayMode("0");
					btnPlayMode.setBackgroundResource(R.drawable.btn_play_mode_close);

					if (toast != null)
					{
						toast.setText(R.string.scrmain_play_mode_close);
						toast.setDuration(Toast.LENGTH_SHORT);
					}
					else
						toast = Toast.makeText(scrMain.this, R.string.scrmain_play_mode_close, Toast.LENGTH_SHORT);
				}

				edt.commit();
				toast.show();
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

		/* 音量控制 */
		btnVolume.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				VolumeBoxSwitcher();
			}
		});

		/* 文件列表确定 */
		btnFileOK.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Map<String, String> map = new HashMap<String, String>();
				map = lstLRCFile.get(SelectedFileIndex);
				String strPath = map.get("AbsolutePath");
				File f = new File(strPath);
				if (f.isDirectory()) // 进入目录
					SetFileList(strPath);
				else
				{
					Map<String, Object> mapMusic = new HashMap<String, Object>();
					mapMusic = lstSong.get(ms.getCurrIndex());
					sd.execSQL("update music_info set lrc_path='" + strPath + "' where music_path='" + (String) mapMusic.get("MusicPath") + "';");
					ls.setStrLRCPath(strPath); // 设置新的歌词

					// 更新列表中的歌词路径
					mapMusic.put("LRCPath", strPath);
					lstSong.set(ms.getCurrIndex(), mapMusic);

					txtLRC.setVisibility(View.VISIBLE); // 关闭自己
				}
			}
		});

		/* 文件列表取消 */
		btnFileCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				txtLRC.setVisibility(View.VISIBLE);
			}
		});

		/* 搜索按钮 */
		btnSearch.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (IsRefreshing)
				{
					final MessageDialog md = new MessageDialog();
					md.ShowMessage(scrMain.this, layActivity, getString(R.string.global_wait), getString(R.string.scrmain_scanning), 18, new OnClickListener()
					{
						public void onClick(View v)
						{
							md.CloseDialog();
						}
					}, null);
				}
				else
				{
					Editor edt = sp.edit();
					edt.putString("LastKeyword", txtKeyword.getText().toString());
					st.setLastKeyword(txtKeyword.getText().toString());
					edt.commit();

					SetMusicListByDB();
					txtKeyword.setText("");
					SearchBoxSwitcher();
					txtKeyword.clearFocus();
				}
			}
		});

		/* 音量滑块 */
		skbVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				SetCurrentTitle(ms.getStrShownTitle()); // 还原标题
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

				if (!IsStartup)
				{
					if (progress == 0)
						SetCurrentTitle(getString(R.string.scrmain_volume_mute)); // 显示静音
					else
						SetCurrentTitle(getString(R.string.scrmain_volume) + progress); // 暂时显示音量
				}
			}
		});

		/* 显示歌词关联菜单 */
		txtLRC.setOnLongClickListener(new OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				// 拖动歌词时不显示菜单
				if (IsLRCMoved)
					return false;

				OptionDialog.ShowDialog(scrMain.this, layActivity, getString(R.string.scrmain_context_menu_lrc), getResources().getStringArray(R.array.item_name_txtlrc_context_menu), 18, -1,
						new OnClickListener()
						{
							public void onClick(View v)
							{
								if (OptionDialog.getRet().equals("0"))
								{
									// 修改歌词关联
									if (ms.getCurrIndex() > lstSong.size() || lstSong.size() == 0)
									{
										final MessageDialog md = new MessageDialog();
										md.ShowMessage(scrMain.this, layActivity, getString(R.string.scrmain_context_menu_lrc), getString(R.string.scrmain_lyric_could_not_relate), 18,
												new OnClickListener()
												{
													public void onClick(View v)
													{
														md.CloseDialog();
													}
												}, null);
									}
									else
									{
										txtLRC.setVisibility(View.GONE);
										layLyricController.setVisibility(View.VISIBLE);
										lstMusic.setVisibility(View.GONE);
										layFileSelector.setVisibility(View.VISIBLE);
										SetFileList("/sdcard");
									}
								}
								else if (OptionDialog.getRet().equals("1"))
								{// 用线程下载歌词
									if (ms.getCurrIndex() > lstSong.size() || lstSong.size() == 0)
									{
										final MessageDialog md = new MessageDialog();
										md.ShowMessage(scrMain.this, layActivity, getString(R.string.scrmain_context_menu_lrc), getString(R.string.scrmain_lyric_could_not_relate), 18,
												new OnClickListener()
												{
													public void onClick(View v)
													{
														md.CloseDialog();
													}
												}, null);
									}
									else
									{
										new Thread()
										{
											public void run()
											{
												ls.GetCurrLyric();
											}
										}.start();
									}
								}

								OptionDialog.getPw().dismiss();
							}
						});

				return false;
			}
		});

		/* 选中列表 */
		lstMusic.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				SelectedItemIndex = arg2; // 更新当前选中的序号

				adapter.getView(arg2, null, lstMusic);
				adapter.notifyDataSetChanged();
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
						startActivity(new Intent(scrMain.this, scrSettings.class));
						break;
					case 1:
						TextView txtDeskLyric = (TextView) arg1.findViewById(R.id.txtMenu);
						if (st.getDeskLRCStatus())
						{
							txtDeskLyric.setText(R.string.scrmain_extend_menu_desk_lrc_show);
							Editor edt = sp.edit();
							edt.putBoolean("DeskLRCStatus", false);
							st.setDeskLRCStatus(false);
							edt.commit();
							nm.cancel(LRC_NOTIFY_ID);

							if (toast != null)
							{
								toast.setText(R.string.float_lrc_deactivation);
								toast.setDuration(Toast.LENGTH_SHORT);
							}
							else
								toast = Toast.makeText(scrMain.this, R.string.float_lrc_deactivation, Toast.LENGTH_SHORT);
						}
						else
						{
							txtDeskLyric.setText(R.string.scrmain_extend_menu_desk_lrc_hide);
							Editor edt = sp.edit();
							edt.putBoolean("DeskLRCStatus", true);
							st.setDeskLRCStatus(true);
							edt.commit();
							CallFloatLRCNotify(st.getFloatLRCLocked());

							if (toast != null)
							{
								toast.setText(R.string.float_lrc_activiation);
								toast.setDuration(Toast.LENGTH_SHORT);
							}
							else
								toast = Toast.makeText(scrMain.this, R.string.float_lrc_activiation, Toast.LENGTH_SHORT);
						}
						toast.show();

						break;
					case 2:
						TextView txtScrOn = (TextView) arg1.findViewById(R.id.txtMenu);

						if (!st.getKeepScreenOn())
						{
							layActivity.setKeepScreenOn(true);
							txtScrOn.setText(R.string.scrmain_extend_menu_keep_screen_on_false);
							Editor edt = sp.edit();
							edt.putBoolean("KeepScreenOn", true);
							st.setKeepScreenOn(true);
							edt.commit();

							if (toast != null)
							{
								toast.setText(R.string.scrmain_extend_menu_keep_screen_on_true);
								toast.setDuration(Toast.LENGTH_SHORT);
							}
							else
								toast = Toast.makeText(scrMain.this, R.string.scrmain_extend_menu_keep_screen_on_true, Toast.LENGTH_SHORT);
						}
						else
						{
							layActivity.setKeepScreenOn(false);
							txtScrOn.setText(R.string.scrmain_extend_menu_keep_screen_on_true);
							Editor edt = sp.edit();
							edt.putBoolean("KeepScreenOn", false);
							st.setKeepScreenOn(false);
							edt.commit();

							if (toast != null)
							{
								toast.setText(R.string.scrmain_extend_menu_keep_screen_on_false);
								toast.setDuration(Toast.LENGTH_SHORT);
							}
							else
								toast = Toast.makeText(scrMain.this, R.string.scrmain_extend_menu_keep_screen_on_false, Toast.LENGTH_SHORT);
						}
						toast.show();

						break;
					case 3:
						SearchBoxSwitcher();
						break;
					case 4:
						TextView txtOrder = (TextView) arg1.findViewById(R.id.txtMenu);
						ImageView imgMenu = (ImageView) arg1.findViewById(R.id.imgMenu);
						if (st.getOrderBy().equals("asc"))
						{
							txtOrder.setText(R.string.scrmain_extend_menu_order_asc);
							imgMenu.setImageResource(R.drawable.menu_order_asc);
							Editor edt = sp.edit();
							edt.putString("OrderBy", "desc");
							st.setOrderBy("desc");
							edt.commit();
						}
						else
						{
							txtOrder.setText(R.string.scrmain_extend_menu_order_desc);
							imgMenu.setImageResource(R.drawable.menu_order_desc);
							Editor edt = sp.edit();
							edt.putString("OrderBy", "asc");
							st.setOrderBy("asc");
							edt.commit();
						}

						SetMusicListByDB();

						break;
					case 5:
						TextDialog.ShowMessage(scrMain.this, layActivity, getString(R.string.scrmain_extend_menu_feedback), getString(R.string.scrmain_feedback_hint), 15, "", 18,
								new OnClickListener()
								{
									public void onClick(View v)
									{
										String strMessage = TextDialog.getEdtMessage().getText().toString().trim();

										if (strMessage != null && !strMessage.equals(""))
										{
											// 获取手机串号等信息并发送
											TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

											// 获取当前时间
											java.util.Date date = new java.util.Date();
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
											String strDateTime = sdf.format(date);

											String strURL = "http://www.littledai.com/LiteListen/SendTicket.php?imei={imei}&locale={locale}&sdk={sdk}&release={release}&model={model}&message={message}&submit_time={submit_time}";
											strURL = strURL.replace("{imei}", java.net.URLEncoder.encode(tm.getDeviceId())).replace("{locale}",
													java.net.URLEncoder.encode(getResources().getConfiguration().locale.toString())).replace("{sdk}", java.net.URLEncoder.encode(Build.VERSION.SDK))
													.replace("{release}", java.net.URLEncoder.encode(Build.VERSION.RELEASE)).replace("{model}", java.net.URLEncoder.encode(Build.MODEL)).replace(
															"{message}", java.net.URLEncoder.encode(strMessage)).replace("{submit_time}", java.net.URLEncoder.encode(strDateTime)); // 将变量转换成URL格式

											if (toast != null)
											{
												toast.setText(getString(R.string.scrmain_feedback_successful));
												toast.setDuration(Toast.LENGTH_SHORT);
											}
											else
												toast = Toast.makeText(scrMain.this, getString(R.string.scrmain_feedback_successful), Toast.LENGTH_SHORT);

											String strHint = getString(R.string.scrmain_feedback_successful);

											if (Common.CallURLPost(strURL, 10000))
												TextDialog.getPw().dismiss(); // 成功后关闭对话框
											else
												strHint = getString(R.string.scrmain_feedback_failure);

											if (toast != null)
											{
												toast.setText(strHint);
												toast.setDuration(Toast.LENGTH_SHORT);
											}
											else
												toast = Toast.makeText(scrMain.this, strHint, Toast.LENGTH_SHORT);
										}
										else
										{
											if (toast != null)
											{
												toast.setText(getString(R.string.scrmain_feedback_blank));
												toast.setDuration(Toast.LENGTH_SHORT);
											}
											else
												toast = Toast.makeText(scrMain.this, getString(R.string.scrmain_feedback_blank), Toast.LENGTH_SHORT);
										}

										toast.show();
									}
								});

						break;
					case 6:
						TextView txtFavourite = (TextView) arg1.findViewById(R.id.txtMenu);
						ImageView imgFavourite = (ImageView) arg1.findViewById(R.id.imgMenu);

						if (IsShowingFavourite)
						{// 最爱-->列表
							txtFavourite.setText(R.string.scrmain_extend_menu_favourite);
							imgFavourite.setImageResource(R.drawable.menu_favourite);

							IsShowingFavourite = false;
						}
						else
						{// 列表-->最爱
							txtFavourite.setText(R.string.scrmain_extend_menu_list);
							imgFavourite.setImageResource(R.drawable.menu_list);

							IsShowingFavourite = true;
						}

						SetMusicListByDB();

						break;
					case 7:
						if (ScreenOrantation == 1 || ScreenOrantation == 3)
							;
						else
						{
							Editor edt = sp.edit();
							edt.putString("LastKeyword", "");
							edt.putBoolean("Started", false); // 是否启动标志，给Widget判断
							st.setLastKeyword("");
							st.setStarted(false);
							edt.commit();
							nm.cancelAll();
							System.exit(0);

							break;
						}
					case 8:
						if (ScreenOrantation == 1 || ScreenOrantation == 3)
						{
							Editor edt = sp.edit();
							edt.putString("LastKeyword", "");
							edt.putBoolean("Started", false); // 是否启动标志，给Widget判断
							st.setLastKeyword("");
							st.setStarted(false);
							edt.commit();
							nm.cancelAll();
							System.exit(0);
						}
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
			boolean Switch2List = false; // 是否需要将歌词切换到播放列表

			// 手指按下时的坐标
			float DownPosX = -1;
			float DownPosY = -1;

			boolean ReadyToZoom = false; // 是否准备缩放
			double StartDistance; // 初始指距
			int StartHeight; // 初始控件高度

			// 获取指距
			private float GetFingerDistance(float PosX1, float PosY1, float PosX2, float PosY2)
			{
				return (float) Math.sqrt((PosX1 - PosX2) * (PosX1 - PosX2) + (PosY1 - PosY2) * (PosY1 - PosY2));
			}

			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getPointerCount() == 2)
				{
					if (ReadyToZoom == false)
					{
						StartDistance = GetFingerDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
						ReadyToZoom = true;
						StartHeight = txtLRC.getLineHeight() * Common.GetStringLines(txtLRC.getText().toString(), (float) (txtLRC.getTextSize() / 1.5), dm.widthPixels);
					}
					else if (event.getAction() == MotionEvent.ACTION_MOVE)
					{
						ls.setCanRefreshLRC(false);
						IsLRCMoved = true; // Move过的标记

						double distance = GetFingerDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
						txtLRC.setTextSize((float) (txtLRC.getTextSize() / 1.5 * distance / StartDistance)); // 除以1.5的像素密度
						if (txtLRC.getTextSize() / 1.5 > 35.0f)
							txtLRC.setTextSize(35);
						if (txtLRC.getTextSize() / 1.5 < 18.0f)
							txtLRC.setTextSize(18);

						LinearLayout.LayoutParams layLRC = (LinearLayout.LayoutParams) txtLRC.getLayoutParams();
						int LineCount = Common.GetStringLines(txtLRC.getText().toString(), (float) (txtLRC.getTextSize() / 1.5), dm.widthPixels);

						if (distance > StartDistance) // 放大
							layLRC.topMargin -= Math.abs(StartHeight - txtLRC.getLineHeight() * LineCount) * ((event.getY(0) + event.getY(1)) / 2) / (txtLRC.getLineHeight() * LineCount);
						else if (distance < StartDistance) // 缩小
							layLRC.topMargin += Math.abs(StartHeight - txtLRC.getLineHeight() * LineCount) * ((event.getY(0) + event.getY(1)) / 2) / (txtLRC.getLineHeight() * LineCount);

						txtLRC.setLayoutParams(layLRC);
						txtLRC.setHeight(txtLRC.getLineHeight() * LineCount);

						txtLRC.invalidate();
						StartDistance = distance;
						StartHeight = txtLRC.getLineHeight() * LineCount;
					}
					else
					{
						ls.setCanRefreshLRC(true);
						ReadyToZoom = false;
						IsLRCMoved = false;
					}
				}
				else
				{
					if (event.getAction() == MotionEvent.ACTION_UP)
					{// 按下歌词显示/隐藏进度条

						if (Switch2List)
						{
							Switch2List = false;
							IsLRCMoved = false;
							LRC2ListSwitcher();
						}
						else if (IsLRCMoved) // Move过不执行托盘变化
							IsLRCMoved = false;

						DownPosX = -1;
						DownPosY = -1;

						ls.setCanRefreshLRC(true);

						// 设置字体大小
						Editor edt = sp.edit();
						edt.putString("LRCFontSize", String.valueOf(txtLRC.getTextSize() / 1.5));
						st.setLRCFontSize(String.valueOf(txtLRC.getTextSize() / 1.5));
						edt.commit();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE)
					{
						// 获取垂直/水平方向手指移动绝对值
						float AbsX = Math.abs(event.getX(0) - DownPosX);
						float AbsY = Math.abs(event.getY(0) - DownPosY);

						// 通过绝对值大小来判定手势方向
						if (AbsX > AbsY)
						{// 横向切换页面
							if (event.getX(0) - DownPosX > 150)
								Switch2List = true;
						}
						else
						{// 纵向（含恰好相等的情况）滚动歌词
							LinearLayout.LayoutParams layLRC = (LinearLayout.LayoutParams) txtLRC.getLayoutParams();
							// 获取scrLRC尺寸参数
							layLRC.topMargin += (int) (event.getY(0) - DownPosY);
							txtLRC.setLayoutParams(layLRC);
						}

						if (Math.abs(DownPosX - event.getX()) > 10 || Math.abs(DownPosY - event.getY()) > 10)
							IsLRCMoved = true; // Move过的标记
					}
					else if (event.getAction() == MotionEvent.ACTION_DOWN)
					{
						ls.setCanRefreshLRC(false);

						DownPosX = event.getX();
						DownPosY = event.getY();
					}
				}

				return false; // 继续回传，否则ACTION_DOWN后接收不到其它事件
			}
		});

		/* 文件列表 */
		lstFile.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if (arg2 == SelectedFileIndex)
				{
					Map<String, String> map = new HashMap<String, String>();
					map = lstLRCFile.get(arg2);
					String strPath = map.get("AbsolutePath");
					File f = new File(strPath);
					if (f.isDirectory()) // 进入目录
						SetFileList(strPath);
				}
				else
				{
					SelectedFileIndex = arg2;

					fAdapter.getView(arg2, null, lstFile);
					fAdapter.notifyDataSetChanged();
				}
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

				final MessageDialog md = new MessageDialog();
				md.ShowMessage(scrMain.this, layActivity, (String) mapItem.get("Title"), strMessage, 20, new OnClickListener()
				{
					public void onClick(View v)
					{
						md.CloseDialog();
					}
				}, null);

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
					if (laySearch.getVisibility() == View.VISIBLE)
					{
						Editor edt = sp.edit();
						edt.putString("LastKeyword", txtKeyword.getText().toString());
						st.setLastKeyword(txtKeyword.getText().toString());
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
		if (grdMenu.getVisibility() == View.GONE)
		{
			grdMenu.setVisibility(View.VISIBLE);
			Animation anim = new AlphaAnimation(0, 1);

			if (st.getUseAnimation())
			{
				anim.setDuration(ANIMATION_TIME);
				grdMenu.startAnimation(anim);
			}
		}
	}

	/* 隐藏扩展托盘 */
	public void HideExtendPanel()
	{
		if (grdMenu.getVisibility() == View.VISIBLE)
		{
			grdMenu.setVisibility(View.GONE);
			Animation anim = new AlphaAnimation(1, 0);

			if (st.getUseAnimation())
			{
				anim.setDuration(ANIMATION_TIME);
				grdMenu.startAnimation(anim);
			}
		}
	}

	/* 扩展托盘显示/隐藏 */
	public void ExtendPanelSwitcher()
	{
		Animation anim = null;

		if (grdMenu.getVisibility() == View.VISIBLE)
		{
			grdMenu.setVisibility(View.GONE);
			anim = new AlphaAnimation(1, 0);
		}
		else
		{
			grdMenu.setVisibility(View.VISIBLE);
			anim = new AlphaAnimation(0, 1);
		}

		if (st.getUseAnimation())
		{
			anim.setDuration(ANIMATION_TIME);
			grdMenu.startAnimation(anim);
		}
	}

	/* 按键动作 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// 如果当前显示的是列表，那么最小化
			if (CurrentShown == 0)
			{
				Intent i = new Intent(Intent.ACTION_MAIN);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addCategory(Intent.CATEGORY_HOME);
				startActivity(i);
			}
			else
				LRC2ListSwitcher();

			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			ExtendPanelSwitcher();

			return false;
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			skbVolume.setProgress(skbVolume.getProgress() + 1);
			SetCurrentTitle(ms.getStrShownTitle());

			if (ScreenOrantation != 1 && ScreenOrantation != 3)
				skbVolume.setVisibility(View.VISIBLE);

			if (toast != null)
			{
				toast.setText(getString(R.string.scrmain_volume) + skbVolume.getProgress());
				toast.setDuration(Toast.LENGTH_SHORT);
			}
			else
				toast = Toast.makeText(scrMain.this, getString(R.string.scrmain_volume) + skbVolume.getProgress(), Toast.LENGTH_SHORT);

			toast.show();

			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
		{
			skbVolume.setProgress(skbVolume.getProgress() - 1);
			SetCurrentTitle(ms.getStrShownTitle());

			if (ScreenOrantation != 1 && ScreenOrantation != 3)
				skbVolume.setVisibility(View.VISIBLE);

			if (toast != null)
			{
				if (skbVolume.getProgress() == 0)
					toast.setText(R.string.scrmain_volume_mute);
				else
					toast.setText(getString(R.string.scrmain_volume) + skbVolume.getProgress());
				toast.setDuration(Toast.LENGTH_SHORT);
			}
			else
				toast = Toast.makeText(scrMain.this, getString(R.string.scrmain_volume) + skbVolume.getProgress(), Toast.LENGTH_SHORT);

			toast.show();

			return true;
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

	public TextView getTxtTitle()
	{
		return txtTitle;
	}

	public void setTxtTitle(TextView txtTitle)
	{
		this.txtTitle = txtTitle;
	}

	public TextView getTxtLRC()
	{
		return txtLRC;
	}

	public void setTxtLRC(TextView txtLRC)
	{
		this.txtLRC = txtLRC;
	}

	public LinearLayout getLayControlPanel()
	{
		return layControlPanel;
	}

	public void setLayControlPanel(LinearLayout layControlPanel)
	{
		this.layControlPanel = layControlPanel;
	}

	public RelativeLayout getLayBody()
	{
		return layBody;
	}

	public void setLayBody(RelativeLayout layBody)
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

	public RelativeLayout getLayMain()
	{
		return layMain;
	}

	public void setLayMain(RelativeLayout layMain)
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

	public RelativeLayout getLaySearch()
	{
		return laySearch;
	}

	public void setLaySearch(RelativeLayout laySearch)
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

	public LinearLayout getLayActivity()
	{
		return layActivity;
	}

	public void setLayActivity(LinearLayout layActivity)
	{
		this.layActivity = layActivity;
	}

	public NotificationManager getNm()
	{
		return nm;
	}

	public void setNm(NotificationManager nm)
	{
		this.nm = nm;
	}

	public static int getMusicNotifyId()
	{
		return MUSIC_NOTIFY_ID;
	}

	public WindowManager getWm()
	{
		return wm;
	}

	public void setWm(WindowManager wm)
	{
		this.wm = wm;
	}

	public FloatLRC getFl()
	{
		return fl;
	}

	public void setFl(FloatLRC fl)
	{
		this.fl = fl;
	}

	public WindowManager.LayoutParams getLayWM()
	{
		return layWM;
	}

	public void setLayWM(WindowManager.LayoutParams layWM)
	{
		this.layWM = layWM;
	}

	public boolean isIsStartup()
	{
		return IsStartup;
	}

	public void setIsStartup(boolean isStartup)
	{
		IsStartup = isStartup;
	}

	public boolean isIsRefreshing()
	{
		return IsRefreshing;
	}

	public void setIsRefreshing(boolean isRefreshing)
	{
		IsRefreshing = isRefreshing;
	}

	public ImageButton getBtnVolume()
	{
		return btnVolume;
	}

	public void setBtnVolume(ImageButton btnVolume)
	{
		this.btnVolume = btnVolume;
	}

	public SeekBar getSkbVolume()
	{
		return skbVolume;
	}

	public void setSkbVolume(SeekBar skbVolume)
	{
		this.skbVolume = skbVolume;
	}

	public AudioManager getAm()
	{
		return am;
	}

	public void setAm(AudioManager am)
	{
		this.am = am;
	}

	public DisplayMetrics getDm()
	{
		return dm;
	}

	public void setDm(DisplayMetrics dm)
	{
		this.dm = dm;
	}

	public static int getLrcNotifyId()
	{
		return LRC_NOTIFY_ID;
	}

	public Toast getToast()
	{
		return toast;
	}

	public void setToast(Toast toast)
	{
		this.toast = toast;
	}

	public int getVerifyCode()
	{
		return VerifyCode;
	}

	public void setVerifyCode(int verifyCode)
	{
		VerifyCode = verifyCode;
	}

	public boolean isIsShowingFavourite()
	{
		return IsShowingFavourite;
	}

	public void setIsShowingFavourite(boolean isShowingFavourite)
	{
		IsShowingFavourite = isShowingFavourite;
	}

	public Button getBtnFileOK()
	{
		return btnFileOK;
	}

	public void setBtnFileOK(Button btnFileOK)
	{
		this.btnFileOK = btnFileOK;
	}

	public Button getBtnFileCancel()
	{
		return btnFileCancel;
	}

	public void setBtnFileCancel(Button btnFileCancel)
	{
		this.btnFileCancel = btnFileCancel;
	}

	public LinearLayout getLayLyricController()
	{
		return layLyricController;
	}

	public void setLayLyricController(LinearLayout layLyricController)
	{
		this.layLyricController = layLyricController;
	}

	public List<Map<String, String>> getLstLRCFile()
	{
		return lstLRCFile;
	}

	public void setLstLRCFile(List<Map<String, String>> lstLRCFile)
	{
		this.lstLRCFile = lstLRCFile;
	}

	public int getSelectedFileIndex()
	{
		return SelectedFileIndex;
	}

	public void setSelectedFileIndex(int selectedFileIndex)
	{
		SelectedFileIndex = selectedFileIndex;
	}

	public ListView getLstFile()
	{
		return lstFile;
	}

	public void setLstFile(ListView lstFile)
	{
		this.lstFile = lstFile;
	}

	public FileAdapterForMain getfAdapter()
	{
		return fAdapter;
	}

	public void setfAdapter(FileAdapterForMain fAdapter)
	{
		this.fAdapter = fAdapter;
	}

	public TextView getTxtCurrentPath()
	{
		return txtCurrentPath;
	}

	public void setTxtCurrentPath(TextView txtCurrentPath)
	{
		this.txtCurrentPath = txtCurrentPath;
	}

	public boolean isIsPlayingExternal()
	{
		return IsPlayingExternal;
	}

	public void setIsPlayingExternal(boolean isPlayingExternal)
	{
		IsPlayingExternal = isPlayingExternal;
	}

	public TextView getTxtTimeCurrent()
	{
		return txtTimeCurrent;
	}

	public void setTxtTimeCurrent(TextView txtTimeCurrent)
	{
		this.txtTimeCurrent = txtTimeCurrent;
	}

	public TextView getTxtTimeTotal()
	{
		return txtTimeTotal;
	}

	public void setTxtTimeTotal(TextView txtTimeTotal)
	{
		this.txtTimeTotal = txtTimeTotal;
	}

	public boolean isIsStartedUp()
	{
		return IsStartedUp;
	}

	public void setIsStartedUp(boolean isStartedUp)
	{
		IsStartedUp = isStartedUp;
	}

	public RelativeLayout getLayFileSelector()
	{
		return layFileSelector;
	}

	public void setLayFileSelector(RelativeLayout layFileSelector)
	{
		this.layFileSelector = layFileSelector;
	}

	public SettingProvider getSt()
	{
		return st;
	}

	public void setSt(SettingProvider st)
	{
		this.st = st;
	}

	public SQLiteDatabase getSd()
	{
		return sd;
	}

	public void setSd(SQLiteDatabase sd)
	{
		this.sd = sd;
	}

	public int getOldHeight()
	{
		return OldHeight;
	}

	public void setOldHeight(int oldHeight)
	{
		OldHeight = oldHeight;
	}

	public scrMain getMain()
	{
		return main;
	}

	public void setMain(scrMain main)
	{
		this.main = main;
	}

	public boolean isIsLRCMoved()
	{
		return IsLRCMoved;
	}

	public void setIsLRCMoved(boolean isLRCMoved)
	{
		IsLRCMoved = isLRCMoved;
	}

	public boolean isIsForceHideFloatLRC()
	{
		return IsForceHideFloatLRC;
	}

	public void setIsForceHideFloatLRC(boolean isForceHideFloatLRC)
	{
		IsForceHideFloatLRC = isForceHideFloatLRC;
	}
}