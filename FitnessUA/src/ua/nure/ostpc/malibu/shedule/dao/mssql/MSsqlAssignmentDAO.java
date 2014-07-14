package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentDAO;
import ua.nure.ostpc.malibu.shedule.dao.mapper.MapperParameters;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Period;

public class MSsqlAssignmentDAO implements AssignmentDAO {
	private static final String SQL__FIND_ASSIGNMENTS_BY_PERIOD_ID = "SELECT * FROM Assignment WHERE SchedulePeriodId=?;";

	public int insertAssignment(Connection con, Assignment assignment)
			throws SQLException {
		Statement st = null;
		int res = 0;
		try {
			st = con.createStatement();
			res = st.executeUpdate(String.format("insert into Assignment"
					+ "(AssignmentId,SchedulePeriodId,ClubId,Date,HalfOfDay) "
					+ "values(%1$d,%2$d,%3$d,%4$d,%5%d)",
					assignment.getAssignmentId(), assignment.getPeriodId(),
					assignment.getClubId(), assignment.getDate(),
					assignment.getHalfOfDay()));

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
		return res;
	}

	@Override
	public int insertAssignment(Assignment ast) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		int updateResult = 0;
		try {
			updateResult = insertAssignment(con, ast);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not update Assignment # " + this.getClass()
					+ " # " + e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return updateResult;
	}

	@Override
	public boolean deleteAssignment(Assignment ast) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		boolean deleteResult = false;
		try {
			deleteResult = deleteAssignment(con, ast);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not delete Assignment # " + this.getClass()
					+ " # " + e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return deleteResult;
	}

	public boolean deleteAssignment(Connection con, Assignment ast)
			throws SQLException {
		Statement st = null;
		int res = 0;
		try {
			st = con.createStatement();
			res = st.executeUpdate(String.format(
					"delete * from Assignment where AssignmentId = %1%d",
					ast.getAssignmentId()));
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
		if (res == 0)
			return false;
		else
			return true;
	}

	public Assignment findAssignment(Connection con, long id)
			throws SQLException {
		Assignment ast = null;
		Statement st = null;
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(String.format(
					"select*from Assignment " + "where AssignmentId=%d", id));
			ast = new Assignment();
			ast.setAssignmentId(resSet.getLong(MapperParameters.ASSIGNMENT__ID));
			ast.setDate(resSet.getDate(MapperParameters.ASSIGNMENT__DATE));
			ast.setHalfOfDay(resSet.getInt(MapperParameters.ASSIGNMENT__HALF_OF_DAY));
			ast.setPeriodId(resSet.getLong(MapperParameters.PERIOD__ID));
			ast.setClubId(resSet.getLong(MapperParameters.CLUB__ID));

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
		return ast;
	}

	@Override
	public Assignment findAssignment(long empId) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Assignment ast = null;
		try {
			ast = findAssignment(con, empId);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not find Assignment # " + this.getClass()
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
		return ast;
	}

	@Override
	public boolean updateAssignment(Assignment ast) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		boolean updateResult = false;
		try {
			updateResult = updateAssignment(con, ast);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not update Assignment # " + this.getClass()
					+ " # " + e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return updateResult;
	}

	public boolean updateAssignment(Connection con, Assignment ast)
			throws SQLException {
		Statement st = null;
		int res = 0;
		try {
			st = con.createStatement();
			res = st.executeUpdate(String
					.format("update Assignment set Date = %2$d, HalfOfDay =%3$d,"
							+ "SchedulePeriodId=%4$d, club_id=%5$d  where AssignmentId=%1$d",
							ast.getAssignmentId(), ast.getDate(),
							ast.getHalfOfDay(), ast.getPeriodId(),
							ast.getClubId()));
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
		if (res == 0)
			return false;
		else
			return true;
	}

	public Set<Assignment> selectAssignments(Connection con, Period period)
			throws SQLException {
		Statement st = null;
		Set<Assignment> resultAssignmentSet = new java.util.HashSet<Assignment>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(String.format(
					"select * from Assignment" + "where SchedulePeriodId=%d",
					period.getPeriodId()));
			while (resSet.next()) {
				Assignment ast = new Assignment();
				ast.setAssignmentId(resSet.getLong(MapperParameters.ASSIGNMENT__ID));
				ast.setDate(resSet.getDate(MapperParameters.ASSIGNMENT__DATE));
				ast.setHalfOfDay(resSet.getInt(MapperParameters.ASSIGNMENT__HALF_OF_DAY));
				ast.setPeriodId(resSet.getLong(MapperParameters.PERIOD__ID));
				ast.setClubId(resSet.getLong(MapperParameters.CLUB__ID));
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
	public Set<Assignment> selectAssignments(Period period) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Set<Assignment> resultAssignmentSet = new java.util.HashSet<Assignment>();
		try {
			resultAssignmentSet = selectAssignments(con, period);
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

	public List<Assignment> findAssignmenstByPeriodId(Connection con,
			long periodId) throws SQLException {
		List<Assignment> assignments = new ArrayList<Assignment>();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_ASSIGNMENTS_BY_PERIOD_ID);
			pstmt.setLong(1, periodId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Assignment assignment = unMapAssignment(rs);
				assignments.add(assignment);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return assignments;
	}

	private Assignment unMapAssignment(ResultSet rs) throws SQLException {
		Assignment assignment = new Assignment();
		assignment.setAssignmentId(rs.getLong(MapperParameters.ASSIGNMENT__ID));
		assignment.setPeriodId(rs
				.getLong(MapperParameters.ASSIGNMENT__PERIOD_ID));
		assignment.setClubId(rs.getLong(MapperParameters.ASSIGNMENT__CLUB_ID));
		assignment.setDate(rs.getDate(MapperParameters.ASSIGNMENT__DATE));
		assignment.setHalfOfDay(rs
				.getInt(MapperParameters.ASSIGNMENT__HALF_OF_DAY));
		return assignment;
	}

}
