package de.fkoeberle.autocommit.message;

import java.util.HashMap;
import java.util.Map;

public final class Session {
	private final Map<Class<?>, Object> objects;

	public Session() {
		objects = new HashMap<Class<?>, Object>();
	}

	public <T> T getInstanceOf(Class<T> c) {
		Object object = objects.get(c);
		if (object == null) {
			try {
				object = c.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			objects.put(c, object);
		}
		return c.cast(object);
	}
}
