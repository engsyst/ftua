package ua.nure.ostpc.malibu.shedule.dao;

import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.User;

/**
 * Interface that all UserDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface UserDAO {

	public boolean containsUser(String login);

	public User getUser(String login);

	public User getUser(long userId);

	public List<Role> getUserRoles(long userId);
	
	public List<Long> getEmployeeIdsWitoutUser();
	
	public List<User> getAllUsers();
	
	public boolean insertUser(User user);
}
