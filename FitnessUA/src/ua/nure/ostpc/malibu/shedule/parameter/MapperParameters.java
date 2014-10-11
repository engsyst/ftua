package ua.nure.ostpc.malibu.shedule.parameter;

public interface MapperParameters {

	// Period mapper parameters.
	public static final String PERIOD__ID = "SchedulePeriodId";
	public static final String PERIOD__LAST_PERIOD_ID = "LastPeriodId";
	public static final String PERIOD__START_DATE = "StartDate";
	public static final String PERIOD__END_DATE = "EndDate";
	public static final String PERIOD__STATUS = "Status";

	// Assignment mapper parameters.
	public static final String ASSIGNMENT__ID = "AssignmentId";
	public static final String ASSIGNMENT__SHIFT_ID = "ShiftId";
	public static final String ASSIGNMENT__EMPLOYEE_ID = "ClubId";

	// Club mapper parameters.
	public static final String CLUB__ID = "ClubId";
	public static final String CLUB__TITLE = "Title";
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
	public static final String EMPLOYEE__PASSPORT_NUMBER = "Passportint";
	public static final String EMPLOYEE__ID_NUMBER = "Idint";
	public static final String EMPLOYEE__CELL_PHONE = "CellPhone";
	public static final String EMPLOYEE__WORK_PHONE = "WorkPhone";
	public static final String EMPLOYEE__HOME_PHONE = "HomePhone";
	public static final String EMPLOYEE__EMAIL = "Email";
	public static final String EMPLOYEE__EDUCATION = "Education";
	public static final String EMPLOYEE__NOTES = "Notes";
	public static final String EMPLOYEE__PASSPORT_ISSUED_BY = "PassportIssuedBy";
	public static final String EMPLOYEE__MIN_DAYS = "MinDays";
	public static final String EMPLOYEE__MAX_DAYS = "MaxDays";

	// AssignmentExcel
	public static final String ASSIGNMENTEXCEL__PERIOD_ID = "SchedulePeriodId";
	public static final String ASSIGNMENTEXCEL__CLUB_TITLE = "Title";
	public static final String ASSIGNMENTEXCEL__DATE = "Date";
	public static final String ASSIGNMENTEXCEL__HALF_OF_DAY = "HalfOfDay";
	public static final String ASSIGNMENTEXCEL__NAME = "Name";
	public static final String ASSIGNMENTEXCEL__COLOUR = "Colour";
	public static final String ASSIGNMENTEXCEL__QUANTITYOFPEOPLE = "QuantityOfPeople";

	// User mapper parameters.
	public static final String USER__ID = "UserId";
	public static final String USER__EMPLOYEE_ID = "EmployeeId";
	public static final String USER__LOGIN = "Login";
	public static final String USER__PASSWORD = "PwdHache";

	// Role mapper parameters.
	public static final String ROLE__ID = "RoleId";
	public static final String ROLE__RIGHTS = "Rights";
	public static final String ROLE__TITLE = "Title";

	// Shift mapper parameters.
	public static final String SHIFT__ID = "ShiftId";
	public static final String SHIFT__SCHEDULE_CLUB_DAY_ID = "ScheduleClubDayId";
	public static final String SHIFT__NUMBER = "ShiftNumber";
	public static final String SHIFT__QUANTITY_OF_EMPLOYEES = "QuantityOfEmp";

	// ClubDaySchedule mapper parameters.
	public static final String CLUB_DAY_SCHEDULE__ID = "ScheduleClubDayId";
	public static final String CLUB_DAY_SCHEDULE__DATE = "Date";
	public static final String CLUB_DAY_SCHEDULE__SCHEDULE_PERIOD_ID = "SchedulePeriodId";
	public static final String CLUB_DAY_SCHEDULE__CLUB_ID = "ClubId";
	public static final String CLUB_DAY_SCHEDULE__SHIFTS_NUMBER = "ShiftsNumber";
	public static final String CLUB_DAY_WORK_HOURS_IN_DAY = "WorkHoursInDay";

	// ClubPref mapper parameters.
	public static final String CLUB_PREF__ID = "ClubPrefsId";
	public static final String CLUB_PREF__CLUB_ID = "ClubId";
	public static final String CLUB_PREF__SCHEDULE_PERIOD_ID = "SchedulePeriodId";
	public static final String CLUB_PREF__EMPLOYEE_ID = "EmployeeId";

	// Holiday mapper parameters.
	public static final String HOLIDAY__ID = "HolidayId";
	public static final String HOLIDAY__DATE = "Date";
	public static final String HOLIDAY__REPEATE = "Repeate";

	// Preference mapper parameters.
	public static final String PREFERENCE__ID = "PrefId";
	public static final String PREFERENCE__SHIFTS_NUMBER = "ShiftsNumber";
	public static final String PREFERENCE__WORK_HOURS_IN_DAY = "WorkHoursInDay";

	// Category mapper parameters.
	public static final String CATEGORY__ID = "CategoryId";
	public static final String CATEGORY__TITLE = "Title";

	// CategoryEmp mapper parameters.
	public static final String CATEGORY_EMP__ID = "CategoryEmpId";
	public static final String CATEGORY_EMP__CATEGORY_ID = "CategoryId";
	public static final String CATEGORY_EMP__EMPLOYEE_ID = "EmployeeId";
}
