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
		Date si = (Date) startInclusive.clone();
		Date ee = (Date) endExclusive.clone();
		si.setHours(0);
		si.setMinutes(0);
		si.setSeconds(0);
		ee.setHours(0);
		ee.setMinutes(0);
		ee.setSeconds(0);
		return (int)((ee.getTime() - si.getTime()) 
				/ (1000 * 60 * 60 * 24));
	}

	public static Date addDays(Date date, int days) {
		return addDays(date, days, 0, 0, 0, 0);
	}

	@SuppressWarnings("deprecation")
	public static Date addDays(Date date, int days, int hours, int minutes,
			int seconds, int milis) {
		Date d  = (Date) date.clone();
		d.setDate(d.getDate() + days); 
		d.setHours(d.getHours() + hours);
		d.setMinutes(d.getMinutes() + minutes);
		d.setSeconds(d.getSeconds() + seconds);
		d.setTime(d.getTime() + milis);
		return d;
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		for (int i = 20; i < 31; i++) {
			Date d = new Date(116,1,i);
			int dayOfWeek = dayOfWeak(d);
			System.out.print(i + " " + d + " " + dayOfWeek + " - ");
			Date d1  = addDays(d, i);
			System.out.println(d1);
			System.out.println(duration(d, d1));
		}
	}

	@SuppressWarnings("deprecation")
	public static int dayOfWeak(Date d) {
	    return (d.getDay() + 6) % 7;
	}
}
