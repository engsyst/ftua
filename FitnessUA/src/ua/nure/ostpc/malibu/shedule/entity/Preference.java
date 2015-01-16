package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Preference implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long preferenceId;
	private int shiftsNumber;
	
	/**
	 * Count of working hours in one day. So, work hours in one shift will
	 * workHoursInDay / shiftsNumber
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
	private int workHoursInWeek = 50;
	
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
	private int workContinusHours = 30;
	
	/**
	 * 
	 */
	private GenFlags mode;

	public GenFlags getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = GenFlags.CHECK_MAX_DAYS; 
		this.mode.setMode(mode);
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

	public Preference(long preferenceId, int shiftsNumber, int workHoursInDay) {
		this.preferenceId = preferenceId;
		this.shiftsNumber = shiftsNumber;
		this.workHoursInDay = workHoursInDay;
	}

	public Preference(long preferenceId, int shiftsNumber, int workHoursInDay,
			int workHoursInWeek, int workContinusHours, GenFlags mode) {
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
}
