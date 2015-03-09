package ua.nure.ostpc.malibu.shedule.dao;

public class DAOException extends Exception {
	private static final long serialVersionUID = 1944430364351977352L;

	Throwable caught;
	String message;

	public DAOException() {
		super();
	}

	public DAOException(String mes) {
		super();
		message = mes;
	}

	public DAOException(String mes, Throwable caught) {
		super();
		message = mes;
		this.caught = caught;
	}

	public DAOException(Throwable caught) {
		super();
		this.caught = caught;
	}

	public Throwable getCaught() {
		return caught;
	}

	public String getMessage() {
		return message;
	}
}
