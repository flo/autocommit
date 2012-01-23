/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.net.URL;

public final class ProfileIdResourceAndName {
	private final String name;
	private final String id;
	private final URL resource;

	public ProfileIdResourceAndName(String id, URL resource, String name) {
		this.id = id;
		this.resource = resource;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public URL getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return name;
	}

}
