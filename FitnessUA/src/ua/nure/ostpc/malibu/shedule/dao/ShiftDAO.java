package ua.nure.ostpc.malibu.shedule.dao;

import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;

/**
 * Interface that all ShiftDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface ShiftDAO {

	public List<Shift> getShiftsByScheduleClubDayId(long scheduleClubDayId);

	public Shift getShift(long shiftId);

	public boolean containsShift(long shiftId);

	public boolean insertShift(Shift shift);

	public boolean updateShift(Shift shift) throws DAOException;

	public boolean removeShift(Shift shift);

	public Schedule updateShift(Shift shift, Long periodId) throws DAOException;
}
