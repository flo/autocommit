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