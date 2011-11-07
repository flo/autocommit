package de.fkoeberle.autocommit;

public interface IRepository {
	void commit();

	/**
	 * 
	 * @return true, if it could be verified that there are no uncomitted
	 *         changes. If it fails to determine if there are changes it returns
	 *         true.
	 */
	boolean noUncommittedChangesExist();

	/**
	 * When there has been no additional changes when commit gets called then
	 * session.add(data) will be called. Does nothing if there are no uncommited
	 * changes.
	 */
	void addSessionDataForUncommittedChanges(Object data);
}
