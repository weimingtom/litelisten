package com.galapk.litelisten.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;

/**
 * Wrapper API for outputing the log record to the local disk file
 * @author pangsy
 * @createDate 2011-7-1
 * @version v0.1
 */
public class FileLog {
    
	private static String TAG = "galapk_player";
	
    /**
	 * the log disable lock
	 * when is true, the log is disabled, or the log is enabled. 
	 */
	private static boolean disable = false;

	/**
	 * build the print message 
	 * @param message
	 * @return
	 */
	private static String buildMessage(String message) {
		StackTraceElement ste = new Throwable().fillInStackTrace().getStackTrace()[2];
		
		StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date(System.currentTimeMillis()));
        sb.append(time);
        sb.append(" ");
        sb.append(TAG);
        sb.append(": ");
		sb.append(ste.getClassName());
		sb.append(".");
		sb.append(ste.getMethodName());
		sb.append("(): ");
		sb.append(message);
		return sb.toString();
	}
	
	/**
	 * write a VERBOSE log message to the file
	 * @param fileName
	 * @param message
	 */
	public static void v(String fileName, String message) {
		if (TextUtils.isEmpty(fileName))
			return;
		
		if (disable)
			return;
		
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		
		try {
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(buildMessage(message));
			bw.newLine();
			bw.flush();
			fw.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}
}