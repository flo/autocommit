package de.fkoeberle.autocommit.message;

public class AdapterNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 286149499494752870L;

	public AdapterNotFoundException(Class<?> outputClass) {
		super("Unable to find an adapter factory for " + outputClass);
	}

}
