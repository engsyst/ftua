package ua.nure.ostpc.malibu.shedule.entity;

import java.util.ArrayList;
import java.util.Date;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;

public class EmplyeeObjective {
	public static final double coefMin = .25;
	public static final double coefMax = .05;
	public static final double coefR = .7;
	public static final double alphaR = 1;
	
	private double globalMinBest; 	// min of all
	private double globalMinWorst; 	// max of all
	
	private double globalMaxBest;		// max of all
	private double globalMaxWorst;	// min of all
	private static EmplyeeObjective objective;
	
	
	
	/**
	 * @param globalMinBest
	 * @param globalMinWorst
	 * @param globalMaxBest
	 * @param globalMaxWorst
	 */
	private EmplyeeObjective(double globalMinBest, double globalMinWorst,
			double globalMaxBest, double globalMaxWorst) {
		super();
		setEmployeeObjective(globalMinBest, globalMaxBest, globalMinWorst, globalMaxWorst);
	}

	public static synchronized EmplyeeObjective getInstance(double globalMinBest, double globalMinWorst,
			double globalMaxBest, double globalMaxWorst) {
		if (objective == null) {
			objective = new EmplyeeObjective(globalMinBest, globalMinWorst, globalMaxBest, globalMaxWorst);
		}
		return objective;
	}

	public static synchronized EmplyeeObjective getInstance(double globalMinWorst, double globalMaxBest) {
		if (objective == null) {
			objective = new EmplyeeObjective(0, globalMinWorst, globalMaxBest, 0);
		}
		return objective;
	}
	
	public double getGlobalMinBest() {
		return globalMinBest;
	}


	public double getGlobalMinWorst() {
		return globalMinWorst;
	}


	public double getGlobalMaxBest() {
		return globalMaxBest;
	}


	public double getGlobalMaxWorst() {
		return globalMaxWorst;
	}

	public void setEmployeeObjective(double globalMaxBest, double globalMinWorst) {
		setEmployeeObjective(0, globalMaxBest, globalMinWorst, 0);
	}
	
	public void setEmployeeObjective(double globalMinBest, double globalMaxBest,
			double globalMinWorst, double globalMaxWorst) {
		this.globalMinBest = globalMinBest;
		this.globalMaxBest = globalMaxBest;
		this.globalMinWorst = globalMinWorst;
		this.globalMaxWorst = globalMaxWorst;

	}
	private double getMinS(Employee emp) {
		return (emp.getMinDays() - globalMinWorst) / (globalMinBest - globalMinWorst);
	}

	private double getMaxS(Employee emp) {
		return (emp.getMaxDays() - globalMaxWorst) / (globalMaxBest - globalMaxWorst);
	}
	
	public double getObjectiveValue(Date first, Date last, Employee emp) {
		final double minS = objective.getMinS(emp);
		final double maxS = objective.getMaxS(emp);
		
		double real = emp.getAssignments(first, last);
		final double realS = Math.pow((real - emp.getMaxDays()) / (minS - emp.getMaxDays()), alphaR);
		
		double of = minS * coefMin + maxS * coefMax + realS * coefR;
		emp.setObjectiveValue(of);
		return of;
	}
	
	public static void main(String[] args) {
		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		EmployeeDAO ed = df.getEmployeeDAO();
		ArrayList<Employee> emps = (ArrayList<Employee>) ed.getAllEmployee();
		
		// max of maxDays
		int max = 0;
		// max of minDays
		int min = 0;
		for (Employee employee : emps) {
			if (employee.getMaxDays() > max)
				max = employee.getMaxDays();
			if (employee.getMinDays() > min)
				min = employee.getMinDays();
		}
		
		EmplyeeObjective ob = EmplyeeObjective.getInstance(min, max);

		for (Employee employee : emps) {
			ob.getObjectiveValue(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), employee);
			System.out.println(employee);
		}
	}
}
