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
	/*��������*/
	private OnClickListener ClickListener;
	private View v;
	ColorPickerView cpv;

	/*����ؼ�*/
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
		
		//���ò���
		frmMain=new LinearLayout(context);
		frmMain.setPadding(10,10,10,10);
		frmMain.setOrientation(1);  //��ֱ����
		
		frmColor=new LinearLayout(context);
		frmColor.setPadding(10,0,10,0);
		frmColor.addView(cpv);  //�����ɫѡ����ͼ
		
		frmValue=new LinearLayout(context);
		labValue=new TextView(context);  //��ǩ
		labValue.setText("��ǰ��ɫ��");
		frmValue.addView(labValue);  //��ӱ�ǩ
		
		txtValue=new EditText(context);  //�ı���
		txtValue.setWidth(350);  //ʹ�ı����ܳ����ĳ���
		txtValue.setText("#"+Integer.toHexString(color).toUpperCase().substring(2));
		frmValue.addView(txtValue);  //����ı���
		
		frmMain.addView(frmColor);  //�����ɫ��ֵ��ͼ
		frmMain.addView(frmValue);  //�����ɫ��ֵ��ͼ

		setView(frmMain);
		
		txtValue.setOnKeyListener(new View.OnKeyListener()
		{
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				try
				{
					if(event.getAction()==1)
					{//KeyUp�¼�
						String strColor=txtValue.getText().toString().trim();  //��ȡ�ı�������
						
						if(strColor.length()>=8)
						{//��������7���ַ���#000000��
							strColor=strColor.substring(0,7);
							txtValue.setText(strColor);
							
							//������ƶ������һ���ַ��ĺ���
							txtValue.clearFocus();
							txtValue.setFocusable(true);
						}
						
						if(strColor.indexOf("#")!=0)
						{//�����ַ���Ϊ��#���򲹳�
							txtValue.setText("#"+strColor);
							
							//������ƶ������һ���ַ��ĺ���
							txtValue.clearFocus();
							txtValue.setFocusable(true);
						}
						cpv.paintCenter.setColor(Color.parseColor(txtValue.getText().toString()));  //������ͼ��ѡ�е���ɫ
						cpv.invalidate();  //ˢ����ͼ
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
		if(which==DialogInterface.BUTTON_POSITIVE) ClickListener.onClick(v,cpv.paintCenter.getColor());  //ȷ����ִ�лص�����
		dismiss();
	}

	/*��ɫѡ����ͼ*/
	private static class ColorPickerView extends View
    {
		//����
		private Paint paintCircleShadow=new Paint(Paint.ANTI_ALIAS_FLAG);  //��ɫ����Ӱ
        private Paint paintCircle=new Paint(Paint.ANTI_ALIAS_FLAG);  //��ɫ��
        private Paint paintCenterShadow=new Paint(Paint.ANTI_ALIAS_FLAG);  //������ɫԤ����Ӱ
        private Paint paintCenter=new Paint(Paint.ANTI_ALIAS_FLAG);  //������ɫԤ��
        private Paint paintGrayShadow=new Paint(Paint.ANTI_ALIAS_FLAG);  //��ɫ�Ҷ���Ӱ
        private Paint paintGray=new Paint(Paint.ANTI_ALIAS_FLAG);  //��ɫ�Ҷ�
        
        //��ɫ
        private int[] arrColorGray;  //��ɫ�Ҷ�����������ɫ����
        private final int[] arrColorCircle=new int[]{0xFFFF0000,0xFFFF00FF,0xFF0000FF,0xFF00FFFF,0xFF00FF00,0xFFFFFF00,
        		0xFFFF0000};  //��ɫ������������ɫ����

        //��ͼ��ɱ�־
        private boolean mRedrawHSV;
        private boolean IsPressCenter;  //��ָλ���Ƿ���Բ��
        private boolean IsMoveCenter;  //��ָλ���Ƿ�λ��Բ��
        
        //λ�ó���
        private static final int CENTER_X=100;
        private static final int CENTER_Y=100;
        private static final int CENTER_RADIUS=30;
        private static final float PI = 3.1415926f;  //Բ����

        ColorPickerView(Context context,int color)
        {
            super(context);
            
            //��ɫ����Ӱ
            paintCircleShadow.setColor(0xFF000000);
            paintCircleShadow.setStyle(Paint.Style.STROKE);
            paintCircleShadow.setStrokeWidth(32);

            //��ɫ��
            paintCircle.setShader(new SweepGradient(0,0,arrColorCircle,null));
            paintCircle.setStyle(Paint.Style.STROKE);
            paintCircle.setStrokeWidth(32);
            
            //��ɫԤ����Ӱ
            paintCenterShadow.setColor(0xFF000000);
            paintCenterShadow.setStrokeWidth(5);

            //��ɫԤ��
            paintCenter.setColor(color);
            paintCenter.setStrokeWidth(5);
            
            //��ɫ�Ҷ���Ӱ
            paintGrayShadow.setColor(0xFF000000);
            paintGrayShadow.setStrokeWidth(20);

            //��ɫ�Ҷ�
            arrColorGray=new int[]{0xFFFFFFFF,color,0xFF000000};  //������ɫ�Ҷȵȼ�
            paintGray.setStrokeWidth(20);

            mRedrawHSV=true;
        }

        /*��д��ͼ�¼�*/
        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.translate(CENTER_X,CENTER_X);
            float r=CENTER_X-paintCircle.getStrokeWidth()*0.5f;  //�뾶
            int color=paintCenter.getColor();
            
            if(mRedrawHSV)
            {
            	arrColorGray[1]=color;
            	paintGray.setShader(new LinearGradient(CENTER_X,-CENTER_Y,CENTER_X,100,arrColorGray,null,Shader.TileMode.CLAMP));
            }

            //��ͼ
            canvas.drawOval(new RectF(-r+3,-r+3,r+3,r+3),paintCircleShadow);  //��ɫ����Ӱ
            canvas.drawOval(new RectF(-r,-r,r,r),paintCircle);  //��ɫ��
            canvas.drawCircle(3,3,CENTER_RADIUS,paintCenterShadow);  //��ɫԤ����Ӱ
            canvas.drawCircle(0,0,CENTER_RADIUS,paintCenter);  //��ɫԤ��
            canvas.drawRect(new RectF(CENTER_X+18,-CENTER_Y+3,CENTER_X+48,103),paintGrayShadow);  //�Ҷȵȼ���Ӱ
            canvas.drawRect(new RectF(CENTER_X+15,-CENTER_Y,CENTER_X+45,100),paintGray);  //�Ҷȵȼ�

            if(IsPressCenter)
            {//�����ָ������Բ�ģ�׼��Χ��Ԥ����ɫ��Ȧ
                paintCenter.setStyle(Paint.Style.STROKE);  //��Ȧ��ʽ

                if(IsMoveCenter)  //��ָ������
                    paintCenter.setAlpha(0xFF);
                else  //��ָ�ƿ�����
                    paintCenter.setAlpha(0x66);
                
                canvas.drawCircle(0,0,CENTER_RADIUS+paintCenter.getStrokeWidth(),paintCenter);  //��Ȧ
                paintCenter.setStyle(Paint.Style.FILL);  //��ԭΪ�����ʽ
                paintCenter.setColor(color);  //��ԭ��ǰ����ɫ����ֹ��͸������
            }

            mRedrawHSV = true;
        }

        /*������ͼ��С*/
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
            		{//������ɫ���ϵ���ɫ
	                    float angle=(float) java.lang.Math.atan2(y,x);
	                    float unit=angle/(2*PI);
	                    if(unit<0) unit+=1;
	                    paintCenter.setColor(interpColor(arrColorCircle,unit));
	                    invalidate();
	                }
	                else
	                {//����Ҷ����ϵ���ɫ
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
