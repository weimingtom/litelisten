package com.littledai.top5friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.view.MotionEvent;  
import android.os.Bundle;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

@SuppressWarnings({"deprecation", "unchecked"})
public class srcMain extends Activity
{
    /*定义控件*/
	EditText txtKeyword;
	Button btnSearchType;
	LinearLayout pnlResult;
	ListView lstContact;
	ImageButton btnSubMenu;
	ImageButton btnIndexing;
	ImageButton btnSettings;
	ImageButton btnExit;
	ImageButton btnOpenSearch;
	ImageButton btnCloseSearch;
	AbsoluteLayout pnlMenu;
	LinearLayout pnlSearch;
	
	/*给ContextMenu使用的选中项信息*/
	String strPhoneNumber = "";  //电话号码
	String strName = "";  //姓名
	String strUsingTimes = "";  //联系次数
	String strContextMenuFlag = "";  //触发ContextMenu的标志
	String strCurrOperation = "";  //给子菜单判断的当前操作标志
	float CurrX = -1;  //当前触摸位置X
	float CurrY = -1;  //当前触摸位置Y
	
	Context ctx;  //全局的Context
	boolean ItemSelected = false;  //是否选中了lstContact项
	View LastSelectedItem = null;  //上一次选中的lstContact项

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        /*设置窗口样式，必须按照顺序*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无标题栏
        setContentView(R.layout.afriends);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        
        /*定义控件实体*/
        btnSearchType = (Button) findViewById(R.id.btnSearchType);
        txtKeyword = (EditText) findViewById(R.id.txtKeyword);
        pnlResult = (LinearLayout) findViewById(R.id.pnlResult);
        lstContact = (ListView) findViewById(R.id.lstContact);
        btnSubMenu = (ImageButton) findViewById(R.id.btnSubMenu);
        btnIndexing = (ImageButton) findViewById(R.id.btnIndexing);
        btnExit = (ImageButton) findViewById(R.id.btnExit);
        btnOpenSearch = (ImageButton) findViewById(R.id.btnOpenSearch);
        btnCloseSearch = (ImageButton) findViewById(R.id.btnCloseSearch);
        pnlMenu = (AbsoluteLayout) findViewById(R.id.pnlMenu);
        pnlSearch = (LinearLayout) findViewById(R.id.pnlSearch);
    	
        /*定义控件委托*/
        btnSearchType.setOnClickListener(CtrlFunc_btnSearchTypeClick);
        lstContact.setOnItemClickListener(CtrlFunc_lstContactClick);
        lstContact.setOnItemLongClickListener(CtrlFunc_lstContactLongClick);  //首先触发长按事件
        lstContact.setOnCreateContextMenuListener(CtrlFunc_lstContactCreateContextMenu);  //然后才建立ContextMenu
        txtKeyword.setOnKeyListener(CtrlFunc_txtKeywordKeyPress);
        btnSubMenu.setOnClickListener(CtrlFunc_btnSubMenuClick);
        btnIndexing.setOnClickListener(CtrlFunc_btnIndexingClick);
        btnExit.setOnClickListener(CtrlFunc_btnExitClick);
        btnOpenSearch.setOnClickListener(CtrlFunc_btnOpenCloseSearchClick);
        btnCloseSearch.setOnClickListener(CtrlFunc_btnOpenCloseSearchClick);

