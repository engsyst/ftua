package ua.nure.ostpc.malibu.shedule.dao;

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
}
