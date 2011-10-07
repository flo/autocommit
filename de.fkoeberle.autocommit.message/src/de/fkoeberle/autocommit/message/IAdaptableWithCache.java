package de.fkoeberle.autocommit.message;

public interface IAdaptableWithCache {

	public abstract <T> T getAdapter(Class<T> adapterClass);

}