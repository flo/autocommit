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
import java.io.OutputStream;

import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;

import de.fkoeberle.autocommit.message.IFileContent;

class FileContent implements IFileContent {
	private final ObjectId objectId;
	private final ObjectReader reader;
	private ObjectLoader loader;
	
	public FileContent(ObjectId objectId, ObjectReader reader) {
		this.objectId = objectId;
		this.reader = reader;
	}
	
	private ObjectLoader getLoader() throws IOException {
		if (loader == null) {
			loader = reader.open(objectId);
		}
		return loader;
	}

	@Override
	public long getSize() throws IOException {
		return getLoader().getSize();
	}
	
	@Override
	public void copyTo(OutputStream outputStream) throws IOException {
		getLoader().copyTo(outputStream);
	}

	@Override
	public byte[] getBytesForReadOnlyPurposes() throws IOException {
		try {
			return getLoader().getCachedBytes();
		} catch (LargeObjectException e) {
			throw new IOException(e);
		}
	}
	
}