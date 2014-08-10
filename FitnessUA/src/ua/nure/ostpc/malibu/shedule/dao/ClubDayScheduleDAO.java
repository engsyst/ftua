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

	public List<ClubDaySchedule> getClubDaySchedulesByDate(Date date);
}
