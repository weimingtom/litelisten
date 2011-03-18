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
	Context context;  //共享Context
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);		
		SetDateFormatAndSplitter();  //更新日期格式和分隔符显示
		SetSkinSummary();  //更新皮肤名称显示
		context=this;  //获取本窗口的Context
		
		//设置日期颜色选择上的当前颜色
		Preference btnDateColor=(Preference)findPreference("btnDateColor");
		SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //读取配置文件
		String strColor=sp.getString("strDateColor","#0099FF");  //获取颜色
		btnDateColor.setSummary("设置日期显示颜色。当前：["+strColor+"]");
    }
	
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,Preference preference)
	{
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference,Object newValue)
			{
				if(preference.getKey().equals("lstDateFormat"))
				{//日期格式
					ListPreference lstDateFormat=(ListPreference)findPreference("lstDateFormat");  //日期格式控件
					lstDateFormat.setValue((String)newValue);  //获取日期格式选项的值
					SetDateFormatAndSplitter();
				}
				else if(preference.getKey().equals("txtDateSplitter"))
				{//日期分隔符
					EditTextPreference txtDateSplitter=(EditTextPreference)findPreference("txtDateSplitter");  //日期分隔符控件
					txtDateSplitter.setText((String)newValue);  //获取日期分隔符
					SetDateFormatAndSplitter();
				}
				else if(preference.getKey().equals("lstSkin"))
				{
					ListPreference lstSkin=(ListPreference)findPreference("lstSkin");  //皮肤
					lstSkin.setValue((String)newValue);
					SetSkinSummary();  //更新皮肤名称显示
				}

				return true;
			}
		});

		if(preference.getKey().equals("btnRestorePreference"))
		{//还原设置
			new AlertDialog.Builder(context).setMessage("确定将所有设置还原为默认值？")
	        .setTitle("还原设置")
	        .setPositiveButton("确定",new DialogInterface.OnClickListener()
	        {
	        	public void onClick(DialogInterface dialog,int whichButton)
	        	{//还原默认值
	        		ListPreference lstSkin=(ListPreference)findPreference("lstSkin");  //皮肤
					CheckBoxPreference chkTimeFormat=(CheckBoxPreference)findPreference("chkTimeFormat");  //时间格式
					ListPreference lstDateFormat=(ListPreference)findPreference("lstDateFormat");  //日期格式
					EditTextPreference txtDateSplitter=(EditTextPreference)findPreference("txtDateSplitter");  //日期分隔符
					CheckBoxPreference chkReduceResUse=(CheckBoxPreference)findPreference("chkReduceResUse");  //节能模式
					CheckBoxPreference chkShowBG=(CheckBoxPreference)findPreference("chkShowBG");  //小部件背景
	        	
					//默认值
					lstSkin.setValue("0");
					chkTimeFormat.setChecked(true);
					lstDateFormat.setValue("0");
					txtDateSplitter.setText("-");
					txtDateSplitter.setEnabled(false);
					chkReduceResUse.setChecked(true);
					chkShowBG.setChecked(true);
					
					SetDateFormatAndSplitter();  //更新日期格式和分隔符显示
					SetSkinSummary();  //更新皮肤名称显示
					
					Toast.makeText(context,"已将所有设置还原为默认值。",Toast.LENGTH_LONG).show();  //提示还原成功
				}
	        })
	        .setNegativeButton("取消",new DialogInterface.OnClickListener()
	        {//取消按钮不执行任何操作
	        	public void onClick(DialogInterface dialog,int whichButton){}
	        })
	        .show();
		}
		
		if(preference.getKey().equals("btnDateColor"))
		{//日期颜色选择
			SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //读取配置文件
			String strColor=sp.getString("strDateColor","#0099FF");  //获取颜色
			new ColorPickerDialog(context,null,Color.parseColor(strColor),this).show();  //提交给对话框
		}

		return false;
	}
	
	/*设置皮肤说明信息*/
	public void SetSkinSummary()
	{
		ListPreference lstSkin=(ListPreference)findPreference("lstSkin");  //皮肤
		if(lstSkin.getValue().equals("0"))
			lstSkin.setSummary("系统提供了多种皮肤。\n当前：[摩登蓝]");
		else if(lstSkin.getValue().equals("1"))
			lstSkin.setSummary("系统提供了多种皮肤。\n当前：[摩登红]");
		else if(lstSkin.getValue().equals("2"))
			lstSkin.setSummary("系统提供了多种皮肤。\n当前：[摩登黄]");
		else if(lstSkin.getValue().equals("3"))
			lstSkin.setSummary("系统提供了多种皮肤。\n当前：[经典黄]");
	}

	/*设置日期格式与分隔符菜单项之间的关联*/
	public void SetDateFormatAndSplitter()
	{
		ListPreference lstDateFormat=(ListPreference)findPreference("lstDateFormat");  //日期格式控件
		EditTextPreference txtDateSplitter=(EditTextPreference)findPreference("txtDateSplitter");  //日期分隔符控件
		String strDateFormatSummary="日期显示格式，如：2010-12-31\n当前：[";  //日期格式注释的前半部分
		txtDateSplitter.setSummary("自定义日期分隔符。当前："+txtDateSplitter.getText());  //设置日期分隔符注释文字
		
		//判断日期格式选项
    	if(lstDateFormat.getValue().equals("0"))
    	{//文本表达的日期格式
    		txtDateSplitter.setEnabled(false);  //关闭日期分隔符
    		lstDateFormat.setSummary(strDateFormatSummary+"2010年12月31日]");
    	}
    	else
    	{//通用表达式
			txtDateSplitter.setEnabled(true);  //启用日期分隔符
			String strSplitter=txtDateSplitter.getText();  //获取当前分隔符
			if(lstDateFormat.getValue().equals("1"))  //如果是日期通用表达式（年-月-日）
				lstDateFormat.setSummary(strDateFormatSummary+"2010"+strSplitter+"12"+strSplitter+"31]");
			else if(lstDateFormat.getValue().equals("2"))  //如果是日期通用表达式（月-日-年）
				lstDateFormat.setSummary(strDateFormatSummary+"12"+strSplitter+"31"+strSplitter+"2010]");
    	}
	}

	public void onClick(View view, int color)
	{
		Preference btnDateColor=(Preference)findPreference("btnDateColor");
		String strColor="#"+Integer.toHexString(color).substring(2).toUpperCase();
		btnDateColor.setSummary("设置日期显示颜色。当前：["+strColor+"]");
		SharedPreferences sp=context.getSharedPreferences("cn.aaronsoft.atime_preferences",0);  //读取配置文件
		Editor e=sp.edit();  //编辑配置文件
		e.putString("strDateColor",strColor);  //设置颜色
		e.commit();  //确定更改
	}
}