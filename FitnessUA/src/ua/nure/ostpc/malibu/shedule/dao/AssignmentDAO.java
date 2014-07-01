/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Set;
import java.sql.SQLException;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Period;


/**
 * Description of AssignmentDAO.
 * 
 * @author engsyst
 */
public interface AssignmentDAO {
	public int insertAssignment(Assignment ast);
	public int insertAssignment(Assignment ast) throws SQLException;
	
	public boolean deleteAssignment(Assignment ast);
	public boolean deleteAssignment(Assignment ast) throws SQLException;

	public Assignment findAssignment(long id);
	public Assignment findAssignment(long id) throws SQLException;
	public boolean updateAssignment(Assignment ast);
	public boolean updateAssignment(Assignment ast) throws SQLException;
	
	/**
	 * Сортировка по: дате, половине дня, фамилии
	 * @param period
	 * @return
	 */
	public Set<Assignment> selectAssignments(Period period);
	public Set<Assignment> selectAssignments(Period period) throws SQLException;
}
