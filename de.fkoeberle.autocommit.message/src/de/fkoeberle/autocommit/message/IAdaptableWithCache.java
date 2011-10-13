package de.fkoeberle.autocommit.message;

public interface IAdaptableWithCache {

	/**
	 * 
	 * @param adapterClass
	 *            requested return type
	 * @return an object which implements adapterClass which must not be
	 *         modified or null if either no adapter could be found or if the
	 *         adapter returned null.
	 */
	public abstract <T> T getSharedAdapter(Class<T> adapterClass);

}