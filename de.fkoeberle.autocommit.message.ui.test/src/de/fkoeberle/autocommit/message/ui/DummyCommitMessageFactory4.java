/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;

import de.fkoeberle.autocommit.message.ICommitMessageFactory;

public class DummyCommitMessageFactory4 implements ICommitMessageFactory {

	@Override
	public String createMessage() throws IOException {
		return null;
	}

}
