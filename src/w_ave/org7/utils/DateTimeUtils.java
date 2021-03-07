package w_ave.org7.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {
	public static String dateTimeToShortForm(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " "
				+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE);
	}
}
