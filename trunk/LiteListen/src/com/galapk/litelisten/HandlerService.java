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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HandlerService
{
	private scrMain main = null;
	private scrSettings settings = null;

	public HandlerService(scrMain main)
	{
		this.main = main;
	}

	public HandlerService(scrSettings settings)
	{
		this.settings = settings;
	}

	/* �������������Ե�Handler */
	private Handler hdlSetStartupLanguage = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.SetLanguage();
		}
	};

	/* ��ʾ����MessageDialog��Handler */
	private Handler hdlShowMessageDialog = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			MessageDialog md = (MessageDialog) msg.obj;
			if (md.getPw() != null)
				md.getPw().showAtLocation(md.getWindowParent(), Gravity.CENTER, 0, 0);
		}
	};

	/* ���������ʵ�Handler */
	private Handler hdlSetStrLRCPath = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getLs().setStrLRCPath((String) msg.obj);
		}
	};

	/* ��ʾ�����渡����ʾ��Handler */
	private Handler hdlShowToastMain = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Toast.makeText(main, (String) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};

	/* ��ʾ���ý��渡����ʾ��Handler */
	private Handler hdlShowToastSettings = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Toast.makeText(settings, (String) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};

	/* �رջ�ӭ�����Handler */
	private Handler hdlShowMain = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getLaySplash().setVisibility(View.GONE);
		}
	};

	/* ��ʾ������־��Handler */
	private Handler hdlShowUpdateLog = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// �Ƿ��Ѿ���ʾ�˸�����־
			if (main.getSp().getBoolean("IsFirstStart25", true))
			{
				final MessageDialog md = new MessageDialog();
				md.ShowMessage(main, main.getLayActivity(), main.getString(R.string.scrmain_update_log), main.getString(R.string.update_info), 15, new OnClickListener()
				{
					public void onClick(View v)
					{
						Editor edt = main.getSp().edit();
						edt.putBoolean("IsFirstStart25", false); // ���õ�ǰ�汾
						edt.remove("IsFirstStart24"); // ɾ���ϸ��汾�ı��
						edt.commit();

						md.CloseDialog();
					}
				}, null);
			}
		}
	};

	/* �����µ�Handler */
	private Handler hdlCheckForUpdate = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// �Ƿ��Ѿ���ʾ�˸�����־
			if ((main.getSp().getString("HowToCheckForUpdate", "1").equals("1") && Common.IsWiFiConnected(main)) || main.getSp().getString("HowToCheckForUpdate", "1").equals("2"))
			{
				// ��ȡ�汾�ţ�VersionCode��
				Toast.makeText(main, main.getString(R.string.pfrscat_others_check_for_update_checking), Toast.LENGTH_SHORT).show(); // ��ʾ������ʾ
				int CurrentVersion = 0; // �汾��
				try
				{
					CurrentVersion = main.getPackageManager().getPackageInfo("com.galapk.litelisten", 0).versionCode;
				}
				catch (NameNotFoundException e)
				{
					if (e.getMessage() != null)
						Log.w(Common.LOGCAT_TAG, e.getMessage());
					else
						e.printStackTrace();
				}

				// ������
				String RemoteVersion = Common.CheckForUpdate(CurrentVersion);
				if (RemoteVersion != null && !RemoteVersion.equals(""))
				{
					final MessageDialog md = new MessageDialog();
					md.SetMessage(main, main.getLayActivity(), main.getString(R.string.pfrscat_others_check_for_update_got_title), main
							.getString(R.string.pfrscat_others_check_for_update_got_message1)
							+ RemoteVersion + main.getString(R.string.pfrscat_others_check_for_update_got_message2), 18, new OnClickListener()
					{
						public void onClick(View v)
						{
							new Thread()
							{
								public void run()
								{
									// д��ͳ����־
									TelephonyManager tm = (TelephonyManager) main.getSystemService(Context.TELEPHONY_SERVICE); // ��ȡ�ֻ����ŵ���Ϣ������

									// ��������
									String strURL = "http://www.littledai.com/LiteListen/SetDevInfo.php?imei={imei}&locale={locale}&sdk={sdk}&release={release}&model={model}";
									strURL = strURL.replace("{imei}", java.net.URLEncoder.encode(tm.getDeviceId())).replace("{locale}",
											java.net.URLEncoder.encode(main.getResources().getConfiguration().locale.toString())).replace("{sdk}", java.net.URLEncoder.encode(Build.VERSION.SDK))
											.replace("{release}", java.net.URLEncoder.encode(Build.VERSION.RELEASE)).replace("{model}",
													java.net.URLEncoder.encode(Build.MODEL).replace("{action}", java.net.URLEncoder.encode("update")));

									Common.CallURLPost(strURL, 10000);

									// ��ʼ���ظ����ļ�
									File TempFile = Common.GetUpdate();
									if (TempFile != null)
									{// �ļ��������
										Intent intent = new Intent();
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.setAction(android.content.Intent.ACTION_VIEW);
										intent.setDataAndType(Uri.fromFile(TempFile), Common.GetMIMEType(TempFile));
										main.startActivity(intent);
									}
									else
									{// δ��ɣ�������ʾ
										final MessageDialog md = new MessageDialog();
										md.SetMessage(main, main.getLayActivity(), main.getString(R.string.pfrscat_others_check_for_update_got_title), main
												.getString(R.string.pfrscat_others_check_for_update_got_failed), 18, new OnClickListener()
										{
											public void onClick(View v)
											{
												md.CloseDialog();
											}
										}, null);

										// ��ʾ�Ի���
										Message msg = new Message();
										msg.obj = md;
										hdlShowMessageDialog.sendMessage(msg);
									}
								}
							}.start();

							Toast.makeText(main, main.getString(R.string.pfrscat_others_check_for_update_downloading), Toast.LENGTH_SHORT).show(); // ��ʾ������ʾ
							md.CloseDialog();
						}
					}, new OnClickListener()
					{
						public void onClick(View v)
						{
							md.CloseDialog();
						}
					});

					// ��ʾ�Ի���
					Message msgUpdate = new Message();
					msgUpdate.obj = md;
					hdlShowMessageDialog.sendMessage(msgUpdate);
				}
				else
					Toast.makeText(main, main.getString(R.string.pfrscat_others_check_for_update_nothing_message), Toast.LENGTH_SHORT).show(); // ��ʾ������ʾ
			}
		}
	};

	/* �����豸��Ϣ��Handler */
	private Handler hdlRequestDevInfo = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// �Ƿ��Ѿ��������豸���
			if (!main.getSp().getBoolean("SentInfo", false))
			{
				final MessageDialog md = new MessageDialog();
				md.ShowMessage(main, main.getLayActivity(), main.getString(R.string.global_request), main.getString(R.string.scrmain_request_info), 18, new OnClickListener()
				{
					public void onClick(View v)
					{
						Editor edt = main.getSp().edit();
						edt.putBoolean("SentInfo", true);
						edt.commit();

						// ��ȡ�ֻ����ŵ���Ϣ������
						TelephonyManager tm = (TelephonyManager) main.getSystemService(Context.TELEPHONY_SERVICE);

						// ��������
						String strURL = "http://www.littledai.com/LiteListen/SetDevInfo.php?imei={imei}&locale={locale}&sdk={sdk}&release={release}&model={model}";
						strURL = strURL.replace("{imei}", java.net.URLEncoder.encode(tm.getDeviceId())).replace("{locale}",
								java.net.URLEncoder.encode(main.getResources().getConfiguration().locale.toString())).replace("{sdk}", java.net.URLEncoder.encode(Build.VERSION.SDK)).replace(
								"{release}", java.net.URLEncoder.encode(Build.VERSION.RELEASE)).replace("{model}", java.net.URLEncoder.encode(Build.MODEL));

						Common.CallURLPost(strURL, 10000);
						md.CloseDialog();
					}
				}, new OnClickListener()
				{
					public void onClick(View v)
					{
						Editor edt = main.getSp().edit();
						edt.putBoolean("SentInfo", true);
						edt.commit();

						md.CloseDialog();
					}
				});
			}
		}
	};

	/* ˢ��Adapter��Handler */
	private Handler hdlRefreshAdapter = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getAdapter().notifyDataSetChanged();
		}
	};

	/* ��Adapter��ˢ��ID3���Ե�Handler */
	private Handler hdlAdapterBinding = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.setAdapter(new MusicAdapter(main, main.getLstSong()));
			main.getLstMusic().setAdapter(main.getAdapter());
			main.RefreshID3();
		}
	};

	/* ����Adapter��Handler */
	private Handler hdlAdapterUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getLstMusic().setAdapter((MusicAdapter) msg.obj);
			main.SetAlbumIcon();
		}
	};

	/* ˢ��ʱ����ʾ�� Handler */
	private Handler hdlRefreshTime = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// ����ʱ��
			if (main.getMs().getPlayerStatus() != MusicService.STATUS_STOP)
			{
				if (main.getMs().isCanRefreshSeekBar())
				{
					main.getSkbMusic().setMax(main.getMs().GetTotalTime());
					main.getSkbMusic().setProgress(main.getMs().GetCurrTime());
				}
				main.getTxtTimeCurrent().setText(main.getLs().IntegerToTime(main.getMs().GetCurrTime()));
				main.getTxtTimeTotal().setText(main.getLs().IntegerToTime(main.getMs().GetTotalTime()));
			}
			else
			{
				main.getSkbMusic().setMax(0);
				main.getSkbMusic().setProgress(0);
				main.getTxtTimeCurrent().setText("00:00");
				main.getTxtTimeTotal().setText("00:00");
			}

			// ͨ���㲥���� Widget
			Intent intent = new Intent(IntentConst.INTENT_ACTION_REFRESH_TIME_N_TITLE);
			intent.putExtra("Time", main.getTxtTimeCurrent().getText().toString());
			intent.putExtra("Title", main.getMs().getStrArtist() + " - " + main.getMs().getStrShownTitle());
			main.sendBroadcast(intent);
		}
	};

	/* ���ͬ����Handler */
	private Handler hdlLRCSync = new Handler()
	{
		int LastPos = -1; // ��һ�β��Ŷ�����λ��
		int LastIndex = -1; // ��һ�β��ŵĸ���

		@Override
		public void handleMessage(Message msg)
		{
			if (main.getLs().isCanRefreshLRC())
			{// ����LRC����
				if (main.getCurrentShown() == 1)
				{
					LinearLayout.LayoutParams layLRC = (LinearLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // ��ȡscrLRC�ߴ����
					int OldY = layLRC.topMargin; // ��¼��ǰλ��
					layLRC.topMargin = msg.what;
					layLRC.height = main.getTxtLRC().getLineCount() * main.getTxtLRC().getLineHeight();

					main.getTxtLRC().setLayoutParams(layLRC);
					main.getTxtLRC().setLines(main.getTxtLRC().getLineCount());
					main.getTxtLRC().setText((SpannableStringBuilder) msg.obj);

					// �������������ò���
					if (LastIndex != msg.arg2)
					{
						LastPos = -1;
						LastIndex = msg.arg2;
					}

					// λ�ò���ͬʱ�Ų��Ŷ���
					int OffsetY = 275; // Ĭ��������ƫ��275dip
					if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
						OffsetY = 95; // ����ƫ��95dip

					if (LastPos != layLRC.topMargin && layLRC.topMargin != OffsetY)
					{
						if (main.getSt().getUseAnimation())
						{
							Animation anim = new TranslateAnimation(0, 0, OldY - msg.what, 0); // �ӵ�ǰλ�õ�Ŀ��λ�ö���
							Bundle b = msg.getData();
							if (main.getSt().getScrollMode().equals("1"))
								anim.setDuration(b.getLong("TimeGap")); // ƽ������
							else
								anim.setDuration(200); // ���й���

							// ��ʾʱ�Ų��Ŷ���
							if (main.getTxtLRC().getVisibility() == View.VISIBLE)
								main.getTxtLRC().startAnimation(anim);
						}

						LastPos = layLRC.topMargin;
					}
				}
				else if (main.getCurrentShown() == 0)
					main.getTxtLRC().setText((SpannableStringBuilder) msg.obj); // ��ֹLRC���������¸����ɫ
			}

			// �����ǰ�ں�̨�������������С����
			if (main.getSt().getIsRunBackground())
			{
				Intent intent = new Intent(IntentConst.INTENT_ACTION_REFRESH_LRC);
				intent.putExtra("LRCSmall", main.getLs().getStrCurrLRCSentenceSmall());
				intent.putExtra("LRCMedium", main.getLs().getStrCurrLRCSentenceMedium());
				intent.putExtra("LRCLarge", main.getLs().getStrCurrLRCSentenceLarge());
				main.sendBroadcast(intent);
			}

			if (main.getLs().isIsChanged())
			{
				if (main.getLs().isCanRefreshFloatRC())
					main.getFl().SetLRC(R.drawable.album_selected, main.getLs().getStrLRCToFloat1(), Color.WHITE, main.getLs().getStrLRCToFloat2(), Color.rgb(155, 215, 255),
							main.getLs().getTimeGap(), 1);
				else
					main.getFl().SetLRC(R.drawable.album_selected, main.getLs().getStrLRCToFloat1(), Color.rgb(155, 215, 255), main.getLs().getStrLRCToFloat2(), Color.WHITE,
							main.getLs().getTimeGap(), 2);
			}
		}
	};

	/* ���¸�����ʵ�Handler */
	private Handler hdlSetFloatLRC = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getFl().SetLRC(R.drawable.album_selected, msg.getData().getString("Sentence1"), Color.WHITE, msg.getData().getString("Sentence2"), Color.rgb(155, 215, 255), (long) 0, 1);
		}
	};

	/* ˢ�º������� Handler */
	private Handler hdlLoadLRC = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			LinearLayout.LayoutParams layLRC = (LinearLayout.LayoutParams) main.getTxtLRC().getLayoutParams(); // ��ȡtxtLRC�ߴ����
			if (main.getScreenOrantation() == 1 || main.getScreenOrantation() == 3)
				layLRC.topMargin = 95;
			else
				layLRC.topMargin = 275;
			main.getTxtLRC().setLayoutParams(layLRC);
			main.getTxtLRC().setText(main.getLs().getStrLRC());
		}
	};

	/* ����/��ͣ�� Handler */
	private Handler hdlPlayPause = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getMs().PlayPause();
		}
	};

	/* ������һ�׵� Handler */
	private Handler hdlPlayLast = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getMs().Last();
		}
	};

	/* ������һ�׵� Handler */
	private Handler hdlPlayNext = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			main.getMs().Next(false);
		}
	};

	public Handler getHdlPlayPause()
	{
		return hdlPlayPause;
	}

	public void setHdlPlayPause(Handler hdlPlayPause)
	{
		this.hdlPlayPause = hdlPlayPause;
	}

	public Handler getHdlPlayLast()
	{
		return hdlPlayLast;
	}

	public void setHdlPlayLast(Handler hdlPlayLast)
	{
		this.hdlPlayLast = hdlPlayLast;
	}

	public Handler getHdlPlayNext()
	{
		return hdlPlayNext;
	}

	public void setHdlPlayNext(Handler hdlPlayNext)
	{
		this.hdlPlayNext = hdlPlayNext;
	}

	public scrMain getMain()
	{
		return main;
	}

	public void setMain(scrMain main)
	{
		this.main = main;
	}

	public Handler getHdlAdapterUpdateHandler()
	{
		return hdlAdapterUpdateHandler;
	}

	public void setHdlAdapterUpdateHandler(Handler hdlAdapterUpdateHandler)
	{
		this.hdlAdapterUpdateHandler = hdlAdapterUpdateHandler;
	}

	public Handler getHdlSetStartupLanguage()
	{
		return hdlSetStartupLanguage;
	}

	public void setHdlSetStartupLanguage(Handler hdlSetStartupLanguage)
	{
		this.hdlSetStartupLanguage = hdlSetStartupLanguage;
	}

	public Handler getHdlRefreshTime()
	{
		return hdlRefreshTime;
	}

	public void setHdlRefreshTime(Handler hdlRefreshTime)
	{
		this.hdlRefreshTime = hdlRefreshTime;
	}

	public Handler getHdlLRCSync()
	{
		return hdlLRCSync;
	}

	public void setHdlLRCSync(Handler hdlLRCSync)
	{
		this.hdlLRCSync = hdlLRCSync;
	}

	public Handler getHdlLoadLRC()
	{
		return hdlLoadLRC;
	}

	public void setHdlLoadLRC(Handler hdlLoadLRC)
	{
		this.hdlLoadLRC = hdlLoadLRC;
	}

	public Handler getHdlRefreshAdapter()
	{
		return hdlRefreshAdapter;
	}

	public void setHdlRefreshAdapter(Handler hdlRefreshAdapter)
	{
		this.hdlRefreshAdapter = hdlRefreshAdapter;
	}

	public Handler getHdlShowMain()
	{
		return hdlShowMain;
	}

	public void setHdlShowMain(Handler hdlShowMain)
	{
		this.hdlShowMain = hdlShowMain;
	}

	public Handler getHdlAdapterBinding()
	{
		return hdlAdapterBinding;
	}

	public void setHdlAdapterBinding(Handler hdlAdapterBinding)
	{
		this.hdlAdapterBinding = hdlAdapterBinding;
	}

	public Handler getHdlSetFloatLRC()
	{
		return hdlSetFloatLRC;
	}

	public void setHdlSetFloatLRC(Handler hdlSetFloatLRC)
	{
		this.hdlSetFloatLRC = hdlSetFloatLRC;
	}

	public Handler getHdlShowUpdateLog()
	{
		return hdlShowUpdateLog;
	}

	public void setHdlShowUpdateLog(Handler hdlShowUpdateLog)
	{
		this.hdlShowUpdateLog = hdlShowUpdateLog;
	}

	public Handler getHdlSetStrLRCPath()
	{
		return hdlSetStrLRCPath;
	}

	public void setHdlSetStrLRCPath(Handler hdlSetStrLRCPath)
	{
		this.hdlSetStrLRCPath = hdlSetStrLRCPath;
	}

	public Handler getHdlRequestDevInfo()
	{
		return hdlRequestDevInfo;
	}

	public void setHdlRequestDevInfo(Handler hdlRequestDevInfo)
	{
		this.hdlRequestDevInfo = hdlRequestDevInfo;
	}

	public Handler getHdlShowMessageDialog()
	{
		return hdlShowMessageDialog;
	}

	public void setHdlShowMessageDialog(Handler hdlShowMessageDialog)
	{
		this.hdlShowMessageDialog = hdlShowMessageDialog;
	}

	public scrSettings getSettings()
	{
		return settings;
	}

	public void setSettings(scrSettings settings)
	{
		this.settings = settings;
	}

	public Handler getHdlShowToastMain()
	{
		return hdlShowToastMain;
	}

	public void setHdlShowToastMain(Handler hdlShowToastMain)
	{
		this.hdlShowToastMain = hdlShowToastMain;
	}

	public Handler getHdlShowToastSettings()
	{
		return hdlShowToastSettings;
	}

	public void setHdlShowToastSettings(Handler hdlShowToastSettings)
	{
		this.hdlShowToastSettings = hdlShowToastSettings;
	}

	public Handler getHdlCheckForUpdate()
	{
		return hdlCheckForUpdate;
	}

	public void setHdlCheckForUpdate(Handler hdlCheckForUpdate)
	{
		this.hdlCheckForUpdate = hdlCheckForUpdate;
	}
}