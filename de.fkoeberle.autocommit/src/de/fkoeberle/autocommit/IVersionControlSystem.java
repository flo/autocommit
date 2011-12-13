package de.fkoeberle.autocommit;

import org.eclipse.core.resources.IProject;

/**
 * Can be used to iterate over the repositories with enabled autocommit support.
 * 
 */
public interface IVersionControlSystem extends Iterable<IRepository> {

	IRepository getRepositoryFor(IProject project);

}
