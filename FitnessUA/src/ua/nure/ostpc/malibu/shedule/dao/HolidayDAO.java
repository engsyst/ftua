package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.entity.Holiday;

public interface HolidayDAO {
	
	public Boolean insertHolidays(Collection<Holiday> holidays);
	public Collection<Holiday> getHolidays();
	public Boolean removeHoliday(Long id); 
}
