package de.fkoeberle.autocommit;

import static de.fkoeberle.autocommit.ProjectsWithNatureSearchUtil.searchAutoCommitableProjectsWithEnabledState;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class EnableAutomaticCommitsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		Set<IProject> selectedProjectsWithoutNature = searchAutoCommitableProjectsWithEnabledState(
				selection, false);
		try {
			for (IProject project : selectedProjectsWithoutNature) {
				AutoCommitPluginActivator.getDefault().enableAutoCommitsFor(
						project);
			}
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

}
