package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.Date;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;

/**
 * Interface that all ClubDayScheduleDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface ClubDayScheduleDAO {

	public List<ClubDaySchedule> getClubDaySchedulesByDateAndPeriodId(
			Date date, long periodId);

	public ClubDaySchedule getClubDaySchedule(long clubDayScheduleId);

	public boolean containsClubDaySchedule(long clubDayScheduleId);

	public boolean insertClubDaySchedule(ClubDaySchedule clubDaySchedule);

	public boolean updateClubDaySchedule(ClubDaySchedule clubDaySchedule);

	public boolean removeClubDaySchedule(ClubDaySchedule clubDaySchedule);
}
