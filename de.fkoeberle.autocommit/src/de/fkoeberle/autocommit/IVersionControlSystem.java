package de.fkoeberle.autocommit;

public interface IVersionControlSystem {
	void commit(String message);
	/**
	 * 
	 * @return true, if it could be verified that there are no uncomitted changes. If it fails to determine if there are changes it returns true.
	 */
	boolean noUncommittedChangesExist();
}
