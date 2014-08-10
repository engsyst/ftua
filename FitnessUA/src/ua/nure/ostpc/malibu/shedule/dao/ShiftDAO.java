package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.Date;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Shift;

/**
 * Interface that all ShiftDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface ShiftDAO {

	public List<Shift> getShiftsByPeriodIdAndDate(long periodId, Date date);
}
