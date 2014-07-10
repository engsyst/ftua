package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentDAO;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Period;

public class MSsqlAssignmentDAO implements AssignmentDAO {

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
			ast.setAssignmentId(resSet.getLong("AssignmentId"));
			ast.setDate(resSet.getDate("Date"));
			ast.setHalfOfDay(resSet.getInt("HalfOfDay"));
			ast.setPeriodId(resSet.getLong("ShedulePeriodId"));
			ast.setClubId(resSet.getLong("ClubId"));

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
				ast.setAssignmentId(resSet.getLong("AssignmentId"));
				ast.setDate(resSet.getDate("Date"));
				ast.setHalfOfDay(resSet.getInt("HalfOfDay"));
				ast.setPeriodId(resSet.getLong("ShedulePeriodId"));
				ast.setClubId(resSet.getLong("ClubId"));
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

}
