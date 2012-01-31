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

public interface IFileContent {

	long getSize() throws IOException;

	void copyTo(OutputStream out) throws IOException;

	byte[] getBytesForReadOnlyPurposes() throws IOException;
}
