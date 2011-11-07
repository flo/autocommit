package de.fkoeberle.autocommit.message.refactor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.IUndoManager;
import org.eclipse.ltk.core.refactoring.IUndoManagerListener;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;
import de.fkoeberle.autocommit.IRepository;
public class RefactoringListener implements IUndoManagerListener {
	private final Set<IRepository> repositoriesWithoutChanges = new HashSet<IRepository>();
	private RefactoringDescriptor refactoringDescriptor;
	@Override
	public void undoStackChanged(IUndoManager manager) {
		// ignore
	}

	@Override
	public void redoStackChanged(IUndoManager manager) {
		// ignore
	}

	private AutoCommitPluginActivator getAutoCommitPlugion() {
		return AutoCommitPluginActivator.getDefault();
	}
	@Override
	public void aboutToPerformChange(IUndoManager manager, Change change) {
		ChangeDescriptor changeDescriptor = change.getDescriptor();
		if (changeDescriptor instanceof RefactoringChangeDescriptor) {
			RefactoringChangeDescriptor refactoringChangeDescriptor = (RefactoringChangeDescriptor) changeDescriptor;
			refactoringDescriptor = refactoringChangeDescriptor
					.getRefactoringDescriptor();

			for (IRepository repository : getAutoCommitPlugion()) {
				if (repository.noUncommittedChangesExist()) {
					repositoriesWithoutChanges.add(repository);
				}
			}
		}
	}

	@Override
	public void changePerformed(IUndoManager manager, Change change) {
		if (refactoringDescriptor != null) {
			for (IRepository repository : repositoriesWithoutChanges) {
				RefactoringDescriptorContainer descriptorContainer = new RefactoringDescriptorContainer(refactoringDescriptor);
				repository.addSessionDataForUncommittedChanges(descriptorContainer);
			}
		}
		refactoringDescriptor = null;
	}

}
