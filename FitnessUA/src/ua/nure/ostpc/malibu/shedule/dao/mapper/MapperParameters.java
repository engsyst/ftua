package ua.nure.ostpc.malibu.shedule.dao.mapper;

public final class MapperParameters {

	// Period mapper parameters.
	public static final String PERIOD__ID = "shedule_period_id";
	public static final String PERIOD__START_DATE = "startDate";
	public static final String PERIOD__END_DATE = "endDate";
	public static final String PERIOD__LAST_PERIOD_ID = "last_period_id";

	// Shedule mapper parameters.
	public static final String SCHEDULE__ID = "day_shedule_id";
	public static final String SCHEDULE__DATE = "dates";
	public static final String SCHEDULE__HALF_OF_DAY = "halfOfDay";
	public static final String SCHEDULE__USER_ID = "users_id";
	public static final String SCHEDULE__CLUB_ID = "club_id";
	public static final String SCHEDULE__PERIOD_ID = "schedule_period_id";
}
