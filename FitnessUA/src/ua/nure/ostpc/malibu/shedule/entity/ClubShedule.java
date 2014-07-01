/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Set<Assignment> assignments<br />
 * assignments.length = period.getDuration() * 2<br />
 * ���������� ���������� �� �������� ���
 * 
 * @author engsyst
 */
public class ClubShedule implements Serializable {
	private static final long serialVersionUID = 1L;
	private Period period = null;
	private Set<Assignment> assignments = new TreeSet<Assignment>();
	private Club club = null;

	/**
	 * Returns period.
	 * 
	 * @return period
	 */
	public Period getPeriod() {
		return this.period;
	}

	/**
	 * Sets a value to attribute period.
	 * 
	 * @param newPeriod
	 */
	public void setPeriod(Period newPeriod) {
		this.period = newPeriod;
	}

	/**
	 * Returns assignments.
	 * 
	 * @return assignments
	 */
	public Set<Assignment> getAssignments() {
		return this.assignments;
	}

	/**
	 * Sets a value to attribute assignments.
	 * 
	 * @param newAssignments
	 */
	public void setAssignments(Set<Assignment> newAssignments) {
		this.assignments = newAssignments;
	}

	/**
	 * Adds one attribute (if assignments had a multiple cardinality)
	 * 
	 * @param assignmentToAdd
	 *            in assignments
	 */
	public void addAssignment(Assignment assignmentToAdd) {
		this.assignments.add(assignmentToAdd);
	}

	/**
	 * Removes an attribute (if assignments had a multiple cardinality)
	 * 
	 * @param assignmentToRemove
	 *            in assignments
	 */
	public void removeAssignment(Assignment assignmentToRemove) {
		this.assignments.remove(assignmentToRemove);
	}

	/**
	 * Adds all the attribute (if assignments had a multiple cardinality)
	 * 
	 * @param assignmentsToAdd
	 *            in assignments
	 */
	public void addAllAssignment(Set<Assignment> assignmentsToAdd) {
		this.assignments.addAll(assignmentsToAdd);
	}

	/**
	 * Removes all the attribute (if assignments had a multiple cardinality)
	 * 
	 * @param assignmentsToRemove
	 *            in assignments
	 */
	public void removeAllAssignment(Set<Assignment> assignmentsToRemove) {
		this.assignments.removeAll(assignmentsToRemove);
	}

	/**
	 * Returns club.
	 * 
	 * @return club
	 */
	public Club getClub() {
		return this.club;
	}

	/**
	 * Sets a value to attribute club.
	 * 
	 * @param newClub
	 */
	public void setClub(Club newClub) {
		this.club = newClub;
	}

}
