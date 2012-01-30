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
import java.util.Iterator;
import java.util.List;

public class Profile implements Iterable<ICommitMessageFactory> {
	private final List<ICommitMessageFactory> factories;

	public Profile(List<ICommitMessageFactory> factories) {
		this.factories = factories;
	}

	public List<ICommitMessageFactory> getFactories() {
		return factories;
	}

	@Override
	public Iterator<ICommitMessageFactory> iterator() {
		return factories.iterator();
	}

	public String generateMessage(Session session) throws IOException {
		for (ICommitMessageFactory factory : factories) {
			session.injectSessionData(factory);
			String message = factory.createMessage();
			if (message != null) {
				return message;
			}
		}
		return null;
	}
}
