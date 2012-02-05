/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;

public abstract class AbstractProfileCustomizingOperation extends
		AbstractOperation {
	private final Model model;
	private ProfileIdResourceAndName oldProfileId;

	public AbstractProfileCustomizingOperation(String name, Model model) {
		super(name);
		this.model = model;
	}

	public abstract IStatus executeHook(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException;

	public abstract IStatus undoHook(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException;

	@Override
	public final IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		oldProfileId = model.getCurrentProfile();
		model.setCurrentProfileForOperations(Model.CUSTOM_PROFILE);
		executeHook(monitor, info);
		return Status.OK_STATUS;
	}

	@Override
	public final IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public final IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		undoHook(monitor, info);
		model.setCurrentProfileForOperations(oldProfileId);
		return Status.OK_STATUS;
	}

}