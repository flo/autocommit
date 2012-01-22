package de.fkoeberle.autocommit;

import java.io.IOException;

import org.eclipse.core.resources.IProject;

/**
 * Can be used to iterate over the repositories with enabled autocommit support.
 * 
 */
public interface IVersionControlSystem {

	IRepository getRepositoryFor(IProject project);

	void prepareProjectForAutocommits(IProject project) throws IOException;

}
