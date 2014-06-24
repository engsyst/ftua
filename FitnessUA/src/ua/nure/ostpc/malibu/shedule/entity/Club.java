/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.HashSet;
/**
 * Description of Club.
 * 
 * @author engsyst
 */
public class Club implements Serializable {
	/**
	 * Description of the property clubId.
	 */
	private long clubId = 0L;

	/**
	 * Description of the property title.
	 */
	private String title = "";

	/**
	 * Description of the property isIndependen.
	 */
	private boolean isIndependen = false;

	/**
	 * Description of the property clubPrefs.
	 */
	private HashSet<Employee> clubPrefs = new HashSet<Employee>();

	/**
	 * Returns clubId.
	 * @return clubId 
	 */
	public long getClubId() {
		return this.clubId;
	}

	/**
	 * Sets a value to attribute clubId. 
	 * @param newClubId 
	 */
	public void setClubId(long newClubId) {
		this.clubId = newClubId;
	}

	/**
	 * Returns title.
	 * @return title 
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets a value to attribute title. 
	 * @param newTitle 
	 */
	public void setTitle(String newTitle) {
		this.title = newTitle;
	}

	/**
	 * Returns isIndependen.
	 * @return isIndependen 
	 */
	public Boolean getIsIndependen() {
		return this.isIndependen;
	}

	/**
	 * Sets a value to attribute isIndependen. 
	 * @param newIsIndependen 
	 */
	public void setIsIndependen(Boolean newIsIndependen) {
		this.isIndependen = newIsIndependen;
	}

	/**
	 * Returns clubPrefs.
	 * @return clubPrefs 
	 */
	public HashSet<Employee> getClubPrefs() {
		return this.clubPrefs;
	}

	/**
	 * Sets a value to attribute clubPrefs. 
	 * @param newClubPrefs 
	 */
	public void setClubPrefs(HashSet<Employee> newClubPrefs) {
		this.clubPrefs = newClubPrefs;
	}

	/**
	 * Adds one attribute (if clubPrefs had a multiple cardinality)
	 * @param employeeToAdd in clubPrefs
	 */
	public void addClubPrefs(Employee employeeToAdd) {
		this.clubPrefs.add(employeeToAdd);
	}

	/**
	 * Removes an attribute (if clubPrefs had a multiple cardinality)
	 * @param employeeToRemove in clubPrefs
	 */
	public void removeClubPrefs(Employee employeeToRemove) {
		this.clubPrefs.remove(employeeToRemove);
	}

	/**
	 * Adds all the attribute (if clubPrefs had a multiple cardinality)
	 * @param employeesToAdd in clubPrefs
	 */
	public void addAllClubPrefs(HashSet<Employee> employeesToAdd) {
		this.clubPrefs.addAll(employeesToAdd);
	}

	/**
	 * Removes all the attribute (if clubPrefs had a multiple cardinality)
	 * @param employeesToRemove in clubPrefs
	 */
	public void removeAllClubPrefs(HashSet<Employee> employeesToRemove) {
		this.clubPrefs.removeAll(employeesToRemove);
	}

}
