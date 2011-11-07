package de.fkoeberle.autocommit.event.build;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;

public class PostBuildListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		AutoCommitPluginActivator.getDefault().commitIfPossible();
	}

}
