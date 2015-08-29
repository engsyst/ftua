package ua.nure.ostpc.malibu.shedule.parameter;

public interface AppConstants {
	// Integer parameters
	public static final int EMP_PREF_MIN_DAY_NUMBER = 2;
	public static final int EMP_PREF_MAX_DAY_NUMBER = 5;

	// CSS style parameters for entity edit panel
	public static final String CSS_PANEL_STYLE = "epf-panel";
	public static final String CSS_ERROR_LABEL_PANEL_STYLE = "epf-errLabelPanel";
	public static final String CSS_LABEL_PANEL_STYLE = "epf-labelPanel";
	public static final String CSS_TEXT_BOX_PANEL_STYLE = "epf-textBoxPanel";
	public static final String CSS_BUTTON_PANEL_STYLE = "epf-buttonsPanel";

	// History support
	public static final String HISTORY_VIEW = "view";
	public static final String HISTORY_DRAFT = "draft";
	public static final String HISTORY_EDIT = "edit";
	public static final String HISTORY_MANAGE = "manage";
	public static final String HISTORY_CREATE_NEW = "createnew";
	public static final String HISTORY_SETTINGS = "settings";
	public static final String HISTORY_CLUB_SETTINGS = "clubs";
	public static final String HISTORY_EMPLOYEE_SETTINGS = "employees";
	public static final String HISTORY_CATEGORY_SETTINGS = "categories";
	public static final String HISTORY_HOLIDAYS_SETTINGS = "holidays";

	public static final String USER = "user";
	public static final String LOGIN = "login";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";
	public static final String CELL_PHONE = "cellPhone";
	public static final String LAST_NAME = "lastName";
	public static final String FIRST_NAME = "firstName";
	public static final String SECOND_NAME = "secondName";
	public static final String ADDRESS = "address";
	public static final String PASSPORT_NUMBER = "passportNumber";
	public static final String ID_NUMBER = "idNumber";
	public static final String BIRTHDAY = "birthday";
	public static final String MIN_EMP_DAY_NUMBER = "maxEmpDayNumber";
	public static final String MAX_EMP_DAY_NUMBER = "minEmpDayNumber";
	public static final String OLD_PASSWORD = "oldPassword";
	public static final String NEW_PASSWORD = "newPassword";
	public static final String NEW_PASSWORD_REPEAT = "newPasswordRepeat";
	public static final String PERIOD_ID = "periodId";
	public static final String EMPLOYEE_ID = "employeeId";
	public static final String CATEGORY_MARKER = "c";
	public static final String EMPLOYEE_MARKER = "e";

	public static final String HTTP_METHOD_POST = "POST";

	// errors
	public static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	public static final String LOGIN_SERVER_ERROR = "Работник с таким логином уже существует!";
	public static final String EMAIL_SERVER_ERROR = "Работник с таким адресом электронной почты уже существует!";
	public static final String CELL_PHONE_SERVER_ERROR = "Работник с таким номером телефона уже существует!";
	public static final String PASSPORT_NUMBER_SERVER_ERROR = "Работник с таким номером паспорта уже существует!";
	public static final String ID_NUMBER_SERVER_ERROR = "Работник с таким идентификационный кодом уже существует!";
	public static final String PASSWORD_SERVER_ERROR = "Введен неверный старый пароль! Не удалось изменить пароль!";
	public static final String PASSWORD_INCORRECT_ROLE_ERROR = "Текущая сессия не содержит пользователя с ролью ответственного лица! Не удалось изменить пароль!";

	// DAO
	public static final String USER_DAO = "userDAO";
	public static final String CLUB_DAO = "clubDAO";
	public static final String EMPLOYEE_DAO = "employeeDAO";
	public static final String SCHEDULE_DAO = "scheduleDAO";
	public static final String PREFERENCE_DAO = "preferenceDAO";
	public static final String CATEGORY_DAO = "categoryDAO";
	public static final String HOLIDAY_DAO = "holidayDAO";
	public static final String SHIFT_DAO = "shiftDAO";
	public static final String CLUB_PREF_DAO = "clubPrefDAO";

	// preferences
	public static final String SHIFTS_NUMBER = "shiftsNumber";
	public static final String WORK_HOURS_IN_DAY = "workHoursInDay";
	public static final String WORK_HOURS_IN_WEEK = "workHoursInWeek";
	public static final String WORK_CONTINUS_HOURS = "workContinusHours";
	public static final String GENERATE_MODE = "generateMode";

	// club preferences identifiers id data source
	public static final String DATA_SOURCE_CLUB_PREF = "clubPrefDataSource";
	public static final String DATA_SOURCE_CLUB_PREF_ID = "clubPrefId";
	public static final String DATA_SOURCE_CLUB_PREF_NAME = "clubPrefName";

	// services
	public static final String MAIL_SERVICE = "mailService";
	public static final String NONCLOSED_SCHEDULE_CACHE_SERVICE = "nonclosedSheduleCacheService";
	public static final String SCHEDULE_EDIT_EVENT_SERVICE = "scheduleEditEventService";
	public static final String EXCEL_EMPLOYEE_SERVICE = "excelEmployeeService";

	// security parameters
	public static final String SECURITY_XML = "securityXML";

	// UI text
	public static final String CLUBS_MENU_ITEM = "Клубы";
	public static final String EMPLOYEE_MENU_ITEM = "Сотрудники";
	public static final String CATEGORY_MENU_ITEM = "Категории";
	public static final String HOLIDAY_MENU_ITEM = "Выходные";

	public static final String TEXT__HTML_HELP_ADDR = "от 3 до 100 символов";
	public static final String TEXT__HTML_HELP_NAME = "от 2 до 30 букв";
	public static final String TEXT__HTML_HELP_PHONE = "ровно 10 цифр";
	public static final String TEXT__HTML_HELP_PASSPORT = "2 русских буквы и 6 цифр";
	public static final String TEXT__HTML_HELP_INN = "ровно 10 цифр";
	public static final String TEXT__HTML_HELP_DATE = "ДД.ММ.ГГГГ";
	public static final String TEXT__HTML_HELP_EMAIL = "e-mail";
}
