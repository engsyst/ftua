/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.entity.Assignment;


/**
 * Description of AssignmentDAO.
 * 
 * @author engsyst
 */
public interface AssignmentDAO {
	public int insertAssignment(Assignment ast);

	public boolean deleteAssignment(Assignment ast);

	public Assignment findAssignment();

	public boolean updateAssignment(Assignment ast);

	public Collection<Assignment> selectAssignments(long periodId);
}
