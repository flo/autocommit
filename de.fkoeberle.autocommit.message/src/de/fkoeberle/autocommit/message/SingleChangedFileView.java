/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;


public class SingleChangedFileView {
	private boolean changedFileDetermined;
	private ChangedFile changedFile;

	@InjectedBySession
	private FileSetDelta delta;


	public ChangedFile getChangedFile() {
		if (!changedFileDetermined) {
			changedFileDetermined = true;
			if (delta.getChangedFiles().size() != 1) {
				return null;
			}
			if (delta.getRemovedFiles().size() != 0) {
				return null;
			}
			if (delta.getAddedFiles().size() != 0) {
				return null;
			}

			changedFile = delta.getChangedFiles().get(0);
		}
		return changedFile;
	}
}
