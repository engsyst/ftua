package ua.nure.ostpc.malibu.shedule.dao;

import ua.nure.ostpc.malibu.shedule.entity.Preference;

/**
 * Interface that all PreferenceDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface PreferenceDAO {

	public Preference getLastPreference();
}
