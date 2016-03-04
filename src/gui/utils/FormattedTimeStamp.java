package gui.utils;

import java.text.SimpleDateFormat;

public class FormattedTimeStamp {
	public static String getTime() {
		return String.valueOf(new SimpleDateFormat("HH:mm:ss").format(java.util.Calendar.getInstance().getTime()));
	}
}
