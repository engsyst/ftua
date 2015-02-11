package ua.nure.ostpc.malibu.shedule;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.ClubSettingViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.server.ScheduleManagerServiceImpl;
import ua.nure.ostpc.malibu.shedule.service.MailService;
import ua.nure.ostpc.malibu.shedule.service.ExcelService;

public class Demo {
	void testSchedule() {
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

	private byte[] getExcel(long id, boolean full, Employee emp) {
		if (full) 
			return ExcelService.scheduleAllToExcelConvert(
					DAOFactory.getDAOFactory(DAOFactory.MSSQL).getScheduleDAO().getSchedule(id));
		else 
			return ExcelService.scheduleUserToExcelConvert(
					DAOFactory.getDAOFactory(DAOFactory.MSSQL).getScheduleDAO().getSchedule(id), emp);
	}

	public void sendMail(long id, boolean full, boolean toAll, Long empId)
			throws IllegalArgumentException{
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
			MailService.configure("mail.properties");
			MailService.sendMail(theme, "", xls, fName, emails);
		} catch (Exception e) {
//			log.error("Can not send e-mail", e.getCause());
			throw new IllegalArgumentException("Невозможно отослать почту ", e);
		}
	}
	public static void main(String[] args) throws Exception {
		

		Demo d = new Demo();
		d.sendMail(4L, true, true, 2L);

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
