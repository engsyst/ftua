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
		try{
			return employeeDAO.getMalibuEmployees();
		}
		catch(Exception e){
			
		}
		return new HashSet<Employee>();
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
}
