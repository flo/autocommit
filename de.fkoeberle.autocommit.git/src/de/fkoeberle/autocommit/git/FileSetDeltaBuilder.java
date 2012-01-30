/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;

import de.fkoeberle.autocommit.message.AddedFile;
import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.RemovedFile;

public class FileSetDeltaBuilder implements FileSetDeltaVisitor {
	private final ObjectReader reader;
	private final List<ChangedFile> changedFiles;
	private final List<AddedFile> addedFiles;
	private final List<RemovedFile> removedFiles;

	FileSetDeltaBuilder(ObjectReader reader) {
		this.reader = reader;
		this.changedFiles = new ArrayList<ChangedFile>();
		this.addedFiles = new ArrayList<AddedFile>();
		this.removedFiles = new ArrayList<RemovedFile>();
	}

	FileSetDeltaBuilder(Repository repository) {
		this(repository.newObjectReader());
	}

	@Override
	public void visitAddedFile(String path, ObjectId newObjectId)
			throws IOException {
		IFileContent newContent = new FileContent(newObjectId, reader);
		addedFiles.add(new AddedFile(path, newContent));
	}

	@Override
	public void visitRemovedFile(String path, ObjectId oldObjectId)
			throws IOException {
		IFileContent oldContent = new FileContent(oldObjectId, reader);
		removedFiles.add(new RemovedFile(path, oldContent));
	}

	@Override
	public void visitChangedFile(String path, ObjectId oldObjectId,
			ObjectId newObjectId) throws IOException {
		IFileContent oldContent = new FileContent(oldObjectId, reader);
		IFileContent newContent = new FileContent(newObjectId, reader);
		changedFiles.add(new ChangedFile(path, oldContent, newContent));
	}

	public FileSetDelta build() {
		return new FileSetDelta(changedFiles, addedFiles, removedFiles);
	}

}
