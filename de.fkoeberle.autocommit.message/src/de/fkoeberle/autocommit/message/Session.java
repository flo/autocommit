package de.fkoeberle.autocommit.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class Session {
	private final Map<Class<?>, Object> objects;

	public Session(FileSetDelta delta) {
		objects = new HashMap<Class<?>, Object>();
		objects.put(FileSetDelta.class, delta);
	}

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
			injectSessionData(object);
			objects.put(c, object);
		}
		return c.cast(object);
	}

	public void injectSessionData(Object object) {
		injectSessionDataForClass(object, object.getClass());
	}

	private void injectSessionDataForClass(Object object,
			Class<?> classOrSuperClassOfObject) {
		if (classOrSuperClassOfObject.equals(Object.class))
			return;
		for (Field field : classOrSuperClassOfObject.getDeclaredFields()) {
			InjectedBySession annotation = field
					.getAnnotation(InjectedBySession.class);
			if (annotation != null) {
				Object value = getInstanceOf(field.getType());
				field.setAccessible(true);
				try {
					field.set(object, value);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
