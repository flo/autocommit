package de.fkoeberle.autocommit.event.build;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;
import de.fkoeberle.autocommit.ProjectSetGatherer;

public class PostBuildListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		ProjectSetGatherer projectSetGatherer = new ProjectSetGatherer();
		try {
			event.getDelta().accept(projectSetGatherer);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		Set<IProject> projects = projectSetGatherer.getProjects();
		AutoCommitPluginActivator.getDefault().commitIfPossible(projects);
	}

}
