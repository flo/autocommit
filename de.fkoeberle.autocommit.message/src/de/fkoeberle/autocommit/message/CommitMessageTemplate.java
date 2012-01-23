/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import org.eclipse.osgi.util.NLS;

public final class CommitMessageTemplate {
	private final String value;

	public CommitMessageTemplate(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String createMessageWithArgs(String... args) {
		return NLS.bind(value, args);
	}

}
