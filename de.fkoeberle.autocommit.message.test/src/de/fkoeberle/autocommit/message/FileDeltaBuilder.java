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

public class FileDeltaBuilder {
	private final List<ChangedFile> changedFiles = new ArrayList<ChangedFile>();
	private final List<AddedFile> addedFiles = new ArrayList<AddedFile>();
	private final List<RemovedFile> removedFiles = new ArrayList<RemovedFile>();

	public void addAddedFile(String path, String content) {
		addedFiles.add(new AddedFile(path, new FileContent(content)));
	}

	public void addRemovedFile(String path, String content) {
		removedFiles.add(new RemovedFile(path, new FileContent(content)));
	}

	public void addChangedFile(String path, String oldContent,
			String newContent) {
		changedFiles.add(new ChangedFile(path, new FileContent(oldContent),
				new FileContent(newContent)));
	}

	public FileSetDelta build() {
		return new FileSetDelta(changedFiles, addedFiles, removedFiles);
	}


}
