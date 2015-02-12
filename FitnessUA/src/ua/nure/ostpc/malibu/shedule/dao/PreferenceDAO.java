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

	Boolean removeHoliday(Long id);

	Boolean insertHolidays(Collection<Holiday> holidays);

	Collection<Holiday> getHolidays();
}
