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
        //创建悬浮窗口
        createView();
    
    }
    
  
    
    private void createView(){
    	myFV=new MyFloatView(getApplicationContext());
    	myFV.setImageResource(R.drawable.icon);
    	//获取WindowManager
    	wm=(WindowManager)getApplicationContext().getSystemService("window");
        //设置LayoutParams(全局变量）相关参数
    	wmParams = ((MyApplication)getApplication()).getMywmParams();
        wmParams.type=2002;
        //wmParams.format=1;
        wmParams.flags|=8;
        
        
        wmParams.gravity=Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角
        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x=0;
        wmParams.y=0;
        
        //设置悬浮窗口长宽数据
        wmParams.width=40;
        wmParams.height=40;
        
        //显示myFloatView图像
        wm.addView(myFV, wmParams);
    	
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	//在程序退出(Activity销毁）时销毁悬浮窗口
    	wm.removeView(myFV);
    }
    
   
    
}