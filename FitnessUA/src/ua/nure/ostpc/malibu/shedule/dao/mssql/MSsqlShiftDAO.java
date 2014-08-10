package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
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

	private static final String SQL__GET_SHIFTS_BY_PERIOD_ID_AND_DATE = "SELECT Shifts.* FROM Shifts "
			+ "INNER JOIN ScheduleClubDay ON ScheduleClubDay.ScheduleClubDayId=Shifts.ScheduleClubDayId AND ScheduleClubDay.SchedulePeriodId=? AND ScheduleClubDay.Date=?;";

	private MSsqlEmployeeDAO employeeDAO = (MSsqlEmployeeDAO) DAOFactory
			.getDAOFactory(DAOFactory.MSSQL).getEmployeeDAO();

	@Override
	public List<Shift> getShiftsByPeriodIdAndDate(long periodId, Date date) {
		Connection con = null;
		List<Shift> shifts = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			shifts = getShiftsByPeriodIdAndDate(con, periodId, date);
		} catch (SQLException e) {
			log.error("Can not get shifts by period id and date.", e);
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

	public List<Shift> getShiftsByPeriodIdAndDate(Connection con,
			long periodId, Date date) throws SQLException {
		PreparedStatement pstmt = null;
		List<Shift> shifts = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_SHIFTS_BY_PERIOD_ID_AND_DATE);
			pstmt.setLong(1, periodId);
			pstmt.setDate(2, date);
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
		shift.setShiftNumber(rs.getInt(MapperParameters.SHIFT__NUMBER));
		shift.setQuantityOfEmployees(rs
				.getInt(MapperParameters.SHIFT__QUANTITY_OF_EMPLOYEES));
		return shift;
	}
}