        SearchContact();  //初始化时搜索全部内容
        ctx = this.getBaseContext();
    }
	
	/*获取触摸坐标*/
    @Override  
    public boolean onTouchEvent(MotionEvent event)
    {
    	CurrX = event.getX();
    	CurrY = event.getY();
    	
        return super.onTouchEvent(event);
    }
    
	/*创建OptionMenu*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 0, 0, "更新联系人索引").setIcon(R.drawable.index_contact);
		menu.add(0, 0, 1, "设置").setIcon(R.drawable.settings);
		menu.add(0, 0, 2, "退出系统").setIcon(R.drawable.exit);
		menu.add(0, 0, 3, "关于");  //.setIcon(R.drawable.exit);
		menu.add(0, 0, 4, "帮助");  //.setIcon(R.drawable.exit);
		menu.add(0, 0, 5, "检查更新");  //.setIcon(R.drawable.exit);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/*设置OptionMenu事件*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getOrder() == 0)
		{//打开建立索引窗口
			Intent itt=new Intent(this.getBaseContext(), IndexingDialog.class);
			startActivity(itt);
		}
		else if(item.getOrder() == 1)
			Toast.makeText(this, "设置", Toast.LENGTH_LONG).show();
		else if(item.getOrder() == 2)
			System.exit(0);
		
	    return super.onOptionsItemSelected(item);
	}

	/*获取当前程序的数据库实例*/
	public SQLiteDatabase GetDBInstance(boolean IsReadOnly)
	{
		SQLiteDatabase db;  //数据库
		
		if(IsReadOnly)  //只读
			db = new DBProvider(getBaseContext()).getReadableDatabase();
		else
			db = new DBProvider(getBaseContext()).getWritableDatabase();
			
		return db;
	}
	
	/*修改数据*/
	public boolean DBModifiy(String strTable, String strSubStringOfSet)
	{
		SQLiteDatabase dbContact = GetDBInstance(false);  //数据库实例
		try
		{
			if(strSubStringOfSet.equals("") || strSubStringOfSet.equals(null)) return false;
			String strSQLDelete = "update " + strTable + " " + strSubStringOfSet + ";";
			dbContact.execSQL(strSQLDelete);
			dbContact.close();
			
			return true;
		}
		catch(Exception e)
		{
			dbContact.close();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			
			return false;
		}
	}
	
	/*搜索联系人*/
	private void SearchContact()
	{
		String strSearchConditions = "";  //搜索条件
		
		//根据btnSearch的文本决定搜索的字段，需要避免关键词为空
		if(btnSearchType.getText().equals("简拼") && !txtKeyword.getText().toString().equals(""))
			strSearchConditions = "name_py_simple like '%" + txtKeyword.getText().toString() + "%'";
		else if(btnSearchType.getText().equals("全拼") && !txtKeyword.getText().toString().equals(""))
			strSearchConditions = "name_py_full like '%" + txtKeyword.getText().toString() + "%'";
		else if(btnSearchType.getText().equals("姓名") && !txtKeyword.getText().toString().equals(""))
			strSearchConditions = "name_full like '%" + txtKeyword.getText().toString() + "%'";

		SQLiteDatabase dbContact = GetDBInstance(true);  //数据库实例
		Cursor curContact=dbContact.query("contacts", null, strSearchConditions, null, null, null, "name_py_full");  //按全拼字母顺序排列
    	if(curContact.getCount() > 0)
    	{//找到满足条件的记录
        	List<Map<String, Object>> lst=SetListData(curContact);  //设置Map
        	SimpleAdapter adapter = new SimpleAdapter(srcMain.this, lst, R.layout.extend_list, new String[]{"Name", "PhoneNumber", "UsingTimes"}, new int[]{R.id.labName, R.id.labPhoneNumber, R.id.labUsingTimes});

        	pnlResult.removeAllViews();  //清除可能存在的labInfo
        	pnlResult.addView(lstContact);
        	
        	lstContact.setAdapter(null);  //清除历史Adapter
        	lstContact.setAdapter(adapter);  //重新添加Adapter 
    	}
    	else
    	{
    		TextView labInfo = new TextView(srcMain.this);
    		labInfo.setPadding(15, 0, 0, 0);
    		labInfo.setTextSize(25);
    		labInfo.setShadowLayer(1, 1, 1, Color.parseColor("#0066FF"));  //设置文字阴影
    		labInfo.setTextColor(Color.parseColor("#FFFFFF"));
    		labInfo.setText("没有找到对应的联系人。");

    		pnlResult.removeAllViews();  //先清除所有组件，否则会被lstContact遮挡
    		pnlResult.addView(labInfo);
    	}
    	curContact.close();
    	dbContact.close();
	}
	
    /*将数据库中的内容添加为Map*/
	private List<Map<String, Object>> SetListData(Cursor curContact)
	{
		List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
		while(curContact.moveToNext())
		{
			String strName = curContact.getString(curContact.getColumnIndex("name_full"));
			String strPhoneNumber = curContact.getString(curContact.getColumnIndex("contact_number"));
			String strUsingTimes = curContact.getString(curContact.getColumnIndex("using_times"));

			Map<String, Object> map=new HashMap<String, Object>();
			map.put("Name", strName);
			map.put("PhoneNumber", strPhoneNumber);
			map.put("UsingTimes", "联系" + strUsingTimes + "次");
			lst.add(map);
		}

         return lst;
	}
	
	/*创建ContextMenu*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{//从ListVew打开的菜单项不在此创建
		if(strContextMenuFlag.equals("btnSearchType"))
		{
			menu.setHeaderTitle("选择搜索类型");
			menu.add(0, 0, 0, "简拼");
			menu.add(0, 0, 1, "全拼");
			menu.add(0, 0, 2, "姓名");
		}

		super.onCreateContextMenu(menu, v, menuInfo);				
	}
	
	/*设置ContextMenu事件*/
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		String strTitle = (String)item.getTitle();  //获取菜单标题
		
		if(strContextMenuFlag.equals("btnSearchType"))  //由搜索类型按钮触发
			btnSearchType.setText(item.getTitle());  //将菜单项名（搜索类型）显示到btnSearchType上
		else if(strContextMenuFlag.equals("lstContact"))
		{//由联系人列表控件操作触发
			if(item.getSubMenu() == null)
			{//没有子菜单的情况下直接继续（只有一个电话号码）
				if(item.getOrder() == 0)
					PhoneCall(strPhoneNumber, strName);  //拨打电话
				else if(item.getOrder() == 1)
					SMSSend(strPhoneNumber, strName);  //发送短信
			}
			else
			{//有子菜单（有多个电话号码供选择）
				//通过菜单标题决定子菜单动作类型，用户选择后先设置下一步子菜单的指令
				if(strTitle.equals("给" + strName + "打电话"))
					strCurrOperation = "CALL";
				else if(strTitle.equals("给" + strName + "发短信"))
					strCurrOperation = "SMS";
			}
		}
		else
		{//由子菜单触发，子菜单标题就是电话号码
			if(strCurrOperation.equals("CALL"))
				PhoneCall(strTitle, strName);  //拨打电话
			else if(strCurrOperation.equals("SMS"))
				SMSSend(strTitle, strName);  //发送短信
			
			strCurrOperation = "";  //还原子菜单功能
		}
		strContextMenuFlag = "";  //还原触发控件标志

		return super.onContextItemSelected(item);
	}
	
	/*通过AdapterView获取联系人和电话*/
	public void GetContactInfoByAdapter(AdapterView<?> av, int ItemIndex)
	{
    	Map<String, Object> c = (Map<String, Object>)av.getAdapter().getItem(ItemIndex);  //编译器会发出警告
    	strPhoneNumber = (String)c.get("PhoneNumber");
    	strName = (String)c.get("Name");
    	strUsingTimes = (String)c.get("UsingTimes");
    	strUsingTimes = strUsingTimes.substring(2, strUsingTimes.length()-1);
	}
	
	/*打开系统默认拨号或短信程序*/
	public void OpenSystemCallOrSMS(Context context, String strAction, Uri uri)
	{
    	Intent itt = new Intent(strAction, uri);
    	context.startActivity(itt);
	}
	
	/*增加联系人的联系次数*/
	public void AddContactUsingTimes()
	{
    	strUsingTimes = Integer.toString(Integer.parseInt(strUsingTimes) + 1);  //联系次数 + 1
    	String strSQLModify = "set using_times='" + strUsingTimes + "' where contact_number='" + strPhoneNumber + "';";
    	DBModifiy("contacts", strSQLModify);
	}
	
	/*拨打电话*/
	public void PhoneCall(String TargetNumber, String TargetName)
	{
    	Toast.makeText(srcMain.this, "正在拨号：" + TargetName, Toast.LENGTH_LONG).show();
    	AddContactUsingTimes();  //增加联系次数
    	OpenSystemCallOrSMS(srcMain.this, Intent.ACTION_CALL, Uri.parse("tel:" + TargetNumber));  //调用系统拨号程序
	}
	
	/*发送短信*/
	public void SMSSend(String TargetNumber, String TargetName)
	{
    	Toast.makeText(srcMain.this, "正在启动短信程序，目标：" + TargetName, Toast.LENGTH_LONG).show();
    	AddContactUsingTimes();  //增加联系次数
    	OpenSystemCallOrSMS(srcMain.this, Intent.ACTION_SENDTO, Uri.parse("smsto:" + TargetNumber));  //调用系统短信程序
	}
	
    /*搜索类型按钮单击委托*/
    private OnClickListener CtrlFunc_btnSearchTypeClick=new OnClickListener()
    {
        public void onClick(View v)
        {
        	strContextMenuFlag = "btnSearchType";  //设置触发控件
        	registerForContextMenu(v);
        	openContextMenu(v);
        }
    };
    
    /*联系人列表单击委托*/
    private OnItemClickListener CtrlFunc_lstContactClick= new OnItemClickListener()
    {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        {
			if(ItemSelected && arg1 == LastSelectedItem)
			{//如果有选中项且两次一致
				ItemSelected = false;
				arg1.setBackgroundResource(R.drawable.main_lstcontact_highlight_press);
//				LastSelectedItem = null;
			}
//			else if(ItemSelected && arg1 != LastSelectedItem)
//			{//如果有选中项且两次一致
//				ItemSelected = false;
//				arg1.setBackgroundDrawable(null);  //清除背景高亮
//				LastSelectedItem = null;
//			}
			else
			{
				ItemSelected = true;
				arg1.setBackgroundResource(R.drawable.main_lstcontact_highlight);  //设置背景高亮
				if(LastSelectedItem != null)
					LastSelectedItem.setBackgroundDrawable(null);  //清除上次选中项的背景高亮
				LastSelectedItem = arg1;
			}
        }
    };
    
    /*联系人列表弹出菜单委托*/
    private OnCreateContextMenuListener CtrlFunc_lstContactCreateContextMenu = new OnCreateContextMenuListener()
	{
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
		{
			strContextMenuFlag = "lstContact";  //设置触发控件
			
			menu.setHeaderIcon(R.drawable.icon);
			menu.setHeaderTitle("我想要……");

			String[] strTemp = strPhoneNumber.split(",");  //分割电话
			if(strTemp.length == 1)
			{
				menu.add(0, 0, 0, "给" + strName + "打电话");
				menu.add(0, 0, 1, "给" + strName + "发短信");
			}
			else if(strTemp.length>1)
			{//不止一个电话，将其放入子菜单
				SubMenu sMenuCall=menu.addSubMenu("给" + strName + "打电话");
				SubMenu sMenuSMS=menu.addSubMenu("给" + strName + "发短信");

				for(int i = 0;i < strTemp.length;i++)
				{//将电话添加到子菜单并添加事件处理
					sMenuCall.add(strTemp[i]);
					sMenuSMS.add(strTemp[i]);
				}
			}
		}
    };
    
    /*联系人列表长按委托*/
	private OnItemLongClickListener CtrlFunc_lstContactLongClick=new OnItemLongClickListener()
    {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			ItemSelected = true;
			arg1.setBackgroundResource(R.drawable.main_lstcontact_highlight);  //设置背景高亮
			if(LastSelectedItem != null)
				LastSelectedItem.setBackgroundDrawable(null);  //清除上次选中项的背景高亮
			LastSelectedItem = arg1;
			GetContactInfoByAdapter(arg0, arg2);  //获取姓名和电话号码
			
			return false;
		}
    };

	/*文本框按键委托*/
	private OnKeyListener CtrlFunc_txtKeywordKeyPress=new OnKeyListener()
	{
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
	    	if(keyCode == KeyEvent.KEYCODE_ENTER)
	    		SearchContact();  //按键的同时进行搜索

        	return false;  //为true时不响应物理键盘
		}
	};
	
	/*显示/隐藏子菜单按键委托*/
    private OnClickListener CtrlFunc_btnSubMenuClick=new OnClickListener()
    {
        public void onClick(View v)
        {
        	Animation anim = null;  //动画效果
        	AbsoluteLayout.LayoutParams parResult = (AbsoluteLayout.LayoutParams) pnlResult.getLayoutParams();  //获取pnlResult尺寸参数
        	
        	if(parResult.height == 402)  //隐藏菜单
        	{
        		//设置菜单位置
	    		AbsoluteLayout.LayoutParams parMenu=(AbsoluteLayout.LayoutParams) pnlMenu.getLayoutParams();
	    		parMenu.y = 482;
	    		pnlMenu.setLayoutParams(parMenu);
	    		
	    		//设置结果集位置
	    		parResult.height = 455;
	    		pnlResult.setLayoutParams(parResult);
	    		
        		anim = AnimationUtils.loadAnimation(ctx, R.anim.main_frmmenu_hide);
        	}
        	else if(parResult.height == 455)  //显示菜单
        	{
        		//设置菜单位置
	    		AbsoluteLayout.LayoutParams parMenu=(AbsoluteLayout.LayoutParams) pnlMenu.getLayoutParams();
	    		parMenu.y = 429;
	    		pnlMenu.setLayoutParams(parMenu);
	    		
	    		//设置结果集位置
	    		parResult.height = 402;
	    		pnlResult.setLayoutParams(parResult);
	    		
        		anim = AnimationUtils.loadAnimation(ctx, R.anim.main_frmmenu_show);
        	}
        	
        	pnlMenu.startAnimation(anim);  //执行动画
        }
    };
    
	/*显示/隐藏搜索栏按键委托*/
    private OnClickListener CtrlFunc_btnOpenCloseSearchClick = new OnClickListener()
    {
        public void onClick(View v)
        {
        	Animation animSearch = null;  //搜索栏动画效果
        	Animation animOpenSearch = null;  //打开搜索栏按钮动画效果
        	Animation animCloseSearch = null;  //关闭搜索栏按钮动画效果
        	AbsoluteLayout.LayoutParams parSearch = (AbsoluteLayout.LayoutParams) pnlSearch.getLayoutParams();  //获取frmResult尺寸参数
        	
        	if(parSearch.y == 27)  //隐藏菜单
        	{
        		//设置菜单位置
        		parSearch.y = -38;
        		pnlSearch.setLayoutParams(parSearch);
        		btnOpenSearch.setVisibility(View.VISIBLE);
        		btnCloseSearch.setVisibility(View.GONE);
	    		
        		animSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_frmsearch_hide);
        		animOpenSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_show);
        		animCloseSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_hide);
        	}
        	else if(parSearch.y == -38)  //显示菜单
        	{
        		//设置菜单位置
        		parSearch.y = 27;
        		pnlSearch.setLayoutParams(parSearch);
        		btnOpenSearch.setVisibility(View.GONE);
        		btnCloseSearch.setVisibility(View.VISIBLE);
	    		
        		animSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_frmsearch_show);
        		animOpenSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_hide);
        		animCloseSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_show);
        	}
        	
        	//执行动画
        	pnlSearch.startAnimation(animSearch);
        	btnOpenSearch.startAnimation(animOpenSearch);
        	btnCloseSearch.startAnimation(animCloseSearch);
        }
    };
    
	/*建立索引按钮单击委托*/
    private OnClickListener CtrlFunc_btnIndexingClick = new OnClickListener()
    {
        public void onClick(View v)
        {
			Intent itt = new Intent(ctx, IndexingDialog.class);
			startActivity(itt);
        }
    };
    
	/*退出按钮单击委托*/
    private OnClickListener CtrlFunc_btnExitClick = new OnClickListener()
    {
        public void onClick(View v)
        {
			System.exit(0);
        }
    };
}