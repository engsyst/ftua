/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.HashSet;
/**
 * Description of Shedule.
 * 
 * @author engsyst
 */
public class Shedule implements Serializable {
	/**
	 * Description of the property clubSedules.
	 */
	private HashSet<ClubShedule> clubSedules = new HashSet<ClubShedule>();

	/**
	 * Description of the property period.
	 */
	private Period period = null;

	/**
	 * Returns clubSedules.
	 * @return clubSedules 
	 */
	public HashSet<ClubShedule> getClubSedules() {
		return this.clubSedules;
	}

	/**
	 * Sets a value to attribute clubSedules. 
	 * @param newClubSedules 
	 */
	public void setClubSedules(HashSet<ClubShedule> newClubSedules) {
		this.clubSedules = newClubSedules;
	}

	/**
	 * Adds one attribute (if clubSedules had a multiple cardinality)
	 * @param clubSheduleToAdd in clubSedules
	 */
	public void addClubShedule(ClubShedule clubSheduleToAdd) {
		this.clubSedules.add(clubSheduleToAdd);
	}

	/**
	 * Removes an attribute (if clubSedules had a multiple cardinality)
	 * @param clubSheduleToRemove in clubSedules
	 */
	public void removeClubShedule(ClubShedule clubSheduleToRemove) {
		this.clubSedules.remove(clubSheduleToRemove);
	}

	/**
	 * Adds all the attribute (if clubSedules had a multiple cardinality)
	 * @param clubShedulesToAdd in clubSedules
	 */
	public void addAllClubShedule(
			HashSet<ClubShedule> clubShedulesToAdd) {
		this.clubSedules.addAll(clubShedulesToAdd);
	}

	/**
	 * Removes all the attribute (if clubSedules had a multiple cardinality)
	 * @param clubShedulesToRemove in clubSedules
	 */
	public void removeAllClubShedule(
			HashSet<ClubShedule> clubShedulesToRemove) {
		this.clubSedules.removeAll(clubShedulesToRemove);
	}

	/**
	 * Returns period.
	 * @return period 
	 */
	public Period getPeriod() {
		return this.period;
	}

	/**
	 * Sets a value to attribute period. 
	 * @param newPeriod 
	 */
	public void setPeriod(Period newPeriod) {
		this.period = newPeriod;
	}

}
