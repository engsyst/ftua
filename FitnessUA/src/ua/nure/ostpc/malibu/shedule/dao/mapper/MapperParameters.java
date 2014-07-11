package ua.nure.ostpc.malibu.shedule.dao.mapper;

public final class MapperParameters {

	// Period mapper parameters.
	public static final String PERIOD__ID = "SchedulePeriodId";
	public static final String PERIOD__LAST_PERIOD_ID = "LastPeriodId";
	public static final String PERIOD__START_DATE = "StartDate";
	public static final String PERIOD__END_DATE = "EndDate";

	// Assignment mapper parameters.
	public static final String ASSIGNMENT__ID = "AssignmentId";
	public static final String ASSIGNMENT__PERIOD_ID = "SchedulePeriodId";
	public static final String ASSIGNMENT__CLUB_ID = "ClubId";
	public static final String ASSIGNMENT__DATE = "Date";
	public static final String ASSIGNMENT__HALF_OF_DAY = "HalfOfDay";

	// Club mapper parameters.
	public static final String CLUB__ID = "ClubId";
	public static final String CLUB__TITLE = "Title";
	public static final String CLUB__CASH = "Cash";
	public static final String CLUB__IS_INDEPENDENT = "IsIndependent";

	// Employee mapper parameters.
	public static final String EMPLOYEE__ID = "EmployeeId";
	public static final String EMPLOYEE__CLUB_ID = "ClubId";
	public static final String EMPLOYEE__GROUP_ID = "EmployeeGroupId";
	public static final String EMPLOYEE__FIRSTNAME = "Firstname";
	public static final String EMPLOYEE__SECONDNAME = "Secondname";
	public static final String EMPLOYEE__LASTNAME = "Lastname";
	public static final String EMPLOYEE__BIRTHDAY = "Birthday";
	public static final String EMPLOYEE__ADDRESS = "Address";
	public static final String EMPLOYEE__PASSPORT_NUMBER = "PassportNumber";
	public static final String EMPLOYEE__ID_NUMBER = "IdNumber";
	public static final String EMPLOYEE__CELL_PHONE = "CellPhone";
	public static final String EMPLOYEE__WORK_PHONE = "WorkPhone";
	public static final String EMPLOYEE__HOME_PHONE = "HomePhone";
	public static final String EMPLOYEE__EMAIL = "Email";
	public static final String EMPLOYEE__EDUCATION = "Education";
	public static final String EMPLOYEE__NOTES = "Notes";
	public static final String EMPLOYEE__PASSPORT_ISSUED_BY = "PassportIssuedBy";
	public static final String EMPLOYEE__MIN_DAYS = "MinDays";
	public static final String EMPLOYEE__MAX_DAYS = "MaxDays";
}
