package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;

public class CommitMessageFactoryManager implements
		Iterable<ICommitMessageFactory> {
	private static final String EXTENSION_POINT_ID = "de.fkoeberle.autocommit.message.factory";
	private final List<Extension> list;
	private final IRegistryEventListener listener;

	private static final class Extension {
		private final String id;
		private final ICommitMessageFactory factory;

		public Extension(String id, ICommitMessageFactory factory) {
			this.id = id;
			this.factory = factory;
		}

		public ICommitMessageFactory getFactory() {
			return factory;
		}

		public String getId() {
			return id;
		}

	}

	public CommitMessageFactoryManager() throws CoreException {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT_ID);

		list = new ArrayList<Extension>();
		for (IConfigurationElement e : elements) {
			addConfigurationElementsToList(e);
		}
		listener = new RegistryEventListener();
		Platform.getExtensionRegistry().addListener(listener,
				EXTENSION_POINT_ID);
	}

	protected void addConfigurationElementsToList(IConfigurationElement e)
			throws CoreException {
		final Object o = e.createExecutableExtension("class");
		if (o instanceof ICommitMessageFactory) {
			ICommitMessageFactory factory = (ICommitMessageFactory) o;
			final Extension extension = new Extension(e.getAttribute("id"),
					factory);
			list.add(extension);
		}
	}

	private void removeConfigurationElementsFromMap(IConfigurationElement e)
			throws CoreException {
		String idToDelete = e.getAttribute("id");

		Iterator<Extension> iterator = list.iterator();
		while (iterator.hasNext()) {
			Extension extension = iterator.next();
			if (extension.getId().equals(idToDelete)) {
				iterator.remove();
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
						addConfigurationElementsToList(e);
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

	@Override
	public Iterator<ICommitMessageFactory> iterator() {
		final Iterator<Extension> iterator = list.iterator();
		return new Iterator<ICommitMessageFactory>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public ICommitMessageFactory next() {
				return iterator.next().getFactory();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
