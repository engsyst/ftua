/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.HashSet;
// Start of user code (user defined imports)

// End of user code

/**
 * Set<Assignment> assignments<br />
 * assignments.length = period.getDuration() * 2<br />
 * Ќазначение происходит на половину дн€
 * 
 * @author engsyst
 */
public class ClubShedule implements Serializable {
	/**
	 * Description of the property period.
	 */
	private Period period = null;

	/**
	 * Description of the property assignments.
	 */
	private HashSet<Assignment> assignments = new HashSet<Assignment>();

	/**
	 * Description of the property club.
	 */
	private Club club = null;

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

	/**
	 * Returns assignments.
	 * @return assignments 
	 */
	public HashSet<Assignment> getAssignments() {
		return this.assignments;
	}

	/**
	 * Sets a value to attribute assignments. 
	 * @param newAssignments 
	 */
	public void setAssignments(HashSet<Assignment> newAssignments) {
		this.assignments = newAssignments;
	}

	/**
	 * Adds one attribute (if assignments had a multiple cardinality)
	 * @param assignmentToAdd in assignments
	 */
	public void addAssignment(Assignment assignmentToAdd) {
		this.assignments.add(assignmentToAdd);
	}

	/**
	 * Removes an attribute (if assignments had a multiple cardinality)
	 * @param assignmentToRemove in assignments
	 */
	public void removeAssignment(Assignment assignmentToRemove) {
		this.assignments.remove(assignmentToRemove);
	}

	/**
	 * Adds all the attribute (if assignments had a multiple cardinality)
	 * @param assignmentsToAdd in assignments
	 */
	public void addAllAssignment(
			HashSet<Assignment> assignmentsToAdd) {
		this.assignments.addAll(assignmentsToAdd);
	}

	/**
	 * Removes all the attribute (if assignments had a multiple cardinality)
	 * @param assignmentsToRemove in assignments
	 */
	public void removeAllAssignment(
			HashSet<Assignment> assignmentsToRemove) {
		this.assignments.removeAll(assignmentsToRemove);
	}

	/**
	 * Returns club.
	 * @return club 
	 */
	public Club getClub() {
		return this.club;
	}

	/**
	 * Sets a value to attribute club. 
	 * @param newClub 
	 */
	public void setClub(Club newClub) {
		this.club = newClub;
	}

}
