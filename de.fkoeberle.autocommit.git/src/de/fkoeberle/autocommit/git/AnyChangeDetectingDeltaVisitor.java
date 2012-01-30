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

public class AnyChangeDetectingDeltaVisitor implements GitFileSetDeltaVisitor {
	private boolean detectedChange = false;

	@Override
	public void visitAddedFile(String path, ObjectId newObjectId)
			throws IOException {
		detectedChange = true;

	}

	@Override
	public void visitRemovedFile(String path, ObjectId oldObjectId)
			throws IOException {
		detectedChange = true;
	}

	@Override
	public void visitChangedFile(String path, ObjectId oldObjectId,
			ObjectId newObjectId) throws IOException {
		detectedChange = true;
	}

	public boolean hasDetectedChange() {
		return detectedChange;
	}

}
