package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.SQLException;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.AssignmentExcel;
import ua.nure.ostpc.malibu.shedule.entity.Period;

public interface AssignmentExcelDAO {

	public Set<AssignmentExcel> selectAssignmentsExcel(Period period)
			throws SQLException;

}