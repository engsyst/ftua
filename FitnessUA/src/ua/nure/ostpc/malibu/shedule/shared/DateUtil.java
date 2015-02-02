package ua.nure.ostpc.malibu.shedule.shared;

import java.util.Date;

public class DateUtil {
	/**
	 * Duration in days. Used {@link java.util.Date}
	 * 
	 * @param start
	 *            - left date
	 * @param end
	 *            - right date
	 * @return end - start in days
	 */
	@SuppressWarnings("deprecation")
	public static int duration(Date startInclusive, Date endExclusive) {
		startInclusive.setHours(0);
		startInclusive.setMinutes(0);
		startInclusive.setSeconds(0);
		endExclusive.setHours(0);
		endExclusive.setMinutes(0);
		endExclusive.setSeconds(0);
		return (int)(endExclusive.getTime() - startInclusive.getTime()) 
				/ (1000 * 60 * 60 * 24);
	}

	public static Date addDays(Date date, int days) {
		return addDays(date, days, 0, 0, 0, 0);
	}

	public static Date addDays(Date date, int days, int hours, int minutes,
			int seconds, int milis) {
		long d = ((long) days * 24 * 60 * 60 * 1000) 
				+ ((long) hours * 60 * 60 * 1000)
				+ ((long) minutes * 60 * 1000)
				+ ((long) seconds * 1000)
				+ (long) milis;
		return new Date(date.getTime() + d);
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		for (int i = 20; i < 31; i++) {
			Date d = new Date(116,1,i);
			int dayOfWeek = dayOfWeak(d);
			System.out.println(i + " " + d + " " + dayOfWeek);
		}
		System.out.println(duration(new Date(115, 0, 10), new Date(115, 0, 20)));
	}

	@SuppressWarnings("deprecation")
	public static int dayOfWeak(Date d) {
	    return (d.getDay() + 6) % 7;
	}
}
