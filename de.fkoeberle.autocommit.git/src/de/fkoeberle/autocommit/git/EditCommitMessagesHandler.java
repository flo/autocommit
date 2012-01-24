/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.git;

import java.util.LinkedHashSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fkoeberle.autocommit.AutoCommitPluginActivator;
import de.fkoeberle.autocommit.IRepository;
import de.fkoeberle.autocommit.SelectionSearchUtil;

public class EditCommitMessagesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		LinkedHashSet<IProject> projects = SelectionSearchUtil
				.searchProjectsIn(selection);
		for (IProject project : projects) {
			try {
				IRepository repository = AutoCommitPluginActivator.getDefault()
						.getRepositoryFor(project);
				GitRepositoryAdapter repositoryAdapter = (GitRepositoryAdapter) repository;
				repositoryAdapter.editCommitMessagesFor(project);
			} catch (Exception e) {
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		return null;
	}

}
