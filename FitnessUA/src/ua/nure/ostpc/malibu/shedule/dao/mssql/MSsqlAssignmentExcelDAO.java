package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentExcelDAO;
import ua.nure.ostpc.malibu.shedule.entity.AssignmentExcel;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlAssignmentExcelDAO implements AssignmentExcelDAO {

	public Set<AssignmentExcel> selectAssignmentsExcel(Connection con,
			Period period) throws SQLException {
		Statement st = null;
		Set<AssignmentExcel> resultAssignmentSet = new java.util.HashSet<AssignmentExcel>();
		try {
			st = con.createStatement();
			java.sql.ResultSet rs = st
					.executeQuery(String
							.format("SELECT DISTINCT "
									+ "		[Date],[HalfOfDay],c.Title,emp.Firstname+' '+emp.Lastname as Name,"
									+ "emp.Colour ,SchedulePeriodId, c.QuantityOfPeople"
									+ "		FROM [FitnessUA].[dbo].[Assignment] ass ,"
									+ "		EmployeeToAssignment eta , Club c , Employee emp  "
									+ "WHERE ass.AssignmentId = eta.AssignmentId and c.ClubId=ass.ClubId  "
									+ "and emp.EmployeeId=eta.EmployeeId and SchedulePeriodId =%d;",
									period.getPeriodId()));
			while (rs.next()) {
				AssignmentExcel ast = new AssignmentExcel();
				ast.setClubTitle(rs
						.getString(MapperParameters.ASSIGNMENTEXCEL__CLUB_TITLE));
				ast.setColour(rs
						.getInt(MapperParameters.ASSIGNMENTEXCEL__COLOUR));
				ast.setDate(rs.getDate(MapperParameters.ASSIGNMENTEXCEL__DATE));
				ast.setHalfOfDay(rs
						.getInt(MapperParameters.ASSIGNMENTEXCEL__HALF_OF_DAY));
				ast.setName(rs
						.getString(MapperParameters.ASSIGNMENTEXCEL__NAME));
				ast.setSchedulePeriodId(rs
						.getLong(MapperParameters.ASSIGNMENTEXCEL__PERIOD_ID));
				ast.setQuantityOfPeople(rs
						.getInt(MapperParameters.ASSIGNMENTEXCEL__QUANTITYOFPEOPLE));
				resultAssignmentSet.add(ast);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return resultAssignmentSet;
	}

	@Override
	public Set<AssignmentExcel> selectAssignmentsExcel(Period period)
			throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Set<AssignmentExcel> resultAssignmentSet = new java.util.HashSet<AssignmentExcel>();
		try {
			resultAssignmentSet = selectAssignmentsExcel(con, period);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not find Assignments # " + this.getClass()
					+ " # " + e.getMessage());
			return null;
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return resultAssignmentSet;
	}

}
