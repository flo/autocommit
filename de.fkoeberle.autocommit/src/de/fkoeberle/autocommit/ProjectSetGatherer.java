package de.fkoeberle.autocommit;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

final class ProjectSetGatherer implements IResourceDeltaVisitor {
	private final Set<IProject> projects = new HashSet<IProject>();

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IProject project = delta.getResource().getProject();
		if (project == null) {
			// workspace root -> iterate further
			return true;
		} else {
			projects.add(project);
			return false;
		}
	}

	public Set<IProject> getProjects() {
		return projects;
	}
}