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


public interface ICommitMessageBuilder {

	void addChangedFile(String path, IFileContent oldContent, IFileContent newContent) throws IOException;

	void addDeletedFile(String path, IFileContent oldContent) throws IOException;

	void addAddedFile(String path, IFileContent newContent) throws IOException;

	String buildMessage() throws IOException;

	void addSessionData(Object data);

}
