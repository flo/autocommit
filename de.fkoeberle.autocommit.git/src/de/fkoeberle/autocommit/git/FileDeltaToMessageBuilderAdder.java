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

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;

import de.fkoeberle.autocommit.message.ICommitMessageBuilder;

final class FileDeltaToMessageBuilderAdder implements
		FileSetDeltaVisitor {
	private final ObjectReader reader;
	private final ICommitMessageBuilder messageBuilder;

	FileDeltaToMessageBuilderAdder(ObjectReader reader,
			ICommitMessageBuilder messageBuilder) {
		this.reader = reader;
		this.messageBuilder = messageBuilder;
	}

	@Override
	public void visitAddedFile(String path, ObjectId newObjectId)
			throws IOException {
		messageBuilder.addAddedFile(path, new FileContent(newObjectId,
				reader));

	}

	@Override
	public void visitRemovedFile(String path, ObjectId oldObjectId)
			throws IOException {
		messageBuilder.addDeletedFile(path, new FileContent(oldObjectId,
				reader));

	}

	@Override
	public void visitChangedFile(String path, ObjectId oldObjectId,
			ObjectId newObjectId) throws IOException {
		messageBuilder.addChangedFile(path, new FileContent(oldObjectId,
				reader), new FileContent(newObjectId, reader));

	}
}