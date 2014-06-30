/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.SQLException;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Period;


/**
 * Description of AssignmentDAO.
 * 
 * @author engsyst
 */
public interface AssignmentDAO {
	public int insertAssignment(Assignment ast) throws SQLException;

	public boolean deleteAssignment(Assignment ast) throws SQLException;

	public Assignment findAssignment(long id) throws SQLException;

	public boolean updateAssignment(Assignment ast) throws SQLException;
	
	/**
	 * Сортировка по: дате, половине дня, фамилии
	 * @param period
	 * @return
	 * @throws SQLException 
	 */
	public Set<Assignment> selectAssignments(Period period) throws SQLException;
}
