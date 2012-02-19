/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

public final class WorkedOnDirectoryCMF implements ICommitMessageFactory {

	@InjectedBySession
	private FileSetDelta delta;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOn;

	@Override
	public String createMessage() {
		String prefix = findCommonPrefix();
		return workedOn.createMessageWithArgs(prefix);
	}

	private String findCommonPrefix() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		for (ChangedFile file : delta.getChangedFiles()) {
			finder.handleFilePath(file.getPath());
		}
		for (AddedFile file : delta.getAddedFiles()) {
			finder.handleFilePath(file.getPath());
		}
		for (RemovedFile file : delta.getRemovedFiles()) {
			finder.handleFilePath(file.getPath());
		}
		return finder.getCommonDirectory();
	}

}