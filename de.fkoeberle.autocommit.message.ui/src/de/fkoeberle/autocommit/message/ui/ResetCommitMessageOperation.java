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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fkoeberle.autocommit.message.CommitMessageDescription;

public class ResetCommitMessageOperation extends
		AbstractProfileCustomizingOperation {
	private final CommitMessageDescription messageDescription;
	private String oldMessage;

	public ResetCommitMessageOperation(Model model,
			CommitMessageDescription messageDescription) {
		super("Reset Commit Message", model);
		this.messageDescription = messageDescription;
	}

	@Override
	public IStatus executeHook(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.oldMessage = messageDescription.getCurrentValue();
		messageDescription.reset();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undoHook(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		messageDescription.setCurrentValue(oldMessage);
		return Status.OK_STATUS;
	}
}
