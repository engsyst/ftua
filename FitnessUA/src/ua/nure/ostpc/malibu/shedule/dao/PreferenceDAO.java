package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Preference;

/**
 * Interface that all PreferenceDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface PreferenceDAO {

	public boolean updatePreference(Preference pf);

	public Preference getLastPreference();

	public Holiday getHolidayById(long holidayId);

	public boolean deleteHoliday(long holidayId) throws DAOException;

	public long insertHoliday(Holiday holiday) throws DAOException;

	Boolean insertHolidays(Collection<Holiday> holidays);

	Collection<Holiday> getHolidays();
}
