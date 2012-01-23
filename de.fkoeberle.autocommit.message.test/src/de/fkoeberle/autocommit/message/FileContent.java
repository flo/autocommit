/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.io.OutputStream;

import de.fkoeberle.autocommit.message.IFileContent;

public class FileContent implements IFileContent {
	private final byte[] buffer;
	private final String content;

	public FileContent(String content) {
		this.buffer = content.getBytes();
		this.content = content;
	}

	@Override
	public byte[] getBytesForReadOnlyPurposes() throws IOException {
		return buffer;
	}

	@Override
	public long getSize() throws IOException {
		return buffer.length;
	}

	@Override
	public void copyTo(OutputStream outputStream) throws IOException {
		outputStream.write(buffer);
	}

	@Override
	public String toString() {
		return content;
	}

}
