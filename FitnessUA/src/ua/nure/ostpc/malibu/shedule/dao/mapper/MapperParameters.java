package ua.nure.ostpc.malibu.shedule.dao.mapper;

public final class MapperParameters {

	// Period mapper parameters.
	public static final String PERIOD__ID = "SchedulePeriodId";
	public static final String PERIOD__LAST_PERIOD_ID = "LastPeriodId";
	public static final String PERIOD__START_DATE = "StartDate";
	public static final String PERIOD__END_DATE = "EndDate";	

	// Shedule mapper parameters.
	public static final String SCHEDULE__ID = "day_shedule_id";
	public static final String SCHEDULE__DATE = "dates";
	public static final String SCHEDULE__HALF_OF_DAY = "halfOfDay";
	public static final String SCHEDULE__USER_ID = "users_id";
	public static final String SCHEDULE__CLUB_ID = "club_id";
	public static final String SCHEDULE__PERIOD_ID = "schedule_period_id";
}
