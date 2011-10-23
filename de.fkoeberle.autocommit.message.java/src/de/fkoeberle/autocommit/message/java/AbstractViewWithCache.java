package de.fkoeberle.autocommit.message.java;

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
