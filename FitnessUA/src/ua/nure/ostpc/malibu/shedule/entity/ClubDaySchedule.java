/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * ClubDaySchedule.
 * 
 * @author engsyst
 */
public class ClubDaySchedule implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;
	private Date date;
	private Set<Assignment> assignments;
	private Club club;
	private int quantityOfEmp;

	public ClubDaySchedule() {
	}

	public ClubDaySchedule(Date date, Set<Assignment> assignments, Club club,
			int quantityOfEmp) {
		this.date = date;
		this.assignments = assignments;
		this.club = club;
		this.quantityOfEmp = quantityOfEmp;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public int getQuantityOfEmp() {
		return quantityOfEmp;
	}

	public void setQuantityOfEmp(int quantityOfEmp) {
		this.quantityOfEmp = quantityOfEmp;
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
}
