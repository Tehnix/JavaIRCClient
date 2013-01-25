package client;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utilities {
	
	private final static boolean DEBUG = false;
	
	public static void debug(String text) {
		if (DEBUG) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			String time = format.format(cal.getTime()) + ": ";
			System.out.println(time + text);
		}
	}
	
	public static void debug(boolean err, String text) {
		if (DEBUG) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			String time = format.format(cal.getTime()) + ": ";
			System.err.println(time + text);
		}
	}
	
	public static void debug(StackTraceElement[] stackTraceElements) {
		if (DEBUG) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			String time = format.format(cal.getTime()) + ": ";
			String exception = "";
		    for (StackTraceElement s : stackTraceElements) {
		        exception = exception + s.toString() + "\n\t\t";
		    }
			System.err.println(time + exception);
		}
	}
	
	public static String prettyTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return "[" + format.format(cal.getTime()) + "]";
	}
	
}
