package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ShiftDAO;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlShiftDAO implements ShiftDAO {
	private static final Logger log = Logger.getLogger(MSsqlShiftDAO.class);

	private static final String SQL__GET_SHIFTS_BY_SCHEDULE_CLUB_DAY_ID = "SELECT * FROM Shifts "
			+ "WHERE ScheduleClubDayId=?;";
	private static final String SQL__CONTAINS_SHIFT_WITH_ID = "SELECT * FROM ClubPrefs WHERE ClubPrefsId=?;";
	private static final String SQL__INSERT_SHIFT = "INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(?, ?, ?);";
	private static final String SQL__INSERT_ASSIGNMENT = "INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(?, ?);";
	private static final String SQL__UPDATE_SHIFT = "UPDATE Shifts SET ScheduleClubDayId=?, ShiftId=?, QuantityOfEmp=? WHERE ShiftId=?;";
	private static final String SQL__DELETE_ASSIGNMENT = "DELETE FROM Assignment WHERE ShiftId=? AND EmployeeId=?;";

	private MSsqlEmployeeDAO employeeDAO = (MSsqlEmployeeDAO) DAOFactory
			.getDAOFactory(DAOFactory.MSSQL).getEmployeeDAO();

	@Override
	public List<Shift> getShiftsByScheduleClubDayId(long scheduleClubDayId) {
		Connection con = null;
		List<Shift> shifts = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			shifts = getShiftsByScheduleClubDayId(con, scheduleClubDayId);
		} catch (SQLException e) {
			log.error("Can not get shifts by schedule club day id.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return shifts;
	}

	public List<Shift> getShiftsByScheduleClubDayId(Connection con,
			long scheduleClubDayId) throws SQLException {
		PreparedStatement pstmt = null;
		List<Shift> shifts = null;
		try {
			pstmt = con
					.prepareStatement(SQL__GET_SHIFTS_BY_SCHEDULE_CLUB_DAY_ID);
			pstmt.setLong(1, scheduleClubDayId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				shifts = new ArrayList<Shift>();
			}
			while (rs.next()) {
				Shift shift = unMapShift(rs);
				shift.setEmployees(employeeDAO.getEmployeesByShiftId(con,
						shift.getShiftId()));
				shifts.add(shift);
			}
			return shifts;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	@Override
	public boolean containsShift(long shiftId) {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return containsShift(con, shiftId);
		} catch (SQLException e) {
			log.error("Can not check shift containing.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return false;
	}

	private boolean containsShift(Connection con, long shiftId)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__CONTAINS_SHIFT_WITH_ID);
			pstmt.setLong(1, shiftId);
			ResultSet rs = pstmt.executeQuery();
			return rs.isBeforeFirst();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	@Override
	public boolean insertShift(Shift shift) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertShift(con, shift);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not insert shift.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return result;
	}

	private boolean insertShift(Connection con, Shift shift)
			throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = true;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_SHIFT);
			mapShiftForInsert(shift, pstmt);
			pstmt.executeUpdate();
			if (shift.getEmployees() != null) {
				insertAssigments(con, shift.getShiftId(), shift.getEmployees());
			}
		} catch (SQLException e) {
			result = false;
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
		return result;
	}

	private boolean insertAssigments(Connection con, long shiftId,
			List<Employee> employees) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = true;
		try {
			if (employees != null) {
				pstmt = con.prepareStatement(SQL__INSERT_ASSIGNMENT);
				for (Employee employee : employees) {
					mapAssignment(shiftId, employee.getEmployeeId(), pstmt);
					pstmt.addBatch();
				}
				pstmt.executeBatch();
			}
		} catch (SQLException e) {
			result = false;
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
		return result;
	}

	@Override
	public boolean updateShift(Shift shift) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = updateShift(con, shift);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not update shift.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return result;
	}

	private boolean updateShift(Connection con, Shift shift)
			throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = true;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_SHIFT);
			mapShiftForUpdate(shift, pstmt);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			result = false;
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
		return result;
	}

	private boolean deleteAssigments(Connection con, long shiftId,
			List<Employee> employees) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = true;
		try {
			if (employees != null) {
				pstmt = con.prepareStatement(SQL__DELETE_ASSIGNMENT);
				for (Employee employee : employees) {
					mapAssignment(shiftId, employee.getEmployeeId(), pstmt);
					pstmt.addBatch();
				}
				pstmt.executeBatch();
			}
		} catch (SQLException e) {
			result = false;
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
		return result;
	}

	private void mapShiftForInsert(Shift shift, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, shift.getScheduleClubDayId());
		pstmt.setInt(2, shift.getShiftNumber());
		pstmt.setInt(3, shift.getQuantityOfEmployees());
	}

	private void mapShiftForUpdate(Shift shift, PreparedStatement pstmt)
			throws SQLException {
		mapShiftForInsert(shift, pstmt);
		pstmt.setLong(4, shift.getShiftId());
	}

	private void mapAssignment(long shiftId, long employeeId,
			PreparedStatement pstmt) throws SQLException {
		pstmt.setLong(1, shiftId);
		pstmt.setLong(2, employeeId);
	}

	private Shift unMapShift(ResultSet rs) throws SQLException {
		Shift shift = new Shift();
		shift.setShiftId(rs.getLong(MapperParameters.SHIFT__ID));
		shift.setScheduleClubDayId(rs
				.getLong(MapperParameters.SHIFT__SCHEDULE_CLUB_DAY_ID));
		shift.setShiftNumber(rs.getInt(MapperParameters.SHIFT__NUMBER));
		shift.setQuantityOfEmployees(rs
				.getInt(MapperParameters.SHIFT__QUANTITY_OF_EMPLOYEES));
		return shift;
	}
}
