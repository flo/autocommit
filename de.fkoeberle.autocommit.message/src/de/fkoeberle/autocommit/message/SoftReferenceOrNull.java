/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.lang.ref.SoftReference;

public final class SoftReferenceOrNull<T> {
	private final SoftReference<T> softReference;

	public SoftReferenceOrNull(T t) {
		this.softReference = t == null ? null : new SoftReference<T>(t);
	}

	public SoftReference<T> getSoftReference() {
		return softReference;
	}
}