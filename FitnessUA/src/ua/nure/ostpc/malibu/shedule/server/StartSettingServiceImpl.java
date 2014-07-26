package ua.nure.ostpc.malibu.shedule.server;

import java.util.Collection;
import java.util.HashSet;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.client.StartSettingService;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StartSettingServiceImpl extends RemoteServiceServlet implements
    StartSettingService {
	
	private ClubDAO clubDAO;
	private EmployeeDAO employeeDAO;
	
	private static final Logger log = Logger.getLogger(StartSettingServiceImpl.class);
	
	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		clubDAO = (ClubDAO) servletContext.getAttribute(AppConstants.CLUB_DAO);
		employeeDAO = (EmployeeDAO) servletContext.getAttribute(AppConstants.EMPLOYEE_DAO);
		
		if (clubDAO == null) {
			log.error("ClubDAO attribute is not exists.");
			throw new IllegalStateException("ClubDAO attribute is not exists.");
		}
		else if (employeeDAO == null) {
			log.error("EmployeeDAO attribute is not exists.");
			throw new IllegalStateException("EmployeeDAO attribute is not exists.");
		}
	}
	
	@Override
	public Collection<Club> getClubs() throws IllegalArgumentException {
		try{
			return clubDAO.getMalibuClubs();
		}
		catch(Exception e){
		}
		return new HashSet<Club>();
	}
	
	@Override
	public Collection<Employee> getEmployees() throws IllegalArgumentException {
		try{
			return employeeDAO.getMalibuEmployees();
		}
		catch(Exception e){
			
		}
		return new HashSet<Employee>();
	}
	
	public void setClubs(Collection<Club> clubs) throws IllegalArgumentException{
		String mess="";
		for(Club elem : clubs){
			mess+=elem.getClubId()+" "+elem.getTitle()+"  "+elem.getIsIndependen()+"<br/>";
		}
		throw new IllegalArgumentException(mess);
	}

	@Override
	public void setEmployees(Collection<Employee> admins,
			Collection<Employee> responsiblePersons, Collection<Employee> other)
			throws IllegalArgumentException {
		String mess="<h2>Администраторы:</h2>";
		for(Employee elem : admins){
			mess+=elem.getLastName()+" "+elem.getFirstName()+" " + elem.getSecondName()+"<br/>";
		}
		mess+="<h2>Ответсвенные лица:</h2>";
		for(Employee elem : responsiblePersons){
			mess+=elem.getLastName()+" "+elem.getFirstName()+" " + elem.getSecondName()+"<br/>";
		}
		mess+="<h2>Другие:</h2>";
		for(Employee elem : other){
			mess+=elem.getLastName()+" "+elem.getFirstName()+" " + elem.getSecondName()+"<br/>";
		}
		throw new IllegalArgumentException(mess);
	}
}
