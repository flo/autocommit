/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class Session {
	private final Map<Class<?>, Object> objects;

	public Session() {
		objects = new HashMap<Class<?>, Object>();
	}

	/**
	 * This method returns either an object added with {@link #add(Object)} of
	 * the specified type or an instance of the specified class created via the
	 * default constructor. Instances created via the default constructor will
	 * get initialized by {@link #injectSessionData(Object)}. Two calls with the
	 * same class argument will return the same instance in both cases.
	 * 
	 * @param c
	 *            specified the type of returned value.
	 * @return an object of the specified class.
	 */
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

	/**
	 * Initializes all fields of the specified object which are annotated with
	 * {@link InjectedBySession}. This method initializes the fields by calling
	 * {@link #getInstanceOf(Class)} for each field. As argument it passes the
	 * type of the field.
	 * 
	 * @param object
	 *            the object to initialize.
	 */
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

	/**
	 * Adds a object to this session. Future calls to
	 * {@link Session#getInstanceOf(Class)} will return that object, if it gets
	 * called with the value of <code>data.getClass()</code>.
	 * 
	 * Only one instance per class can be added to the session.
	 * 
	 * @param data
	 *            the object to add to this session.
	 */
	public void add(Object data) {
		objects.put(data.getClass(), data);
	}
}
