/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostsp.malibu.shedule.dao;

import java.util.Collection;

import javax.sql.RowSet;

// Start of user code (user defined imports)
import ua.nure.ostsp.malibu.shedule.entity.Assignment;

// End of user code

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

	public RowSet selectAssignmentsRS();

	public Collection<Assignment> selectAssignmentsTO();
}
