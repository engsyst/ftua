package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import sun.security.util.BitArray;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Preference implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long preferenceId;
	private int shiftsNumber;
	
	/**
	 * Count of working hours in one day. So, work hours in one shift will
	 * workHoursInDay / shiftsNumber
	 * 
	 * <p>
	 * Количество рабочих часов в одном дне.<br />
	 * Таким образом, 'Количество часов в одной смене' = 'Количество часов в дне' / 'количество смен'
	 * </p>
	 */
	private int workHoursInDay;
	
	/**
	 * <p>Этот показатель обычно регулируется законодательством. В Украине это 40
	 * часов. Но компании, также сами регулируют это число. При составлении
	 * графика работ сотрудник, у которого следующее назначение на рабочую смену
	 * приведет к превышению этого числа, не будет назначаться до конца недели.
	 * Отсчет недели начинается с первого дня графика работ.</p>
	 * 
	 * <p>This is usually regulated by law. In Ukraine, it is 40 hours. But the
	 * company also regulate themselves that number. When scheduling work, 
	 * employees, which next the appointment to a work shift would exceed this
	 * number, will not be assigned until the end of the week. The countdown of
	 * the week begins with the first day of the work schedule.</p>
	 */
	private int workHoursInWeek;
	
	/**
	 * <p>По законодательству Украины на каждые 8 рабочих часов положено 16 часов
	 * отдыха. Компания может сама регулировать после скольки рабочих дней
	 * сотрудник получит выходной. Данный показатель задается в часах. При
	 * составлении графика работ, подсчитывается количество рабочих дней подряд.
	 * Если следующее назначение приведет к превышению этого числа ото
	 * сотруднику будет предоставлен выходной.</p>
	 * 
	 * <p>Under the laws of Ukraine for every 8 working hours is necessary 16
	 * hours of rest. The company can adjust itself, after how many days the
	 * employee will get holiday. This indicator is defined in hours. In drawing
	 * up the schedule, countdown the number of working days in a sequence. If the
	 * next assignment would exceed this number, then the employee will get
	 * a holiday.</p>
	 */
	private int workContinusHours;
	
	/**
	 * 
	 */
	private int mode;
	
	private Set<Holiday> holidays;
	

	public static final int MONDAY = 0;
	public static final int TUESDAY = 1;
	public static final int WEDNESDAY = 2;
	public static final int THURSDAY = 3;
	public static final int FRIDAY = 4;
	public static final int SATURDAY = 5;
	public static final int SUNDAY = 6;
	
	private boolean[] weekends;

	public boolean isHoliday(Date d) {
		// TODO resolve d is holiday
		if (holidays == null) 
			return false;
		for (Holiday h : holidays) {
			if (h.getDate().equals(d))
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isWeekend(Date d) {
		if (weekends == null)
			weekends = new boolean[7];
		return weekends[(d.getDay() + 6) % 7];
	}
	
	public boolean[] getWeekends() {
		return weekends;
	}
	
	public int getWeekendsAsInt() {
		if (weekends == null) 
			return 0;
		int w = 0;
		int scale = 1;
		for (int i = 0; i < weekends.length; i++) {
			if (weekends[i])
				w |= scale;
			scale *= 2;
		}
			
		return w;
	}
	
	public void setWeekends(int w) {
		if (weekends == null) 
			weekends = new boolean[7];
		
		int scale = 1;
		for (int i = 0; i < weekends.length; i++) {
			weekends[i] = (w & scale) == scale ;
			scale *= 2;
		}
	}
	
	public void setWeekends(boolean[] days) {
		if (days == null) 
			days = new boolean[7];
		if (days.length != 7)
			throw new IllegalArgumentException();
		this.weekends = days;
	}
	
	public void setWeekend(int dayOfWeek, boolean holiday) {
		if (dayOfWeek > 6)
			throw new IllegalArgumentException();
		if (weekends == null) 
			weekends = new boolean[7];
		this.weekends[dayOfWeek] = holiday;
	}

	public Set<Holiday> getHolidays() {
		if (holidays == null) 
			holidays = new TreeSet<Holiday>();
		return holidays;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int f) {
		this.mode = f;
	}

	public void setModeFlags(boolean clear, GenFlags... flags) {
		if (clear)
			mode = 0;
		for (GenFlags f : flags) {
			mode |= f.getMask(); 
		}
	}
	
	public boolean isFlagsSet(GenFlags... flags) {
		for (GenFlags f : flags) {
			if ((mode & f.getMask()) == f.getMask())
				return true;
		}
		return false;

	}
	
	public int getWorkHoursInWeek() {
		return workHoursInWeek;
	}

	public void setWorkHoursInWeek(int workHoursInWeek) {
		this.workHoursInWeek = workHoursInWeek;
	}

	public int getWorkContinusHours() {
		return workContinusHours;
	}

	public void setWorkContinusHours(int workContinusHours) {
		this.workContinusHours = workContinusHours;
	}

	public Preference() {
	}

	public Preference(long preferenceId, int shiftsNumber, int workHoursInDay,
			int workHoursInWeek, int workContinusHours, int mode) {
		super();
		this.preferenceId = preferenceId;
		this.shiftsNumber = shiftsNumber;
		this.workHoursInDay = workHoursInDay;
		this.workHoursInWeek = workHoursInWeek;
		this.workContinusHours = workContinusHours;
		this.mode = mode;
	}

	public long getPreferenceId() {
		return preferenceId;
	}

	public void setPreferenceId(long preferenceId) {
		this.preferenceId = preferenceId;
	}

	public int getShiftsNumber() {
		return shiftsNumber;
	}

	public void setShiftsNumber(int shiftsNumber) {
		this.shiftsNumber = shiftsNumber;
	}

	public int getWorkHoursInDay() {
		return workHoursInDay;
	}

	public void setWorkHoursInDay(int workHoursInDay) {
		this.workHoursInDay = workHoursInDay;
	}

	@Override
	public int hashCode() {
		return new Long(preferenceId).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Preference [preferenceId=");
		sb.append(preferenceId);
		sb.append(", shiftsNumber=");
		sb.append(shiftsNumber);
		sb.append(", workHoursInDay=");
		sb.append(workHoursInDay);
		sb.append("]");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Preference p = new Preference();
		p.setWeekends(1 << 5);
		System.out.println(Arrays.toString(p.getWeekends()));
		Date d = new Date(115, 1, 15);
		System.out.println(p.isWeekend(d));
	}
}
