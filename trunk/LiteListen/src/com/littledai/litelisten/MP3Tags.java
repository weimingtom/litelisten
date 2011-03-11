package com.littledai.litelisten;

import java.io.File;

import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;

public class MP3Tags
{
	private srcMain main = null;

	public MP3Tags(srcMain main)
	{
		this.main = main;
	}

	public ID3Tag ReadID3(String path)
	{
		try
		{
			File f = new File(path);
			MediaFile mf = new MP3File(f);
			ID3Tag[] ID3 = mf.getTags();

			String index = main.getSp().getString("lstPropertyReadPriority", "0");
			if (index.equals("0"))
			{
				// 优先检测ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);

				// 然后是ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// 最后检测ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);
			}
			else if (index.equals("1"))
			{
				// 优先检测ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// 然后是ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);

				// 最后检测ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);
			}
			else if (index.equals("0"))
			{
				// 优先检测ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);

				// 然后是ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// 最后检测ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);
			}
			else if (index.equals("0"))
			{
				// 优先检测ID3v1.1
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_1Tag)
					return (ID3V1_1Tag) ReCodecTag(ID3[1]);

				// 然后是ID3v1.0
				if (ID3.length == 1 && ID3[0] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V1_0Tag)
					return (ID3V1_0Tag) ReCodecTag(ID3[1]);

				// 最后检测ID3v2
				if (ID3.length == 1 && ID3[0] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[0]);
				else if (ID3.length == 2 && ID3[1] instanceof ID3V2_3_0Tag)
					return (ID3V2Tag) ReCodecTag(ID3[1]);
			}

			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/* 重新编码ID3标签为GBK */
	public ID3Tag ReCodecTag(ID3Tag ID3)
	{
		if (ID3 instanceof ID3V2_3_0Tag)
		{
			ID3V2_3_0Tag ID3v2 = (ID3V2_3_0Tag) ID3;

			try
			{
				ID3v2.setAlbum(new String(ID3v2.getAlbum().getBytes("ISO-8859-1"), "GBK"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				ID3v2.setArtist(new String(ID3v2.getArtist().getBytes("ISO-8859-1"), "GBK"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				ID3v2.setComment(new String(ID3v2.getComment().getBytes("ISO-8859-1"), "GBK"));
			}

			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				ID3v2.setGenre(new String(ID3v2.getGenre().getBytes("ISO-8859-1"), "GBK"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				ID3v2.setTitle(new String(ID3v2.getTitle().getBytes("ISO-8859-1"), "GBK"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return ID3v2;
		}
		else if (ID3 instanceof ID3V1_1Tag)
			return (ID3V1_1Tag) ID3;
		else if (ID3 instanceof ID3V1_0Tag)
			return (ID3V1_0Tag) ID3;

		return null;
	}

	public srcMain getMain()
	{
		return main;
	}

	public void setMain(srcMain main)
	{
		this.main = main;
	}
}