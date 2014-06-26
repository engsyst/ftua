package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentDAO;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;

public class MSsqlAssignmentDAO implements AssignmentDAO {

	@Override
	public int insertAssignment(Assignment ast) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean deleteAssignment(Assignment ast) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Assignment findAssignment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateAssignment(Assignment ast) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Assignment> selectAssignments(long periodId) {
		// TODO Auto-generated method stub
		return null;
	}

}
