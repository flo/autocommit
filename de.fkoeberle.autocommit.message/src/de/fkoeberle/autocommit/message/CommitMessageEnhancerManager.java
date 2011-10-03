package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class CommitMessageEnhancerManager {
	private static final String EXTENSION_POINT_ID = "de.fkoeberle.autocommit.message.enhancer";
	private Map<Class<?>, List<ICommitMessageEnhancer>> map;
	private IRegistryEventListener listener;

	public CommitMessageEnhancerManager() throws CoreException {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT_ID);

		map = new HashMap<Class<?>, List<ICommitMessageEnhancer>>();
		for (IConfigurationElement e : elements) {
			addConfigurationElementsToMap(e);
		}
		listener = new RegistryEventListener();
		Platform.getExtensionRegistry().addListener(listener,
				EXTENSION_POINT_ID);
	}

	/**
	 * 
	 * @param description
	 *            the {@link ICommitDescription} to get enhancers for.
	 * @return a collection of {@link ICommitMessageEnhancer} which are sorted
	 *         from high priority to low priority enhancers.
	 */
	Collection<ICommitMessageEnhancer> getEnhancersFor(
			ICommitDescription description) {
		Collection<ICommitMessageEnhancer> result = map.get(description.getClass());
		if (result != null) {
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	protected void addConfigurationElementsToMap(IConfigurationElement e)
			throws CoreException {
		final Object o = e.createExecutableExtension("enhancer");
		if (o instanceof ICommitMessageEnhancer) {
			String inputClassName = e.getAttribute("input");
			Class<?> inputClass;
			try {
				inputClass = Class.forName(inputClassName);
			} catch (ClassNotFoundException e1) {
				throw new CoreException(new Status(IStatus.ERROR,
						CommitMessageBuilderPluginActivator.PLUGIN_ID,
						"Can't resolve input attribute of extension", e1));
			}
			List<ICommitMessageEnhancer> enhancerList = map.get(inputClass);
			if (enhancerList == null) {
				enhancerList = new ArrayList<ICommitMessageEnhancer>(1);
				map.put(inputClass, enhancerList);
			}
			enhancerList.add((ICommitMessageEnhancer) o);
		}
	}

	private void removeConfigurationElementsFromMap(IConfigurationElement e)
			throws CoreException {
		String enhancerClassName = e.getAttribute("enhancer");
		String inputClassName = e.getAttribute("input");
		Class<?> inputClass;
		try {
			inputClass = Class.forName(inputClassName);
		} catch (ClassNotFoundException e1) {
			throw new CoreException(new Status(IStatus.ERROR,
					CommitMessageBuilderPluginActivator.PLUGIN_ID,
					"Can't resolve input attribute of extension", e1));
		}
		Class<?> enhancerClass;
		try {
			enhancerClass = Class.forName(enhancerClassName);
		} catch (ClassNotFoundException e1) {
			throw new CoreException(new Status(IStatus.ERROR,
					CommitMessageBuilderPluginActivator.PLUGIN_ID,
					"Can't resolve enhancer attribute of extension", e1));
		}
		List<ICommitMessageEnhancer> enhancerList = map.get(inputClass);
		Iterator<ICommitMessageEnhancer> enhancerIterator = enhancerList
				.iterator();
		while (enhancerIterator.hasNext()) {
			ICommitMessageEnhancer enhancer = enhancerIterator.next();
			if (enhancer.getClass().equals(enhancerClass)) {
				enhancerIterator.remove();
			}
			if (enhancerList.isEmpty()) {
				map.remove(inputClass);
			}
		}
	}

	private final class RegistryEventListener implements IRegistryEventListener {
		@Override
		public void added(IExtension[] extensions) {
			for (IExtension extension : extensions) {
				for (IConfigurationElement e : extension
						.getConfigurationElements()) {
					try {
						addConfigurationElementsToMap(e);
					} catch (CoreException ex) {
						CommitMessageBuilderPluginActivator.getDefault()
								.getLog().log(ex.getStatus());
					}
				}
			}
		}

		@Override
		public void removed(IExtension[] extensions) {
			for (IExtension extension : extensions) {
				for (IConfigurationElement e : extension
						.getConfigurationElements()) {
					try {
						removeConfigurationElementsFromMap(e);
					} catch (CoreException ex) {
						CommitMessageBuilderPluginActivator.getDefault()
								.getLog().log(ex.getStatus());
					}
				}
			}
		}

		@Override
		public void added(IExtensionPoint[] extensionPoints) {
			// ignore
		}

		@Override
		public void removed(IExtensionPoint[] extensionPoints) {
			// ignore
		}
	}

}
