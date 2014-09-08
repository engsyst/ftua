package ua.nure.ostpc.malibu.shedule;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.service.MailService;

public class Demo {

	public static void main(String[] args) throws JxlWriteException,
			WriteException, SQLException, IOException, ParseException {
		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		ScheduleDAO scheduleDAO = df.getScheduleDAO();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date startDate = new Date(sdf.parse("05-07-2014").getTime());
		Date endDate = new Date(sdf.parse("28-08-2014").getTime());
		Period aaa = new Period(1, startDate, endDate, 0);
		// scheduleDAO.pushToExcel(aaa);

		EmployeeDAO employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO();
		MailService mailService = new MailService(employeeDAO);
		mailService.sendMail();
	}
}