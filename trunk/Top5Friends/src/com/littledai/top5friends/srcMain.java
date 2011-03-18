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
    /*����ؼ�*/
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
	
	/*��ContextMenuʹ�õ�ѡ������Ϣ*/
	String strPhoneNumber = "";  //�绰����
	String strName = "";  //����
	String strUsingTimes = "";  //��ϵ����
	String strContextMenuFlag = "";  //����ContextMenu�ı�־
	String strCurrOperation = "";  //���Ӳ˵��жϵĵ�ǰ������־
	float CurrX = -1;  //��ǰ����λ��X
	float CurrY = -1;  //��ǰ����λ��Y
	
	Context ctx;  //ȫ�ֵ�Context
	boolean ItemSelected = false;  //�Ƿ�ѡ����lstContact��
	View LastSelectedItem = null;  //��һ��ѡ�е�lstContact��

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        /*���ô�����ʽ�����밴��˳��*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //�ޱ�����
        setContentView(R.layout.afriends);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  //ȫ��
        
        /*����ؼ�ʵ��*/
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
    	
        /*����ؼ�ί��*/
        btnSearchType.setOnClickListener(CtrlFunc_btnSearchTypeClick);
        lstContact.setOnItemClickListener(CtrlFunc_lstContactClick);
        lstContact.setOnItemLongClickListener(CtrlFunc_lstContactLongClick);  //���ȴ��������¼�
        lstContact.setOnCreateContextMenuListener(CtrlFunc_lstContactCreateContextMenu);  //Ȼ��Ž���ContextMenu
        txtKeyword.setOnKeyListener(CtrlFunc_txtKeywordKeyPress);
        btnSubMenu.setOnClickListener(CtrlFunc_btnSubMenuClick);
        btnIndexing.setOnClickListener(CtrlFunc_btnIndexingClick);
        btnExit.setOnClickListener(CtrlFunc_btnExitClick);
        btnOpenSearch.setOnClickListener(CtrlFunc_btnOpenCloseSearchClick);
        btnCloseSearch.setOnClickListener(CtrlFunc_btnOpenCloseSearchClick);

        SearchContact();  //��ʼ��ʱ����ȫ������
        ctx = this.getBaseContext();
    }
	
	/*��ȡ��������*/
    @Override  
    public boolean onTouchEvent(MotionEvent event)
    {
    	CurrX = event.getX();
    	CurrY = event.getY();
    	
        return super.onTouchEvent(event);
    }
    
	/*����OptionMenu*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 0, 0, "������ϵ������").setIcon(R.drawable.index_contact);
		menu.add(0, 0, 1, "����").setIcon(R.drawable.settings);
		menu.add(0, 0, 2, "�˳�ϵͳ").setIcon(R.drawable.exit);
		menu.add(0, 0, 3, "����");  //.setIcon(R.drawable.exit);
		menu.add(0, 0, 4, "����");  //.setIcon(R.drawable.exit);
		menu.add(0, 0, 5, "������");  //.setIcon(R.drawable.exit);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/*����OptionMenu�¼�*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getOrder() == 0)
		{//�򿪽�����������
			Intent itt=new Intent(this.getBaseContext(), IndexingDialog.class);
			startActivity(itt);
		}
		else if(item.getOrder() == 1)
			Toast.makeText(this, "����", Toast.LENGTH_LONG).show();
		else if(item.getOrder() == 2)
			System.exit(0);
		
	    return super.onOptionsItemSelected(item);
	}

	/*��ȡ��ǰ��������ݿ�ʵ��*/
	public SQLiteDatabase GetDBInstance(boolean IsReadOnly)
	{
		SQLiteDatabase db;  //���ݿ�
		
		if(IsReadOnly)  //ֻ��
			db = new DBProvider(getBaseContext()).getReadableDatabase();
		else
			db = new DBProvider(getBaseContext()).getWritableDatabase();
			
		return db;
	}
	
	/*�޸�����*/
	public boolean DBModifiy(String strTable, String strSubStringOfSet)
	{
		SQLiteDatabase dbContact = GetDBInstance(false);  //���ݿ�ʵ��
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
	
	/*������ϵ��*/
	private void SearchContact()
	{
		String strSearchConditions = "";  //��������
		
		//����btnSearch���ı������������ֶΣ���Ҫ����ؼ���Ϊ��
		if(btnSearchType.getText().equals("��ƴ") && !txtKeyword.getText().toString().equals(""))
			strSearchConditions = "name_py_simple like '%" + txtKeyword.getText().toString() + "%'";
		else if(btnSearchType.getText().equals("ȫƴ") && !txtKeyword.getText().toString().equals(""))
			strSearchConditions = "name_py_full like '%" + txtKeyword.getText().toString() + "%'";
		else if(btnSearchType.getText().equals("����") && !txtKeyword.getText().toString().equals(""))
			strSearchConditions = "name_full like '%" + txtKeyword.getText().toString() + "%'";

		SQLiteDatabase dbContact = GetDBInstance(true);  //���ݿ�ʵ��
		Cursor curContact=dbContact.query("contacts", null, strSearchConditions, null, null, null, "name_py_full");  //��ȫƴ��ĸ˳������
    	if(curContact.getCount() > 0)
    	{//�ҵ����������ļ�¼
        	List<Map<String, Object>> lst=SetListData(curContact);  //����Map
        	SimpleAdapter adapter = new SimpleAdapter(srcMain.this, lst, R.layout.extend_list, new String[]{"Name", "PhoneNumber", "UsingTimes"}, new int[]{R.id.labName, R.id.labPhoneNumber, R.id.labUsingTimes});

        	pnlResult.removeAllViews();  //������ܴ��ڵ�labInfo
        	pnlResult.addView(lstContact);
        	
        	lstContact.setAdapter(null);  //�����ʷAdapter
        	lstContact.setAdapter(adapter);  //�������Adapter 
    	}
    	else
    	{
    		TextView labInfo = new TextView(srcMain.this);
    		labInfo.setPadding(15, 0, 0, 0);
    		labInfo.setTextSize(25);
    		labInfo.setShadowLayer(1, 1, 1, Color.parseColor("#0066FF"));  //����������Ӱ
    		labInfo.setTextColor(Color.parseColor("#FFFFFF"));
    		labInfo.setText("û���ҵ���Ӧ����ϵ�ˡ�");

    		pnlResult.removeAllViews();  //������������������ᱻlstContact�ڵ�
    		pnlResult.addView(labInfo);
    	}
    	curContact.close();
    	dbContact.close();
	}
	
    /*�����ݿ��е��������ΪMap*/
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
			map.put("UsingTimes", "��ϵ" + strUsingTimes + "��");
			lst.add(map);
		}

         return lst;
	}
	
	/*����ContextMenu*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{//��ListVew�򿪵Ĳ˵���ڴ˴���
		if(strContextMenuFlag.equals("btnSearchType"))
		{
			menu.setHeaderTitle("ѡ����������");
			menu.add(0, 0, 0, "��ƴ");
			menu.add(0, 0, 1, "ȫƴ");
			menu.add(0, 0, 2, "����");
		}

		super.onCreateContextMenu(menu, v, menuInfo);				
	}
	
	/*����ContextMenu�¼�*/
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		String strTitle = (String)item.getTitle();  //��ȡ�˵�����
		
		if(strContextMenuFlag.equals("btnSearchType"))  //���������Ͱ�ť����
			btnSearchType.setText(item.getTitle());  //���˵��������������ͣ���ʾ��btnSearchType��
		else if(strContextMenuFlag.equals("lstContact"))
		{//����ϵ���б�ؼ���������
			if(item.getSubMenu() == null)
			{//û���Ӳ˵��������ֱ�Ӽ�����ֻ��һ���绰���룩
				if(item.getOrder() == 0)
					PhoneCall(strPhoneNumber, strName);  //����绰
				else if(item.getOrder() == 1)
					SMSSend(strPhoneNumber, strName);  //���Ͷ���
			}
			else
			{//���Ӳ˵����ж���绰���빩ѡ��
				//ͨ���˵���������Ӳ˵��������ͣ��û�ѡ�����������һ���Ӳ˵���ָ��
				if(strTitle.equals("��" + strName + "��绰"))
					strCurrOperation = "CALL";
				else if(strTitle.equals("��" + strName + "������"))
					strCurrOperation = "SMS";
			}
		}
		else
		{//���Ӳ˵��������Ӳ˵�������ǵ绰����
			if(strCurrOperation.equals("CALL"))
				PhoneCall(strTitle, strName);  //����绰
			else if(strCurrOperation.equals("SMS"))
				SMSSend(strTitle, strName);  //���Ͷ���
			
			strCurrOperation = "";  //��ԭ�Ӳ˵�����
		}
		strContextMenuFlag = "";  //��ԭ�����ؼ���־

		return super.onContextItemSelected(item);
	}
	
	/*ͨ��AdapterView��ȡ��ϵ�˺͵绰*/
	public void GetContactInfoByAdapter(AdapterView<?> av, int ItemIndex)
	{
    	Map<String, Object> c = (Map<String, Object>)av.getAdapter().getItem(ItemIndex);  //�������ᷢ������
    	strPhoneNumber = (String)c.get("PhoneNumber");
    	strName = (String)c.get("Name");
    	strUsingTimes = (String)c.get("UsingTimes");
    	strUsingTimes = strUsingTimes.substring(2, strUsingTimes.length()-1);
	}
	
	/*��ϵͳĬ�ϲ��Ż���ų���*/
	public void OpenSystemCallOrSMS(Context context, String strAction, Uri uri)
	{
    	Intent itt = new Intent(strAction, uri);
    	context.startActivity(itt);
	}
	
	/*������ϵ�˵���ϵ����*/
	public void AddContactUsingTimes()
	{
    	strUsingTimes = Integer.toString(Integer.parseInt(strUsingTimes) + 1);  //��ϵ���� + 1
    	String strSQLModify = "set using_times='" + strUsingTimes + "' where contact_number='" + strPhoneNumber + "';";
    	DBModifiy("contacts", strSQLModify);
	}
	
	/*����绰*/
	public void PhoneCall(String TargetNumber, String TargetName)
	{
    	Toast.makeText(srcMain.this, "���ڲ��ţ�" + TargetName, Toast.LENGTH_LONG).show();
    	AddContactUsingTimes();  //������ϵ����
    	OpenSystemCallOrSMS(srcMain.this, Intent.ACTION_CALL, Uri.parse("tel:" + TargetNumber));  //����ϵͳ���ų���
	}
	
	/*���Ͷ���*/
	public void SMSSend(String TargetNumber, String TargetName)
	{
    	Toast.makeText(srcMain.this, "�����������ų���Ŀ�꣺" + TargetName, Toast.LENGTH_LONG).show();
    	AddContactUsingTimes();  //������ϵ����
    	OpenSystemCallOrSMS(srcMain.this, Intent.ACTION_SENDTO, Uri.parse("smsto:" + TargetNumber));  //����ϵͳ���ų���
	}
	
    /*�������Ͱ�ť����ί��*/
    private OnClickListener CtrlFunc_btnSearchTypeClick=new OnClickListener()
    {
        public void onClick(View v)
        {
        	strContextMenuFlag = "btnSearchType";  //���ô����ؼ�
        	registerForContextMenu(v);
        	openContextMenu(v);
        }
    };
    
    /*��ϵ���б���ί��*/
    private OnItemClickListener CtrlFunc_lstContactClick= new OnItemClickListener()
    {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        {
			if(ItemSelected && arg1 == LastSelectedItem)
			{//�����ѡ����������һ��
				ItemSelected = false;
				arg1.setBackgroundResource(R.drawable.main_lstcontact_highlight_press);
//				LastSelectedItem = null;
			}
//			else if(ItemSelected && arg1 != LastSelectedItem)
//			{//�����ѡ����������һ��
//				ItemSelected = false;
//				arg1.setBackgroundDrawable(null);  //�����������
//				LastSelectedItem = null;
//			}
			else
			{
				ItemSelected = true;
				arg1.setBackgroundResource(R.drawable.main_lstcontact_highlight);  //���ñ�������
				if(LastSelectedItem != null)
					LastSelectedItem.setBackgroundDrawable(null);  //����ϴ�ѡ����ı�������
				LastSelectedItem = arg1;
			}
        }
    };
    
    /*��ϵ���б����˵�ί��*/
    private OnCreateContextMenuListener CtrlFunc_lstContactCreateContextMenu = new OnCreateContextMenuListener()
	{
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
		{
			strContextMenuFlag = "lstContact";  //���ô����ؼ�
			
			menu.setHeaderIcon(R.drawable.icon);
			menu.setHeaderTitle("����Ҫ����");

			String[] strTemp = strPhoneNumber.split(",");  //�ָ�绰
			if(strTemp.length == 1)
			{
				menu.add(0, 0, 0, "��" + strName + "��绰");
				menu.add(0, 0, 1, "��" + strName + "������");
			}
			else if(strTemp.length>1)
			{//��ֹһ���绰����������Ӳ˵�
				SubMenu sMenuCall=menu.addSubMenu("��" + strName + "��绰");
				SubMenu sMenuSMS=menu.addSubMenu("��" + strName + "������");

				for(int i = 0;i < strTemp.length;i++)
				{//���绰��ӵ��Ӳ˵�������¼�����
					sMenuCall.add(strTemp[i]);
					sMenuSMS.add(strTemp[i]);
				}
			}
		}
    };
    
    /*��ϵ���б���ί��*/
	private OnItemLongClickListener CtrlFunc_lstContactLongClick=new OnItemLongClickListener()
    {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			ItemSelected = true;
			arg1.setBackgroundResource(R.drawable.main_lstcontact_highlight);  //���ñ�������
			if(LastSelectedItem != null)
				LastSelectedItem.setBackgroundDrawable(null);  //����ϴ�ѡ����ı�������
			LastSelectedItem = arg1;
			GetContactInfoByAdapter(arg0, arg2);  //��ȡ�����͵绰����
			
			return false;
		}
    };

	/*�ı��򰴼�ί��*/
	private OnKeyListener CtrlFunc_txtKeywordKeyPress=new OnKeyListener()
	{
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
	    	if(keyCode == KeyEvent.KEYCODE_ENTER)
	    		SearchContact();  //������ͬʱ��������

        	return false;  //Ϊtrueʱ����Ӧ�������
		}
	};
	
	/*��ʾ/�����Ӳ˵�����ί��*/
    private OnClickListener CtrlFunc_btnSubMenuClick=new OnClickListener()
    {
        public void onClick(View v)
        {
        	Animation anim = null;  //����Ч��
        	AbsoluteLayout.LayoutParams parResult = (AbsoluteLayout.LayoutParams) pnlResult.getLayoutParams();  //��ȡpnlResult�ߴ����
        	
        	if(parResult.height == 402)  //���ز˵�
        	{
        		//���ò˵�λ��
	    		AbsoluteLayout.LayoutParams parMenu=(AbsoluteLayout.LayoutParams) pnlMenu.getLayoutParams();
	    		parMenu.y = 482;
	    		pnlMenu.setLayoutParams(parMenu);
	    		
	    		//���ý����λ��
	    		parResult.height = 455;
	    		pnlResult.setLayoutParams(parResult);
	    		
        		anim = AnimationUtils.loadAnimation(ctx, R.anim.main_frmmenu_hide);
        	}
        	else if(parResult.height == 455)  //��ʾ�˵�
        	{
        		//���ò˵�λ��
	    		AbsoluteLayout.LayoutParams parMenu=(AbsoluteLayout.LayoutParams) pnlMenu.getLayoutParams();
	    		parMenu.y = 429;
	    		pnlMenu.setLayoutParams(parMenu);
	    		
	    		//���ý����λ��
	    		parResult.height = 402;
	    		pnlResult.setLayoutParams(parResult);
	    		
        		anim = AnimationUtils.loadAnimation(ctx, R.anim.main_frmmenu_show);
        	}
        	
        	pnlMenu.startAnimation(anim);  //ִ�ж���
        }
    };
    
	/*��ʾ/��������������ί��*/
    private OnClickListener CtrlFunc_btnOpenCloseSearchClick = new OnClickListener()
    {
        public void onClick(View v)
        {
        	Animation animSearch = null;  //����������Ч��
        	Animation animOpenSearch = null;  //����������ť����Ч��
        	Animation animCloseSearch = null;  //�ر���������ť����Ч��
        	AbsoluteLayout.LayoutParams parSearch = (AbsoluteLayout.LayoutParams) pnlSearch.getLayoutParams();  //��ȡfrmResult�ߴ����
        	
        	if(parSearch.y == 27)  //���ز˵�
        	{
        		//���ò˵�λ��
        		parSearch.y = -38;
        		pnlSearch.setLayoutParams(parSearch);
        		btnOpenSearch.setVisibility(View.VISIBLE);
        		btnCloseSearch.setVisibility(View.GONE);
	    		
        		animSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_frmsearch_hide);
        		animOpenSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_show);
        		animCloseSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_hide);
        	}
        	else if(parSearch.y == -38)  //��ʾ�˵�
        	{
        		//���ò˵�λ��
        		parSearch.y = 27;
        		pnlSearch.setLayoutParams(parSearch);
        		btnOpenSearch.setVisibility(View.GONE);
        		btnCloseSearch.setVisibility(View.VISIBLE);
	    		
        		animSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_frmsearch_show);
        		animOpenSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_hide);
        		animCloseSearch = AnimationUtils.loadAnimation(ctx, R.anim.main_openclosesearch_show);
        	}
        	
        	//ִ�ж���
        	pnlSearch.startAnimation(animSearch);
        	btnOpenSearch.startAnimation(animOpenSearch);
        	btnCloseSearch.startAnimation(animCloseSearch);
        }
    };
    
	/*����������ť����ί��*/
    private OnClickListener CtrlFunc_btnIndexingClick = new OnClickListener()
    {
        public void onClick(View v)
        {
			Intent itt = new Intent(ctx, IndexingDialog.class);
			startActivity(itt);
        }
    };
    
	/*�˳���ť����ί��*/
    private OnClickListener CtrlFunc_btnExitClick = new OnClickListener()
    {
        public void onClick(View v)
        {
			System.exit(0);
        }
    };
}