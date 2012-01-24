package de.fkoeberle.autocommit;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

public class AutoCommitAvailablePropertyTester extends PropertyTester {

	public AutoCommitAvailablePropertyTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		boolean expectedBooleanValue = (expectedValue == Boolean.TRUE);
		IProject project = (IProject) receiver;
		IRepository repository = AutoCommitPluginActivator.getDefault()
				.getRepositoryFor(project);
		boolean available = (repository != null);
		return available == expectedBooleanValue;
	}

}
