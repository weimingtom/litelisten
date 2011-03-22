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

import android.app.AlertDialog;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TextDialog
{
	private SettingService ss = null;
	private AlertDialog dlg = null;
	private EditText txtPath = null;
	private String strPreference = "";

	public TextDialog(SettingService ss)
	{
		this.ss = ss;
	}

	public void ShowDialog(String Preference)
	{
		strPreference = Preference;
		dlg = new AlertDialog.Builder(ss).show();
		dlg.getWindow().setContentView(R.layout.textalertdialog);
		txtPath = (EditText) dlg.findViewById(R.id.txtPath);
		txtPath.setText(ss.findPreference(Preference).getSummary().toString());

		// ����ͼ��
		ImageView imgIcon = (ImageView) dlg.getWindow().findViewById(R.id.imgTextIcon);
		imgIcon.setBackgroundResource(R.drawable.album_normal);

		// ���öԻ������
		TextView txtTitle = (TextView) dlg.getWindow().findViewById(R.id.txtTextTitle);
		txtTitle.setText(ss.findPreference(Preference).getTitle().toString());

		// ����ȷ����ť
		Button btnOK = (Button) dlg.getWindow().findViewById(R.id.btnTextOK);
		btnOK.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Editor e = ss.getSp().edit(); // �༭�����ļ�
				e.putString("btnMusicPath", txtPath.getText().toString()); // ������ɫ
				e.commit(); // ȷ������
				ss.findPreference(strPreference).setSummary(txtPath.getText().toString());
				dlg.cancel();
			}
		});

		// ����ȡ����ť
		Button btnCancel = (Button) dlg.getWindow().findViewById(R.id.btnTextCancel);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				dlg.cancel();
			}
		});
	}

	public void ChangeLayout()
	{
		if (dlg != null && dlg.isShowing())
		{
			dlg.getWindow().setContentView(R.layout.alertdialog);
			txtPath = (EditText) dlg.findViewById(R.id.txtPath);
			txtPath.setText(ss.findPreference(strPreference).getSummary().toString());

			// ����ͼ��
			ImageView imgIcon = (ImageView) dlg.getWindow().findViewById(R.id.imgTextIcon);
			imgIcon.setBackgroundResource(R.drawable.album_normal);

			// ���öԻ������
			TextView txtTitle = (TextView) dlg.getWindow().findViewById(R.id.txtTextTitle);
			txtTitle.setText(ss.findPreference(strPreference).getTitle().toString());

			// ����ȷ����ť
			Button btnOK = (Button) dlg.getWindow().findViewById(R.id.btnTextOK);
			btnOK.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					Editor e = ss.getSp().edit(); // �༭�����ļ�
					e.putString("btnMusicPath", txtPath.getText().toString()); // ������ɫ
					e.commit(); // ȷ������
					ss.findPreference(strPreference).setSummary(txtPath.getText().toString());
					dlg.cancel();
				}
			});

			// ����ȡ����ť
			Button btnCancel = (Button) dlg.getWindow().findViewById(R.id.btnTextCancel);
			btnCancel.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					dlg.cancel();
				}
			});
		}
	}

	public AlertDialog getDlg()
	{
		return dlg;
	}

	public void setDlg(AlertDialog dlg)
	{
		this.dlg = dlg;
	}

	public EditText getTxtPath()
	{
		return txtPath;
	}

	public void setTxtPath(EditText txtPath)
	{
		this.txtPath = txtPath;
	}

	public SettingService getSs()
	{
		return ss;
	}

	public void setSs(SettingService ss)
	{
		this.ss = ss;
	}

	public String getStrPreference()
	{
		return strPreference;
	}

	public void setStrPreference(String strPreference)
	{
		this.strPreference = strPreference;
	}
}