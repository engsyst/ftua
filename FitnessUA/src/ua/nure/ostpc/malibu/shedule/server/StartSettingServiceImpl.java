package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;
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
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("GET method starts");
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Path.PAGE__START_SETTING);
		dispatcher.forward(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
	}
	
	@Override
	public Collection<Club> getClubs() throws IllegalArgumentException {
		Collection<Club> malibuClubs = clubDAO.getAllMalibuClubs(); 
		if(malibuClubs == null)
			return new ArrayList<Club>();
		else
			return malibuClubs;
		
	}
	
	@Override
	public Collection<Employee> getEmployees() throws IllegalArgumentException {
		Collection<Employee> malibuEmployees = employeeDAO.getMalibuEmployees(); 
		if(malibuEmployees == null)
			return new ArrayList<Employee>();
		else
			return malibuEmployees;
	}

	@Override
	public Map<Long, Club> getDictionaryClub()
			throws IllegalArgumentException {
		Map<Long,Club> conformity = clubDAO.getConformity();
		if(conformity == null)
			return new HashMap<Long, Club>();
		else
			return conformity;
	}

	@Override
	public void setClubs(Collection<Club> clubsForInsert, Collection<Club> clubsForOnlyOurInsert,
			Collection<Club> clubsForUpdate, Collection<Club> clubsForDelete)
			throws IllegalArgumentException {
		for(Club elem : clubsForDelete){
			if(!clubDAO.deleteClub(elem.getClubId())){
				throw new IllegalArgumentException("Произошла ошибка при удалении клуба " + elem.getTitle());
			}
		}
		
		for(Club elem : clubsForUpdate){
			if(!clubDAO.updateClub(elem)){
				throw new IllegalArgumentException("Произошла ошибка при обновлении клуба " + elem.getTitle());
			}
		}
		
		if(!clubDAO.insertClubs(clubsForOnlyOurInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке клубов");
		}
		
		if(!clubDAO.insertClubsWithConformity(clubsForInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке клубов");
		}
	}

	@Override
	public Collection<Club> getOnlyOurClubs() throws IllegalArgumentException {
		Collection<Club> ourClub = clubDAO.getOnlyOurClub();
		if(ourClub == null)
			return new ArrayList<Club>();
		else
			return ourClub;
	}

	@Override
	public Collection<Employee> getOnlyOurEmployees()
			throws IllegalArgumentException {
		Collection<Employee> ourEmployee = employeeDAO.getOnlyOurEmployees();
		if(ourEmployee == null)
			return new ArrayList<Employee>();
		else
			return ourEmployee;
	}

	@Override
	public Map<Long, Employee> getDictionaryEmployee()
			throws IllegalArgumentException {
		Map<Long, Employee> conformity = employeeDAO.getConformity();
		if(conformity == null)
			return new HashMap<Long, Employee>();
		else
			return conformity;
	}

	@Override
	public Map<Long, Collection<Boolean>> getRoleEmployee()
			throws IllegalArgumentException {
		Map<Long, Collection<Boolean>> roles = employeeDAO.getRolesForEmployee();
		if(roles == null)
			return new HashMap<Long, Collection<Boolean>>();
		else
			return roles;
	}

	@Override
	public void setEmployees(Collection<Employee> employeesForInsert,
			Collection<Employee> employeesForOnlyOurInsert,
			Collection<Employee> employeesForUpdate,
			Collection<Employee> employeesForDelete,
			Map<Integer, Collection<Long>> roleForInsert,
			Map<Integer, Collection<Long>> roleForDelete,
			Map<Integer, Collection<Employee>> roleForInsertNew)
			throws IllegalArgumentException {
		for(Employee elem : employeesForDelete){
			if(!employeeDAO.deleteEmployee(elem.getEmployeeId())){
				throw new IllegalArgumentException("Произошла ошибка при удалении сотрудника " + elem.getNameForSchedule());
			}
		}
		
		/*for(Employee elem : employeesForUpdate){
			if(!employeeDAO.updateEmployee(elem)){
				throw new IllegalArgumentException("Произошла ошибка при обновлении сотрудника " + elem.getTitle());
			}
		}*/
		
		/*if(!employeeDAO.inserEmployees(employeesForOnlyOurInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке сотрудников");
		}*/
		
		if(!employeeDAO.insertEmployeesWithConformity(employeesForInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке сотрудников");
		}
		
		if(!employeeDAO.setRolesForEmployees(roleForInsert)){
			throw new IllegalArgumentException("Произошла ошибка при установки ролей сотрудникам");
		}
		
		if(!employeeDAO.deleteRolesForEmployees(roleForDelete)){
			throw new IllegalArgumentException("Произошла ошибка при удалении ролей сотрудников");
		}
		
	}
}
