package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentDAO;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Period;

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
	public Assignment findAssignment(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateAssignment(Assignment ast) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Assignment> selectAssignments(Period period) {
		// TODO Auto-generated method stub
		return null;
	}

}
