package ua.nure.ostpc.malibu.shedule.service;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * Substitute dates. Used {@link java.util.Date}
	 * 
	 * @param start
	 *            - left date
	 * @param end
	 *            - right date
	 * @return end - start in days
	 */
	public static long subDays(Date start, Date end) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);
		return (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
	}

	public static Date subDays(Date date, int days) {
		return subDays(date, days, 0, 0, 0, 0);
	}

	public static Date subDays(Date date, int days, int hours, int minutes,
			int seconds, int milis) {
		long d = ((long) days * 24 * 60 * 60 * 1000) 
				+ ((long) hours * 60 * 60 * 1000)
				+ ((long) minutes * 60 * 1000)
				+ ((long) seconds * 1000)
				+ (long) milis;
		return new Date(date.getTime() - d);
	}

	public static void main(String[] args) {
		Date d = new Date(System.currentTimeMillis());
		System.out.println(d + "              " + subDays(d, 5));
	}

	public static int dayOfWeak(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;
	}
}
