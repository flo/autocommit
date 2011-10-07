package de.fkoeberle.autocommit.message;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

public abstract class AbstractAdaptableWithCache implements IAdaptableWithCache {
	private final Map<Class<?>, Object> cache = new HashMap<Class<?>, Object>();

	@Override
	public final <T> T getAdapter(Class<T> adapterClass) {
		Object object = cache.get(adapterClass);
		if (object == null) {
			object = Platform.getAdapterManager().loadAdapter(this,
					adapterClass.getCanonicalName());
			cache.put(adapterClass, object);
		}
		return adapterClass.cast(object);
	}
}
