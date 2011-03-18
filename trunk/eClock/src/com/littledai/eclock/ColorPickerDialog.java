package com.littledai.eclock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColorPickerDialog extends AlertDialog implements OnClickListener
{
	/*公共变量*/
	private OnClickListener ClickListener;
	private View v;
	ColorPickerView cpv;

	/*界面控件*/
	LinearLayout frmMain;
	LinearLayout frmColor;
	LinearLayout frmValue;
	TextView labValue;
	EditText txtValue;

	public ColorPickerDialog(Context context,View view,int color,OnClickListener listener)
	{
		super(context);

		v=view;
		ClickListener=listener;
		cpv=new ColorPickerView(context,color);
		Resources res=context.getResources();
		setTitle(res.getText(R.string.title_date_color));
		setButton(BUTTON_POSITIVE,res.getText(android.R.string.ok),this);
		setButton(BUTTON_NEGATIVE,res.getText(android.R.string.cancel),this);
		
		//设置布局
		frmMain=new LinearLayout(context);
		frmMain.setPadding(10,10,10,10);
		frmMain.setOrientation(1);  //垂直排列
		
		frmColor=new LinearLayout(context);
		frmColor.setPadding(10,0,10,0);
		frmColor.addView(cpv);  //添加颜色选择视图
		
		frmValue=new LinearLayout(context);
		labValue=new TextView(context);  //标签
		labValue.setText("当前颜色：");
		frmValue.addView(labValue);  //添加标签
		
		txtValue=new EditText(context);  //文本框
		txtValue.setWidth(350);  //使文本框能撑满的长度
		txtValue.setText("#"+Integer.toHexString(color).toUpperCase().substring(2));
		frmValue.addView(txtValue);  //添加文本框
		
		frmMain.addView(frmColor);  //添加颜色数值视图
		frmMain.addView(frmValue);  //添加颜色数值视图

		setView(frmMain);
		
		txtValue.setOnKeyListener(new View.OnKeyListener()
		{
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				try
				{
					if(event.getAction()==1)
					{//KeyUp事件
						String strColor=txtValue.getText().toString().trim();  //获取文本框内容
						
						if(strColor.length()>=8)
						{//不允许超过7个字符（#000000）
							strColor=strColor.substring(0,7);
							txtValue.setText(strColor);
							
							//将光标移动到最后一个字符的后面
							txtValue.clearFocus();
							txtValue.setFocusable(true);
						}
						
						if(strColor.indexOf("#")!=0)
						{//若首字符不为“#”则补充
							txtValue.setText("#"+strColor);
							
							//将光标移动到最后一个字符的后面
							txtValue.clearFocus();
							txtValue.setFocusable(true);
						}
						cpv.paintCenter.setColor(Color.parseColor(txtValue.getText().toString()));  //更新视图中选中的颜色
						cpv.invalidate();  //刷新视图
					}
				}
				catch(Exception e)
				{
					
				}

				return false;
			}
		});
	}

    public interface OnClickListener
    {
        public void onClick(View view,int color);
    }
    
	public void onClick(DialogInterface dialog,int which) 
	{
		if(which==DialogInterface.BUTTON_POSITIVE) ClickListener.onClick(v,cpv.paintCenter.getColor());  //确定后执行回调函数
		dismiss();
	}

	/*颜色选择视图*/
	private static class ColorPickerView extends View
    {
		//画笔
		private Paint paintCircleShadow=new Paint(Paint.ANTI_ALIAS_FLAG);  //颜色环阴影
        private Paint paintCircle=new Paint(Paint.ANTI_ALIAS_FLAG);  //颜色环
        private Paint paintCenterShadow=new Paint(Paint.ANTI_ALIAS_FLAG);  //中心颜色预览阴影
        private Paint paintCenter=new Paint(Paint.ANTI_ALIAS_FLAG);  //中心颜色预览
        private Paint paintGrayShadow=new Paint(Paint.ANTI_ALIAS_FLAG);  //颜色灰度阴影
        private Paint paintGray=new Paint(Paint.ANTI_ALIAS_FLAG);  //颜色灰度
        
        //颜色
        private int[] arrColorGray;  //颜色灰度所包含的颜色数组
        private final int[] arrColorCircle=new int[]{0xFFFF0000,0xFFFF00FF,0xFF0000FF,0xFF00FFFF,0xFF00FF00,0xFFFFFF00,
        		0xFFFF0000};  //颜色环所包含的颜色数组

        //绘图许可标志
        private boolean mRedrawHSV;
        private boolean IsPressCenter;  //手指位置是否按在圆心
        private boolean IsMoveCenter;  //手指位置是否位于圆心
        
        //位置常量
        private static final int CENTER_X=100;
        private static final int CENTER_Y=100;
        private static final int CENTER_RADIUS=30;
        private static final float PI = 3.1415926f;  //圆周率

        ColorPickerView(Context context,int color)
        {
            super(context);
            
            //颜色环阴影
            paintCircleShadow.setColor(0xFF000000);
            paintCircleShadow.setStyle(Paint.Style.STROKE);
            paintCircleShadow.setStrokeWidth(32);

            //颜色环
            paintCircle.setShader(new SweepGradient(0,0,arrColorCircle,null));
            paintCircle.setStyle(Paint.Style.STROKE);
            paintCircle.setStrokeWidth(32);
            
            //颜色预览阴影
            paintCenterShadow.setColor(0xFF000000);
            paintCenterShadow.setStrokeWidth(5);

            //颜色预览
            paintCenter.setColor(color);
            paintCenter.setStrokeWidth(5);
            
            //颜色灰度阴影
            paintGrayShadow.setColor(0xFF000000);
            paintGrayShadow.setStrokeWidth(20);

            //颜色灰度
            arrColorGray=new int[]{0xFFFFFFFF,color,0xFF000000};  //设置颜色灰度等级
            paintGray.setStrokeWidth(20);

            mRedrawHSV=true;
        }

        /*重写绘图事件*/
        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.translate(CENTER_X,CENTER_X);
            float r=CENTER_X-paintCircle.getStrokeWidth()*0.5f;  //半径
            int color=paintCenter.getColor();
            
            if(mRedrawHSV)
            {
            	arrColorGray[1]=color;
            	paintGray.setShader(new LinearGradient(CENTER_X,-CENTER_Y,CENTER_X,100,arrColorGray,null,Shader.TileMode.CLAMP));
            }

            //绘图
            canvas.drawOval(new RectF(-r+3,-r+3,r+3,r+3),paintCircleShadow);  //颜色环阴影
            canvas.drawOval(new RectF(-r,-r,r,r),paintCircle);  //颜色环
            canvas.drawCircle(3,3,CENTER_RADIUS,paintCenterShadow);  //颜色预览阴影
            canvas.drawCircle(0,0,CENTER_RADIUS,paintCenter);  //颜色预览
            canvas.drawRect(new RectF(CENTER_X+18,-CENTER_Y+3,CENTER_X+48,103),paintGrayShadow);  //灰度等级阴影
            canvas.drawRect(new RectF(CENTER_X+15,-CENTER_Y,CENTER_X+45,100),paintGray);  //灰度等级

            if(IsPressCenter)
            {//如果手指单击了圆心，准备围绕预览颜色画圈
                paintCenter.setStyle(Paint.Style.STROKE);  //画圈样式

                if(IsMoveCenter)  //手指在中心
                    paintCenter.setAlpha(0xFF);
                else  //手指移开中心
                    paintCenter.setAlpha(0x66);
                
                canvas.drawCircle(0,0,CENTER_RADIUS+paintCenter.getStrokeWidth(),paintCenter);  //画圈
                paintCenter.setStyle(Paint.Style.FILL);  //还原为填充样式
                paintCenter.setColor(color);  //还原先前的颜色（防止被透明处理）
            }

            mRedrawHSV = true;
        }

        /*控制视图大小*/
        @Override
        protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec)
        {
            setMeasuredDimension(CENTER_X*2+50,CENTER_Y*2+23);
        }

        private int ave(int s,int d,float p)
        {
            return s+java.lang.Math.round(p*(d-s));
        }

        private int interpColor(int colors[],float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int) p;
            p -= i;

            int c0 = colors[i];
            int c1 = colors[i + 1];
            int a = ave(Color.alpha(c0),Color.alpha(c1),p);
            int r = ave(Color.red(c0),Color.red(c1),p);
            int g = ave(Color.green(c0),Color.green(c1),p);
            int b = ave(Color.blue(c0),Color.blue(c1),p);

            return Color.argb(a,r,g,b);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float x=event.getX()-CENTER_X;
            float y=event.getY()-CENTER_Y;
            boolean inCenter=java.lang.Math.sqrt(x*x+y*y)<=CENTER_RADIUS;

            switch(event.getAction())
            {
            	case MotionEvent.ACTION_DOWN:
            		IsPressCenter=inCenter;
            		if(inCenter)
            		{
            			IsMoveCenter=true;
            			invalidate();
            			break;
            		}
            	case MotionEvent.ACTION_MOVE:
            		if (IsPressCenter)
            		{
            			if(IsMoveCenter!=inCenter)
            			{
	                    	IsMoveCenter=inCenter;
	                        invalidate();
            			}
            		}
            		else if((x>=-CENTER_X && x<=CENTER_X) && (y>=-CENTER_Y && y<=CENTER_Y))
            		{//计算颜色环上的颜色
	                    float angle=(float) java.lang.Math.atan2(y,x);
	                    float unit=angle/(2*PI);
	                    if(unit<0) unit+=1;
	                    paintCenter.setColor(interpColor(arrColorCircle,unit));
	                    invalidate();
	                }
	                else
	                {//计算灰度条上的颜色
            			int a,r,g,b,c0,c1;
            			float p;

	                    if (y<0)
	                    {
	                        c0=arrColorGray[0];
	                        c1=arrColorGray[1];
	                        p=(y+100)/100;
	                    }
	                    else
	                    {
	                        c0=arrColorGray[1];
	                        c1=arrColorGray[2];
	                        p=y/100;
	                    }
	
	                    a=ave(Color.alpha(c0),Color.alpha(c1),p);
	                    r=ave(Color.red(c0),Color.red(c1),p);
	                    g=ave(Color.green(c0),Color.green(c1),p);
	                    b=ave(Color.blue(c0),Color.blue(c1),p);
	
	                    paintCenter.setColor(Color.argb(a,r,g,b));
	                    mRedrawHSV = false;
	                    invalidate();
	                }
            		
            		break;
            	case MotionEvent.ACTION_UP:
	                if(IsPressCenter)
	                {
	                	IsPressCenter=false;
	                    invalidate();
	                }
	                break;
            }
            
            return true;
        }
    }
}
