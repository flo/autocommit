/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

public class ChangedFile {
	private final String path;
	private final IFileContent oldContent;
	private final IFileContent newContent;

	public ChangedFile(String path, IFileContent oldContent,
			IFileContent newContent) {
		this.path = path;
		this.oldContent = oldContent;
		this.newContent = newContent;
	}

	public String getPath() {
		return path;
	}

	public IFileContent getOldContent() {
		return oldContent;
	}

	public IFileContent getNewContent() {
		return newContent;
	}
}
