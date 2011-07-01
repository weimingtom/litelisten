package com.galapk.litelisten.util;

/**
 * the String util class 
 * @author pangsy
 * @createDate 2011-7-1
 * @version v0.1
 */
public class StringUtil {

	
	/**
	 * format the file size in KB or MB
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size) {
		StringBuffer sb = new StringBuffer();
		long k = size/1024;
		if(k > 1024) {
			long m = k/1024;
			sb.append(m);
			sb.append("M");
		} else {
			sb.append(k);
			sb.append("K");
		}
		return sb.toString();
	}
}