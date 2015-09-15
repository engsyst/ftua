package ua.nure.ostpc.malibu.shedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.ClubSettingViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmployeeSettingsData;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.server.ScheduleManagerServiceImpl;
import ua.nure.ostpc.malibu.shedule.util.ExcelScheduleUtil;
import ua.nure.ostpc.malibu.shedule.util.MailUtil;

public class Demo {
	void testSchedule() throws DAOException {
		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		ScheduleDAO scheduleDAO = df.getScheduleDAO();
		Schedule sched = scheduleDAO.getSchedule(4);

		ScheduleManagerServiceImpl smsi = new ScheduleManagerServiceImpl();
		smsi.setEmployeeDAO(DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO());

		smsi.generate(sched);
		System.out.println(sched.toString());
		scheduleDAO.updateSchedule(sched);
	}
	
	void testGetAllClubs() throws Exception {
		ClubDAO d = DAOFactory.getDAOFactory(DAOFactory.MSSQL).getClubDAO();
		List<ClubSettingViewData> cvd = d.getAllClubs();
		System.out.println(cvd);
	}

	private byte[] getExcel(long id, boolean full, Employee emp) throws Exception {
		if (full) 
			return ExcelScheduleUtil.scheduleAllToExcelConvert(
					DAOFactory.getDAOFactory(DAOFactory.MSSQL).getScheduleDAO().getSchedule(id));
		else 
			return ExcelScheduleUtil.scheduleUserToExcelConvert(
					DAOFactory.getDAOFactory(DAOFactory.MSSQL).getScheduleDAO().getSchedule(id), emp);
	}

	public void sendMail(long id, boolean full, boolean toAll, Long empId)
			throws Exception{
		String[] emails = null; 
		Employee emp = null; 
		Period p = DAOFactory.getDAOFactory(DAOFactory.MSSQL).getScheduleDAO().getPeriod(id);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String period = df.format(p.getStartDate()) + "-" + df.format(p.getEndDate());
		String fName = "График_работ_" + period;
		String theme = fName;
		if (toAll) {
			emails = DAOFactory.getDAOFactory(DAOFactory.MSSQL).getEmployeeDAO().getEmailListForSubscribers().toArray(new String[0]);
		} else {
			emp = DAOFactory.getDAOFactory(DAOFactory.MSSQL).getEmployeeDAO().getScheduleEmployeeById(empId);
			emails = new String[1];
			emails[0] = emp.getEmail();
			fName += "_" + emp.getShortName();
		}
		fName += ".xls";
		byte[] xls = getExcel(id, full, emp);
		try {
			MailUtil.configure("mail.properties");
			MailUtil.sendMail(theme, "", xls, fName, emails);
		} catch (Exception e) {
//			log.error("Can not send e-mail", e.getCause());
			throw new IllegalArgumentException("Невозможно отослать почту ", e);
		}
	}
	
	public void testGetEmployeeSettings() throws DAOException {
		EmployeeDAO ed = DAOFactory.getDAOFactory(DAOFactory.MSSQL).getEmployeeDAO();
		ArrayList<EmployeeSettingsData> esds = (ArrayList<EmployeeSettingsData>) ed.getEmployeeSettingsData();
		for (EmployeeSettingsData esd : esds) {
			System.out.println(esd);
		}
	}

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String DB_URL = "jdbc:sqlserver://localhost:1433; database=FitnessUA; user=sa; password=master;";
	
	public static void main(String[] args) throws Exception {
//		Demo d = new Demo();
		HashSet<A> hs = new HashSet<A>();
		for (int i = 0; i < 14; i++) {
			hs.add(new A(i, Long.toString(i)));
		}
		
		A[] ar = hs.toArray(new A[0]);
		for (int i = 0; i < ar.length; i++) {
			if (ar[i].getId() > 5) {
				hs.remove(ar[i]);
				A ta = new A(ar[i].getId(), ar[i].getS() + ar[i].getId());
				hs.add(ta);
			}
			
		}
		for (A a : hs) {
			System.out.println(a);
		}
//		d.testGetEmployeeSettings();
		final String str = "Мама мыла раму";
		final String str1 = "РњР°РјР° РјС‹Р»Р° СЂР°РјСѓ";
		byte[] b = str.getBytes("UTF-8");
		System.out.println(new String(b, "UTF-16BE"));
		
		//		d.testGetEmployeeSettings();
		
//		d.testGetEmployeeSettings();
//		d.sendMail(4L, true, true, 2L);

		/*
		 * DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		 * ScheduleDAO scheduleDAO = df.getScheduleDAO(); SimpleDateFormat sdf =
		 * new SimpleDateFormat("dd-MM-yyyy"); Date startDate = new
		 * Date(sdf.parse("15-09-2014").getTime()); Date endDate = new
		 * Date(sdf.parse("18-09-2014").getTime()); Period aaa = new Period(1,
		 * startDate, endDate, 0); // scheduleDAO.pushToExcel(aaa);
		 * 
		 * EmployeeDAO employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
		 * .getEmployeeDAO(); MailService mailService = new
		 * MailService(employeeDAO); mailService.sendMail();
		 */}
}


class A {
	private long id;
	private String s;
	public A(long id, String s) {
		super();
		this.id = id;
		this.s = s;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("A [id=");
		builder.append(id);
		builder.append(", s=");
		builder.append(s);
		builder.append("]");
		return builder.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		A other = (A) obj;
		if (id != other.id)
			return false;
		return true;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	
}