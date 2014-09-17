package ua.nure.ostpc.malibu.shedule;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.service.MailService;

public class Demo {

	public static void main(String[] args) throws JxlWriteException,
			WriteException, SQLException, IOException, ParseException {
		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		ScheduleDAO scheduleDAO = df.getScheduleDAO();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date startDate = new Date(sdf.parse("15-09-2014").getTime());
		// Date endDate = new Date(sdf.parse("18-09-2014").getTime());
		// Period aaa = new Period(1, startDate, endDate, 0);
		// scheduleDAO.pushToExcel(aaa);

		EmployeeDAO employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO();
		MailService mailService = new MailService(employeeDAO);
		mailService.sendMail();

		Schedule schedule = scheduleDAO.getSchedule(2);
		// System.out.println(schedule.getDayScheduleMap());
		Set<java.sql.Date> keySet = schedule.getDayScheduleMap().keySet();
		ClubDaySchedule clubDaySchedule = schedule.getDayScheduleMap()
				.get(keySet.iterator().next()).get(0);
		System.out.println(clubDaySchedule.getShifts().get(0));
		Map<java.sql.Date, List<ClubDaySchedule>> notRight = schedule
				.getDayScheduleMap();
		// Set<java.sql.Date> lst = notRight.keySet();
		// Iterator<java.sql.Date> iterator = lst.iterator();
		List<ClubDaySchedule> clubDayScheduleList = notRight.get(startDate);
		Iterator<ClubDaySchedule> iter = clubDayScheduleList.iterator();
		while (iter.hasNext()) {
			ClubDaySchedule daySchedule = iter.next();
			// Club club = clubDaySchedule.getClub();
			if (clubDaySchedule.getClub().getClubId() == 1) {
				System.out.println(daySchedule);
				System.out.println(daySchedule.getShifts());
				for (Shift shift : daySchedule.getShifts()) {
					if (shift.getEmployees() != null) {
						for (Employee employee : shift.getEmployees()) {
							System.out.println(employee.getNameForSchedule());
						}
					} else {
						System.out.println(shift.getEmployees());
					}
				}
			}
		}

	}
}