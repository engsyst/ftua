package ua.nure.ostpc.malibu.shedule;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.security.Hashing;
import ua.nure.ostpc.malibu.shedule.shared.FieldVerifier;

public class Demo {

	public static void main(String[] args) throws SQLException, ParseException {
		/*
		 * DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		 * ScheduleDAO scheduleDAO = df.getScheduleDAO(); SimpleDateFormat sdf =
		 * new SimpleDateFormat("dd-MM-yyyy"); Date date = new
		 * Date(sdf.parse("17-06-2014").getTime()); Period period =
		 * scheduleDAO.readPeriod(date); System.out.println(period);
		 */

		/*
		 * DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		 * ScheduleDAO scheduleDAO = df.getScheduleDAO(); SimpleDateFormat sdf =
		 * new SimpleDateFormat("dd-MM-yyyy"); Date date = new
		 * Date(sdf.parse("13-06-2014").getTime()); Period period =
		 * scheduleDAO.readPeriod(date); System.out.println(period); Schedule
		 * schedule = scheduleDAO.readSchedule(period);
		 * System.out.println(schedule);
		 */

		/*
		 * DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		 * ScheduleDAO scheduleDAO = df.getScheduleDAO(); SimpleDateFormat sdf =
		 * new SimpleDateFormat("dd-MM-yyyy"); Date startDate = new
		 * Date(sdf.parse("01-06-2014").getTime()); Date endDate = new
		 * Date(sdf.parse("30-06-2014").getTime()); Set<Schedule> schedules =
		 * scheduleDAO.readSchedules(startDate, endDate); Iterator<Schedule> it
		 * = schedules.iterator(); while (it.hasNext()) { Schedule schedule =
		 * it.next(); System.out.println(schedule); }
		 */

		/*
		 * DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL); UserDAO
		 * userDAO = df.getUserDAO(); User user = userDAO.getUser(5);
		 * System.out.println(user);
		 * System.out.println(user.getRole().getRight());
		 * 
		 * String login = "loginNine"; String password = "Password_9"; password
		 * = Hashing.salt(password, login); System.out.println(password);
		 */

		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		ScheduleDAO scheduleDAO = df.getScheduleDAO();
		Period aaa = new Period(1);
		scheduleDAO.pushToExcel(aaa);
	}
}
