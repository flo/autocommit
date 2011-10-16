package de.fkoeberle.autocommit.message;

public interface ISession {
	<T> T getSharedAdapter(Object adaptable, Class<T> adapterClass)
			throws AdapterNotFoundException;
}