package com.littledai.eclock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements ColorPickerDialog.OnClickListener
{
	Context context;  //����Context
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);		
		SetDateFormatAndSplitter();  //�������ڸ�ʽ�ͷָ�����ʾ
		SetSkinSummary();  //����Ƥ��������ʾ
		context=this;  //��ȡ�����ڵ�Context
		
		//����������ɫѡ���ϵĵ�ǰ��ɫ
		Preference btnDateColor=(Preference)findPreference("btnDateColor");
		SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //��ȡ�����ļ�
		String strColor=sp.getString("strDateColor","#0099FF");  //��ȡ��ɫ
		btnDateColor.setSummary("����������ʾ��ɫ����ǰ��["+strColor+"]");
    }
	
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,Preference preference)
	{
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference,Object newValue)
			{
				if(preference.getKey().equals("lstDateFormat"))
				{//���ڸ�ʽ
					ListPreference lstDateFormat=(ListPreference)findPreference("lstDateFormat");  //���ڸ�ʽ�ؼ�
					lstDateFormat.setValue((String)newValue);  //��ȡ���ڸ�ʽѡ���ֵ
					SetDateFormatAndSplitter();
				}
				else if(preference.getKey().equals("txtDateSplitter"))
				{//���ڷָ���
					EditTextPreference txtDateSplitter=(EditTextPreference)findPreference("txtDateSplitter");  //���ڷָ����ؼ�
					txtDateSplitter.setText((String)newValue);  //��ȡ���ڷָ���
					SetDateFormatAndSplitter();
				}
				else if(preference.getKey().equals("lstSkin"))
				{
					ListPreference lstSkin=(ListPreference)findPreference("lstSkin");  //Ƥ��
					lstSkin.setValue((String)newValue);
					SetSkinSummary();  //����Ƥ��������ʾ
				}

				return true;
			}
		});

		if(preference.getKey().equals("btnRestorePreference"))
		{//��ԭ����
			new AlertDialog.Builder(context).setMessage("ȷ�����������û�ԭΪĬ��ֵ��")
	        .setTitle("��ԭ����")
	        .setPositiveButton("ȷ��",new DialogInterface.OnClickListener()
	        {
	        	public void onClick(DialogInterface dialog,int whichButton)
	        	{//��ԭĬ��ֵ
	        		ListPreference lstSkin=(ListPreference)findPreference("lstSkin");  //Ƥ��
					CheckBoxPreference chkTimeFormat=(CheckBoxPreference)findPreference("chkTimeFormat");  //ʱ���ʽ
					ListPreference lstDateFormat=(ListPreference)findPreference("lstDateFormat");  //���ڸ�ʽ
					EditTextPreference txtDateSplitter=(EditTextPreference)findPreference("txtDateSplitter");  //���ڷָ���
					CheckBoxPreference chkReduceResUse=(CheckBoxPreference)findPreference("chkReduceResUse");  //����ģʽ
					CheckBoxPreference chkShowBG=(CheckBoxPreference)findPreference("chkShowBG");  //С��������
	        	
					//Ĭ��ֵ
					lstSkin.setValue("0");
					chkTimeFormat.setChecked(true);
					lstDateFormat.setValue("0");
					txtDateSplitter.setText("-");
					txtDateSplitter.setEnabled(false);
					chkReduceResUse.setChecked(true);
					chkShowBG.setChecked(true);
					
					SetDateFormatAndSplitter();  //�������ڸ�ʽ�ͷָ�����ʾ
					SetSkinSummary();  //����Ƥ��������ʾ
					
					Toast.makeText(context,"�ѽ��������û�ԭΪĬ��ֵ��",Toast.LENGTH_LONG).show();  //��ʾ��ԭ�ɹ�
				}
	        })
	        .setNegativeButton("ȡ��",new DialogInterface.OnClickListener()
	        {//ȡ����ť��ִ���κβ���
	        	public void onClick(DialogInterface dialog,int whichButton){}
	        })
	        .show();
		}
		
		if(preference.getKey().equals("btnDateColor"))
		{//������ɫѡ��
			SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //��ȡ�����ļ�
			String strColor=sp.getString("strDateColor","#0099FF");  //��ȡ��ɫ
			new ColorPickerDialog(context,null,Color.parseColor(strColor),this).show();  //�ύ���Ի���
		}

		return false;
	}
	
	/*����Ƥ��˵����Ϣ*/
	public void SetSkinSummary()
	{
		ListPreference lstSkin=(ListPreference)findPreference("lstSkin");  //Ƥ��
		if(lstSkin.getValue().equals("0"))
			lstSkin.setSummary("ϵͳ�ṩ�˶���Ƥ����\n��ǰ��[Ħ����]");
		else if(lstSkin.getValue().equals("1"))
			lstSkin.setSummary("ϵͳ�ṩ�˶���Ƥ����\n��ǰ��[Ħ�Ǻ�]");
		else if(lstSkin.getValue().equals("2"))
			lstSkin.setSummary("ϵͳ�ṩ�˶���Ƥ����\n��ǰ��[Ħ�ǻ�]");
		else if(lstSkin.getValue().equals("3"))
			lstSkin.setSummary("ϵͳ�ṩ�˶���Ƥ����\n��ǰ��[�����]");
	}

	/*�������ڸ�ʽ��ָ����˵���֮��Ĺ���*/
	public void SetDateFormatAndSplitter()
	{
		ListPreference lstDateFormat=(ListPreference)findPreference("lstDateFormat");  //���ڸ�ʽ�ؼ�
		EditTextPreference txtDateSplitter=(EditTextPreference)findPreference("txtDateSplitter");  //���ڷָ����ؼ�
		String strDateFormatSummary="������ʾ��ʽ���磺2010-12-31\n��ǰ��[";  //���ڸ�ʽע�͵�ǰ�벿��
		txtDateSplitter.setSummary("�Զ������ڷָ�������ǰ��"+txtDateSplitter.getText());  //�������ڷָ���ע������
		
		//�ж����ڸ�ʽѡ��
    	if(lstDateFormat.getValue().equals("0"))
    	{//�ı��������ڸ�ʽ
    		txtDateSplitter.setEnabled(false);  //�ر����ڷָ���
    		lstDateFormat.setSummary(strDateFormatSummary+"2010��12��31��]");
    	}
    	else
    	{//ͨ�ñ��ʽ
			txtDateSplitter.setEnabled(true);  //�������ڷָ���
			String strSplitter=txtDateSplitter.getText();  //��ȡ��ǰ�ָ���
			if(lstDateFormat.getValue().equals("1"))  //���������ͨ�ñ��ʽ����-��-�գ�
				lstDateFormat.setSummary(strDateFormatSummary+"2010"+strSplitter+"12"+strSplitter+"31]");
			else if(lstDateFormat.getValue().equals("2"))  //���������ͨ�ñ��ʽ����-��-�꣩
				lstDateFormat.setSummary(strDateFormatSummary+"12"+strSplitter+"31"+strSplitter+"2010]");
    	}
	}

	public void onClick(View view, int color)
	{
		Preference btnDateColor=(Preference)findPreference("btnDateColor");
		String strColor="#"+Integer.toHexString(color).substring(2).toUpperCase();
		btnDateColor.setSummary("����������ʾ��ɫ����ǰ��["+strColor+"]");
		SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //��ȡ�����ļ�
		Editor e=sp.edit();  //�༭�����ļ�
		e.putString("strDateColor",strColor);  //������ɫ
		e.commit();  //ȷ������
	}
}