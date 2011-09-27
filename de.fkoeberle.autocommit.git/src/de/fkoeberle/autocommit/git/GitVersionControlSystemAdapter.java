package de.fkoeberle.autocommit.git;

import de.fkoeberle.autocommit.IVersionControlSystem;

public class GitVersionControlSystemAdapter implements IVersionControlSystem {

	public GitVersionControlSystemAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void commit(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean noUncommittedChangesExist() {
		// TODO Auto-generated method stub
		return true;
	}

}
