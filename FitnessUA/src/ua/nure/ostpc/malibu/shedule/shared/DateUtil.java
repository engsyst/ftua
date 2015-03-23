package ua.nure.ostpc.malibu.shedule.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

	@SuppressWarnings("deprecation")
	public static Date addDays(Date date, int days, int hours, int minutes,
			int seconds, int milis) {
		date.setDate(date.getDate() + days);
		date.setHours(date.getHours() + hours);
		date.setMinutes(date.getMinutes() + minutes);
		date.setSeconds(date.getSeconds() + seconds);
		date.setTime(date.getTime() + milis);
		return date;
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH mm", new Locale("ru", "ua"));
		Date d = sdf.parse("26 10 2015 12 35");
		System.out.println(d);
		d = addDays(d, 1);
		System.out.println(d);
		d = addDays(d, 0, 0, 0, 00, 1000);
		System.out.println(d);
	}

	@SuppressWarnings("deprecation")
	public static int dayOfWeak(Date d) {
	    return (d.getDay() + 6) % 7;
	}
}
