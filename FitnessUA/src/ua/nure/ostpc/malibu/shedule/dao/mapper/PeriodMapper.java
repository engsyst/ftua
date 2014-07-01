package ua.nure.ostpc.malibu.shedule.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ua.nure.ostpc.malibu.shedule.entity.Period;

public class PeriodMapper {

	public static Period unMapPeriod(ResultSet rs) throws SQLException {
		Period period = new Period(rs.getLong(MapperParameters.PERIOD__ID));
		period.setPeriod(rs.getDate(MapperParameters.PERIOD__START_DATE),
				rs.getDate(MapperParameters.PERIOD__END_DATE));
		period.setLastPriodId(rs
				.getLong(MapperParameters.PERIOD__LAST_PERIOD_ID));
		return period;
	}
}
