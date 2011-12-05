package de.fkoeberle.autocommit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class Nature implements IProjectNature {
	public static final String ID = "de.fkoeberle.autocommit.nature";

	private IProject project;

	@Override
	public void configure() throws CoreException {
		// do nothing
	}

	@Override
	public void deconfigure() throws CoreException {
		// do nothing
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
