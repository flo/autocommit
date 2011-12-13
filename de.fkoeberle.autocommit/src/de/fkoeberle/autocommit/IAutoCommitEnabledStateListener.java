package de.fkoeberle.autocommit;

import org.eclipse.core.resources.IProject;

public interface IAutoCommitEnabledStateListener {
	void handleEnabledStateChanged(IProject project);
}
