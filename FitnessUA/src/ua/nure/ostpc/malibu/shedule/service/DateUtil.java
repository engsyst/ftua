package ua.nure.ostpc.malibu.shedule.service;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * Substitute dates. Used {@link java.util.Date}
	 * 
	 * @param start - left date
	 * @param end - right date
	 * @return end - start in days
	 */
	public static long subDays(Date start, Date end) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);
		return (end.getTime() - start.getTime()) 
				/ (1000 * 60 *60 *24);
	}

	public static Date subDays(Date date, int days) {
		return subDays(date, days, 0, 0, 0, 0);
	}
	
	public static Date subDays(Date date, int days, int hours, int minutes, int seconds, int milis) {
		return new Date(date.getTime() 
				- (days * 24 * 60 * 60 * 1000)
				- (hours * 60 * 60 * 1000)
				- (minutes * 60 * 1000)
				- (seconds * 1000)
				- milis);
	}
	
	public static void main(String[] args) {
		Date d = new Date(System.currentTimeMillis());
		System.out.println(d + "              " + subDays(d, 5));
	}
}
