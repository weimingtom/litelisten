package com.littledai.litelisten;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LDFile
{
	private List<String> lstFile = new ArrayList<String>();

	// ����һ��Ŀ¼�µ�����ָ����չ�����ļ�����ѡ���Ƿ�������ļ���
	public void GetFiles(String Path, String Extension, boolean IsIterative, boolean SkipNomedia)
	{
		File[] files = new File(Path).listFiles();

		if (SkipNomedia)
		{// ���.nomedia�ļ�
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile() && files[i].getName().indexOf(".nomedia") != -1)
					return;
			}
		}

		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isFile())
			{
				if (files[i].getPath().substring(files[i].getPath().length() - Extension.length()).equals(Extension))
					lstFile.add(files[i].getPath());

				if (!IsIterative)
					break;
			}
			else if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1)
				GetFiles(files[i].getPath(), Extension, IsIterative, SkipNomedia);
		}
	}

	public List<String> getLstFile()
	{
		return lstFile;
	}

	public void setLstFile(List<String> lstFile)
	{
		this.lstFile = lstFile;
	}
}