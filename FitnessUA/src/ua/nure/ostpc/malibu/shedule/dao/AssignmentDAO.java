/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Period;


/**
 * Description of AssignmentDAO.
 * 
 * @author engsyst
 */
public interface AssignmentDAO {
	public int insertAssignment(Assignment ast);

	public boolean deleteAssignment(Assignment ast);

	public Assignment findAssignment(long id);

	public boolean updateAssignment(Assignment ast);
	
	/**
	 * Сортировка по: дате, половине дня, фамилии
	 * @param period
	 * @return
	 */
	public Set<Assignment> selectAssignments(Period period);
}
