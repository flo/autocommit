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

interface FileSetDeltaVisitor {
	void visitAddedFile(String path, ObjectId newObjectId)
			throws IOException;

	void visitRemovedFile(String path, ObjectId oldObjectId)
			throws IOException;

	void visitChangedFile(String path, ObjectId oldObjectId,
			ObjectId newObjectId) throws IOException;
}