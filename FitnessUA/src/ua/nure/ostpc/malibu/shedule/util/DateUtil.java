package ua.nure.ostpc.malibu.shedule.util;

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
	public static long duration(Date start, Date end) {
		resetTime(start);
		resetTime(end);
		return (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
	}

	public static Date addDays(Date date, int days) {
		date = addDays(date, days, 0, 0, 0, 0);
		return date;
	}
	
	@SuppressWarnings("deprecation")
	public static Date resetTime(Date d) {
		d.setHours(0);
		d.setMinutes(0);
		d.setSeconds(0);
		return d;
	}

	public static Date addDays(Date date, int days, int hours, int minutes,
			int seconds, int milis) {
		long d = ((long) days * 24 * 60 * 60 * 1000) 
				+ ((long) hours * 60 * 60 * 1000)
				+ ((long) minutes * 60 * 1000)
				+ ((long) seconds * 1000)
				+ (long) milis;
		date = new Date(date.getTime() + d);
		return date;
	}

	public static void main(String[] args) {
		Date d = new Date(System.currentTimeMillis());
		System.out.println(d + "              " + addDays(d, 5));
	}

	@SuppressWarnings("deprecation")
	public static int dayOfWeak(Date d) {
		return (d.getDay() + 6) % 7;
	}
}
