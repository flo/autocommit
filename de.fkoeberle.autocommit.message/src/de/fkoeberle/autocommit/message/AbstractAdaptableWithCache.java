package de.fkoeberle.autocommit.message;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

public abstract class AbstractAdaptableWithCache implements IAdaptableWithCache {
	private final Map<Class<?>, SoftReference<Object>> cache = new HashMap<Class<?>, SoftReference<Object>>();

	@Override
	public final <T> T getAdapter(Class<T> adapterClass) {
		SoftReference<Object> softReference = cache.get(adapterClass);
		Object object = null;
		if (softReference != null) {
			object = softReference.get();
		}
		if (object == null) {
			object = Platform.getAdapterManager().loadAdapter(this,
					adapterClass.getCanonicalName());
			if (object != null) {
				softReference = new SoftReference<Object>(object);
				cache.put(adapterClass, softReference);
			}
		}
		return adapterClass.cast(object);
	}
}
