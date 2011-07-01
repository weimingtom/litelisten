package com.galapk.litelisten.log;

/**
 * Wrapper API for outputing the log record
 * @author pangsy
 * @createDate 2011-7-1
 * @version v0.1
 */
public class Log {

	protected static final String TAG = "MobilePaper";
	
	/**
	 * the log disable lock
	 * when is true, the log is disabled, or the log is enabled. 
	 */
	private static boolean disable = false;
	
	/**
	 * the constructor
	 */
	public Log() {
	}
	
	/**
	 * build the complete message 
	 * @param message
	 * @return
	 */
	private static String buildMessage(String message) {
		StackTraceElement ste = new Throwable().fillInStackTrace().getStackTrace()[2];
		
		StringBuffer sb = new StringBuffer();
		sb.append(ste.getClassName());
		sb.append(".");
		sb.append(ste.getMethodName());
		sb.append("(): ");
		sb.append(message);
		return sb.toString();
	}
	
	/**
	 * check if the log is disabled
	 * @return
	 */
	private static boolean isEnabled() {
		return !disable;
	}
	
	/**
	 * send a VERBOSE log message.
	 * @param msg
	 */
	public static void v(String msg) {
		if(isEnabled()) {
			android.util.Log.v(TAG, buildMessage(msg));
		}
	}
	
	/**
	 * send a VERBOSE log message and log the exception
	 * @param msg
	 * @param throwable
	 */
	public static void v(String msg, Throwable throwable) {
		if(isEnabled()) {
			android.util.Log.v(TAG, buildMessage(msg), throwable);
		}
	}
	
	/**
	 * send a DEBUG log message
	 * @param msg
	 */
	public static void d(String msg) {
		if(isEnabled()) {
			android.util.Log.d(TAG, buildMessage(msg));
		}
	}
	
	/**
	 * send a DEBUG log message and log the exception
	 * @param msg
	 * @param throwable
	 */
	public static void d(String msg, Throwable throwable) {
		if(isEnabled()) {
			android.util.Log.d(TAG, buildMessage(msg), throwable);
		}
	}
	
	/**
	 * send a INFO log message
	 * @param msg
	 */
	public static void i(String msg) {
		if(isEnabled()) {
			android.util.Log.i(TAG, buildMessage(msg));
		}
	}
	
	/**
	 * send a INFO log message and log the exception
	 * @param msg
	 * @param throwable
	 */
	public static void i(String msg, Throwable throwable) {
		if(isEnabled()) {
			android.util.Log.i(TAG, buildMessage(msg), throwable);
		}
	}
	
	/**
	 * send a ERROR log message
	 * @param msg
	 */
	public static void e(String msg) {
		if(isEnabled()) {
			android.util.Log.e(TAG, buildMessage(msg));
		}
	}
	
	/**
	 * send a ERROR log message and log the exception
	 * @param msg
	 * @param throwable
	 */
	public static void e(String msg, Throwable throwable) {
		if(isEnabled()) {
			android.util.Log.e(TAG, buildMessage(msg), throwable);
		}
	}
	
	/**
	 * send a WARN log message
	 * @param msg
	 */
	public static void w(String msg) {
		if(isEnabled()) {
			android.util.Log.w(TAG, buildMessage(msg));
		}
	}
	
	/**
	 * send a WARN log message and log the exception
	 * @param msg
	 * @param throwable
	 */
	public static void w(String msg, Throwable throwable) {
		if(isEnabled()) {
			android.util.Log.w(TAG, buildMessage(msg), throwable);
		}
	}
}