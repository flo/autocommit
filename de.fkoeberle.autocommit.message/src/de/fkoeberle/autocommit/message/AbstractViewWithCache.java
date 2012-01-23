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
import java.lang.ref.SoftReference;

public abstract class AbstractViewWithCache<T> {
	private boolean invalid;
	private SoftReference<T> ref;

	protected abstract T determineCachableValue() throws IOException;

	protected T getCachableValue() throws IOException {
		if (invalid) {
			return null;
		}
		T value = null;
		if (ref != null) {
			value = ref.get();
		}
		if (value == null) {
			value = determineCachableValue();
		}
		if (value == null) {
			invalid = true;
			return null;
		} else {
			ref = new SoftReference<T>(value);
			return value;
		}
	}

}
