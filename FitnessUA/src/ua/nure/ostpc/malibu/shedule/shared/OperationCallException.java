package ua.nure.ostpc.malibu.shedule.shared;

@SuppressWarnings("serial")
public class OperationCallException extends Exception {

	public OperationCallException() {
		super();
	}

	public OperationCallException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		//super(message, cause, enableSuppression, writableStackTrace);
	}

	public OperationCallException(String message, Throwable cause) {
		super(message, cause);
	}

	public OperationCallException(String message) {
		super(message);
	}

	public OperationCallException(Throwable cause) {
		super(cause);
	}

}
