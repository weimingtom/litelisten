package com.littledai.litelisten;

import android.app.AlertDialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LDAlertDialog
{
	private Context context = null;
	private AlertDialog dlg = null;
	private String Title = "";
	private String Message = "";
	private OnClickListener onOK = null;
	private OnClickListener onCancel = null;

	public LDAlertDialog(Context context)
	{
		this.context = context;
	}

	public void ShowDialog(String Title, String Message, OnClickListener onOK, OnClickListener onCancel)
	{
		// ��¼��ǰ�����Ա�ָ�
		this.Title = Title;
		this.Message = Message;
		this.onOK = onOK;
		this.onCancel = onCancel;

		dlg = new AlertDialog.Builder(context).show();
		dlg.getWindow().setContentView(R.layout.alertdialog);

		// ����ͼ��
		ImageView imgIcon = (ImageView) dlg.getWindow().findViewById(R.id.imgIcon);
		imgIcon.setBackgroundResource(R.drawable.album_normal);

		// ���öԻ������
		TextView txtTitle = (TextView) dlg.getWindow().findViewById(R.id.txtTitle);
		txtTitle.setText(Title);

		// ������ʾ��Ϣ
		TextView txtMessage = (TextView) dlg.getWindow().findViewById(R.id.txtMessage);
		txtMessage.setText(Message);
		txtMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

		// ����ȷ����ť
		Button btnOK = (Button) dlg.getWindow().findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOK);

		// ����ȡ����ť
		Button btnCancel = (Button) dlg.getWindow().findViewById(R.id.btnCancel);
		if (onCancel != null)
			btnCancel.setOnClickListener(onCancel);
		else
			btnCancel.setVisibility(View.GONE); // ���ɼ�
	}

	public void ChangeLayout()
	{
		if (dlg != null && dlg.isShowing())
		{
			dlg.getWindow().setContentView(R.layout.alertdialog);

			// ����ͼ��
			ImageView imgIcon = (ImageView) dlg.getWindow().findViewById(R.id.imgIcon);
			imgIcon.setBackgroundResource(R.drawable.album_normal);

			// ���öԻ������
			TextView txtTitle = (TextView) dlg.getWindow().findViewById(R.id.txtTitle);
			txtTitle.setText(Title);

			// ������ʾ��Ϣ
			TextView txtMessage = (TextView) dlg.getWindow().findViewById(R.id.txtMessage);
			txtMessage.setText(Message);
			txtMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

			// ����ȷ����ť
			Button btnOK = (Button) dlg.getWindow().findViewById(R.id.btnOK);
			btnOK.setOnClickListener(onOK);

			// ����ȡ����ť
			Button btnCancel = (Button) dlg.getWindow().findViewById(R.id.btnCancel);
			if (onCancel != null)
				btnCancel.setOnClickListener(onCancel);
			else
				btnCancel.setVisibility(View.GONE); // ���ɼ�
		}
	}

	public void CloseDialog()
	{
		if (dlg != null && dlg.isShowing())
			dlg.cancel();
	}

	public AlertDialog getDlg()
	{
		return dlg;
	}

	public void setDlg(AlertDialog dlg)
	{
		this.dlg = dlg;
	}

	public Context getContext()
	{
		return context;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	public String getTitle()
	{
		return Title;
	}

	public void setTitle(String title)
	{
		Title = title;
	}

	public String getMessage()
	{
		return Message;
	}

	public void setMessage(String message)
	{
		Message = message;
	}

	public OnClickListener getOnOK()
	{
		return onOK;
	}

	public void setOnOK(OnClickListener onOK)
	{
		this.onOK = onOK;
	}

	public OnClickListener getOnCancel()
	{
		return onCancel;
	}

	public void setOnCancel(OnClickListener onCancel)
	{
		this.onCancel = onCancel;
	}
}