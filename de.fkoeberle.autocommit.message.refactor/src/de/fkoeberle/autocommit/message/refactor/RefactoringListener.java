/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.refactor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

			for (IRepository repository : getAutoCommitPlugion()
					.getAllEnabledRepositories()) {
				try {
					if (repository.noUncommittedChangesExist()) {
						repositoriesWithoutChanges.add(repository);
					}
				} catch (IOException e) {
					logError(e);
				}
			}
		}
	}

	private void logError(IOException e) {
		RefactorEventPluginActivator
				.getDefault()
				.getLog()
				.log(new Status(IStatus.ERROR,
						RefactorEventPluginActivator.PLUGIN_ID,
						"An exception occured", e));
	}

	@Override
	public void changePerformed(IUndoManager manager, Change change) {
		if (refactoringDescriptor != null) {
			for (IRepository repository : repositoriesWithoutChanges) {
				RefactoringDescriptorContainer descriptorContainer = new RefactoringDescriptorContainer(
						refactoringDescriptor);
				try {
					repository
							.addSessionDataForUncommittedChanges(descriptorContainer);
				} catch (IOException e) {
					logError(e);
				}
			}
		}
		refactoringDescriptor = null;
	}

}
