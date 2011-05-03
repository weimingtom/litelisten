package net.xsmile.fv;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

public class MyFloatViewActivity extends Activity {
    /** Called when the activity is first created. */
	
	WindowManager wm=null;
	WindowManager.LayoutParams wmParams=null;
	
	MyFloatView myFV=null;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //������������
        createView();
    
    }
    
  
    
    private void createView(){
    	myFV=new MyFloatView(getApplicationContext());
    	myFV.setImageResource(R.drawable.icon);
    	//��ȡWindowManager
    	wm=(WindowManager)getApplicationContext().getSystemService("window");
        //����LayoutParams(ȫ�ֱ�������ز���
    	wmParams = ((MyApplication)getApplication()).getMywmParams();
        wmParams.type=2002;
        //wmParams.format=1;
        wmParams.flags|=8;
        
        
        wmParams.gravity=Gravity.LEFT|Gravity.TOP;   //�����������������Ͻ�
        //����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
        wmParams.x=0;
        wmParams.y=0;
        
        //�����������ڳ�������
        wmParams.width=40;
        wmParams.height=40;
        
        //��ʾmyFloatViewͼ��
        wm.addView(myFV, wmParams);
    	
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	//�ڳ����˳�(Activity���٣�ʱ������������
    	wm.removeView(myFV);
    }
    
   
    
}