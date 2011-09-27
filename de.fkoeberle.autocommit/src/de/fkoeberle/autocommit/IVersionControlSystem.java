package de.fkoeberle.autocommit;

public interface IVersionControlSystem {
	void commit(String message);
	boolean noUncommittedChangesExist();
}
