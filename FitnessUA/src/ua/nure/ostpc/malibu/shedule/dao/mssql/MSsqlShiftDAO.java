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
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlShiftDAO implements ShiftDAO {
	private static final Logger log = Logger.getLogger(MSsqlShiftDAO.class);

	private static final String SQL__GET_SHIFTS_BY_SCHEDULE_CLUB_DAY_ID = "SELECT * FROM Shifts "
			+ "WHERE ScheduleClubDayId=?;";

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
				shift.setEmployees(employeeDAO.findEmployeesByShiftId(con,
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
