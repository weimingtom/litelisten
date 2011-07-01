package com.galapk.litelisten.util;

import java.text.NumberFormat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * The Util tool class
 * @author pangsy
 * @createDate 2011-7-1
 * @version v0.1
 */
public final class Util {

	private static long totalTimes = 0;
	private static int count = 0;
	
	/**
	 * Statistic the times for debug
	 * @param time
	 */
	public static void statisticTimes(long time) {
		Util.totalTimes += time;
		Util.count += 1;
	}
	
	/**
	 * For debug
	 * @return
	 */
	public static long printAverage() {
		if (count == 0)
			return 0;
		
		return totalTimes / count;
	}
	
	/**
	 * Calc how many days the first time is expired than second time.
	 * @param first
	 * @param second
	 * @return long the days
	 */
	public static long expireDays(long first, long second) {
		if (first < second)
			return 0;
		
		long delays = first - second;
		long days = delays / (1000 * 60 * 60 * 24);
		return days;
	}
	
	/**
	 * Launch the email intent
	 * @param context
	 * @param subject
	 * @param sendTo
	 * @param includeDebug
	 */
	public static void launchEmailToIntent(Context context, String subject,
			String sendTo, boolean includeDebug) {
		Uri uri = Uri.parse("mailto:" + sendTo);
		Intent msg = new Intent(Intent.ACTION_SENDTO, uri);
		msg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		StringBuilder body = new StringBuilder();
		if (includeDebug) {
			body.append(String.format(
					"\n\n----------\nSysinfo - %s\nModel: %s\n\n",
					Build.FINGERPRINT, Build.MODEL));

			// body.append(String.format("\n\nBrand: %s\n\n", Build.BRAND));
			body.append(String.format("%s config -\n", subject));

			// Add locale info
			body.append(String.format("locale: %s\n", context.getResources()
					.getConfiguration().locale.getDisplayName()));
		}

		msg.putExtra(Intent.EXTRA_EMAIL, sendTo);
		msg.putExtra(Intent.EXTRA_SUBJECT, subject);
		msg.putExtra(Intent.EXTRA_TEXT, body.toString());
		context.startActivity(msg);
	}
	
	/**
	 * Calc the percent
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static String getPercent(double p1, double p2) {
		String percent;
		double p3 = p1 / p2;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(1);
		percent = nf.format(p3);
		return percent;
	}
}