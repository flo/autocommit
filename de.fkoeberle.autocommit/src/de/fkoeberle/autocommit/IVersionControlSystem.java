package de.fkoeberle.autocommit;

public interface IVersionControlSystem {
	/**
	 * 
	 * @param message the message the commit should get or null if the message should be chosen automatically.
	 */
	void commit(String message);
	/**
	 * 
	 * @return true, if it could be verified that there are no uncomitted changes. If it fails to determine if there are changes it returns true.
	 */
	boolean noUncommittedChangesExist();
}
