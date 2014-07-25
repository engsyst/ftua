package ua.nure.ostpc.malibu.shedule.server;

import java.util.Collection;
import java.util.HashSet;

import ua.nure.ostpc.malibu.shedule.client.StartSettingService;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StartSettingServiceImpl extends RemoteServiceServlet implements
    StartSettingService {

	@Override
	public Collection<Club> getClubs() throws IllegalArgumentException {
		Collection<Club> resultSet = new HashSet<Club>();
		for(int i=0;i<20;i++){
			resultSet.add(new Club(i, "Club"+i, 0, false, 0));
		}
		return resultSet;
	}
	
	@Override
	public Collection<Employee> getEmployees() throws IllegalArgumentException {
		Collection<Employee> resultSet = new HashSet<Employee>();
		for(int i=0;i<20;i++){
			Employee emp = new Employee("firstName"+i,"sureName"+i,"lastName"+i,0,7);
			emp.setEmployeeId(i);
			resultSet.add(emp);
		}
		return resultSet;
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
