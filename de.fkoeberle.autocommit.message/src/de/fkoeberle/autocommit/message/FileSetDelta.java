/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

public final class FileSetDelta {
	private final List<ChangedFile> changedFiles;
	private final List<AddedFile> addedFiles;
	private final List<RemovedFile> removedFiles;

	public FileSetDelta(List<ChangedFile> changedFiles,
			List<AddedFile> addedFiles, List<RemovedFile> removedFiles) {
		this.changedFiles = changedFiles;
		this.addedFiles = addedFiles;
		this.removedFiles = removedFiles;
	}

	public FileSetDelta() {
		this.changedFiles = new ArrayList<ChangedFile>(0);
		this.addedFiles = new ArrayList<AddedFile>(0);
		this.removedFiles = new ArrayList<RemovedFile>(0);
	}

	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	public List<AddedFile> getAddedFiles() {
		return addedFiles;
	}

	public List<RemovedFile> getRemovedFiles() {
		return removedFiles;
	}

}
