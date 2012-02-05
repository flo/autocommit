/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fkoeberle.autocommit.message.MessagePluginActivator;
import de.fkoeberle.autocommit.message.ProfileDescription;
import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;

public class SetCurrentProfileOperation extends AbstractOperation {
	private final Model model;
	private ProfileIdResourceAndName oldProfileId;
	private final ProfileIdResourceAndName newProfileId;
	private List<Object> oldUsedFactories;
	private List<Object> oldUnusedFactories;

	public SetCurrentProfileOperation(Model model,
			ProfileIdResourceAndName newProfileId) {
		super("Set Profile Operation");
		this.model = model;
		this.newProfileId = newProfileId;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (newProfileId.getId() != null) {
			ProfileDescription profileDescription;
			try {
				profileDescription = MessagePluginActivator
						.createProfileDescription(newProfileId.getResource());
			} catch (IOException e) {
				throw new ExecutionException(
						"Profile switch failed because of an IOException", e);
			}
			oldUsedFactories = copyOf(model.getFactoryDescriptions());
			oldUnusedFactories = copyOf(model.getUnusedFactoryDescriptions());
			model.loadFactoriesFor(profileDescription);
		}
		oldProfileId = model.getCurrentProfile();
		model.setCurrentProfileForOperations(newProfileId);
		return Status.OK_STATUS;
	}

	private List<Object> copyOf(WritableList original) {
		List<Object> result = new ArrayList<Object>(original.size());
		for (Object factory : original) {
			result.add(factory);
		}
		return result;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		WritableList usedFactories = model.getFactoryDescriptions();
		WritableList unusedFactories = model.getUnusedFactoryDescriptions();
		usedFactories.clear();
		usedFactories.addAll(oldUsedFactories);
		unusedFactories.clear();
		unusedFactories.addAll(oldUnusedFactories);
		model.setCurrentProfileForOperations(oldProfileId);
		return Status.OK_STATUS;
	}
}
