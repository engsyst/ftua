package ua.nure.ostpc.malibu.shedule;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.server.ScheduleManagerServiceImpl;

public class Demo {
	void testSchedule() {
		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		ScheduleDAO scheduleDAO = df.getScheduleDAO();
		Schedule sched = scheduleDAO.getSchedule(3);
		ScheduleManagerServiceImpl smsi = new ScheduleManagerServiceImpl();
		smsi.generate(sched);
		System.out.println(sched);
	}

	public static void main(String[] args) throws JxlWriteException,
			WriteException, SQLException, IOException, ParseException {

		Demo d = new Demo();
		d.testSchedule();
		
/*		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		ScheduleDAO scheduleDAO = df.getScheduleDAO();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date startDate = new Date(sdf.parse("15-09-2014").getTime());
		Date endDate = new Date(sdf.parse("18-09-2014").getTime());
		Period aaa = new Period(1, startDate, endDate, 0);
		// scheduleDAO.pushToExcel(aaa);

		EmployeeDAO employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO();
		MailService mailService = new MailService(employeeDAO);
		mailService.sendMail();
*/	}
}
