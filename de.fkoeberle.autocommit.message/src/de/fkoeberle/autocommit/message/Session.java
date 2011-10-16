package de.fkoeberle.autocommit.message;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;

public class Session implements ISession {
	private final WeakHashMap<Object, Map<Class<?>, SoftReferenceOrNull>> cache;

	private final static class SoftReferenceOrNull {
		private final SoftReference<?> softReference;

		public SoftReferenceOrNull(SoftReference<?> softReference) {
			this.softReference = softReference;
		}

		public SoftReference<?> getSoftReference() {
			return softReference;
		}
	}

	public Session() {
		cache = new WeakHashMap<Object, Map<Class<?>, SoftReferenceOrNull>>();
	}

	@Override
	public <T> T getSharedAdapter(Object adaptable, Class<T> requestedClass)
			throws AdapterNotFoundException {
		Map<Class<?>, SoftReferenceOrNull> cacheForAdaptable = cache
				.get(adaptable);
		if (cacheForAdaptable == null) {
			cacheForAdaptable = new HashMap<Class<?>, SoftReferenceOrNull>();
		}
		SoftReferenceOrNull softReferenceOrNull = cacheForAdaptable
				.get(requestedClass);

		Object result = null;
		if (softReferenceOrNull != null) {
			SoftReference<?> softReference = softReferenceOrNull
					.getSoftReference();
			if (softReference == null) {
				// null was stored as a result
				return null;
			} else {
				result = softReference.get();
			}
		}

		if (result == null) {
			result = Platform.getAdapterManager().loadAdapter(this,
					requestedClass.getCanonicalName());
			if (result == null) {
				int adapterState = Platform.getAdapterManager().queryAdapter(
						adaptable, requestedClass.getCanonicalName());
				if (adapterState == IAdapterManager.NONE) {
					throw new AdapterNotFoundException(requestedClass);
				}
				softReferenceOrNull = new SoftReferenceOrNull(
						new SoftReference<Object>(result));
			} else {
				softReferenceOrNull = new SoftReferenceOrNull(null);
			}
			cacheForAdaptable.put(requestedClass, softReferenceOrNull);
		}
		return requestedClass.cast(result);
	}
}
