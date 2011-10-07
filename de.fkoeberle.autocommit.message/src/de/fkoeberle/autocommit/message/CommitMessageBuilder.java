package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public class CommitMessageBuilder implements ICommitMessageBuilder {
	private final CommitMessageFactoryManager factoryManager;
	private boolean dirty;
	private final List<ModifiedFile> changedFiles;
	private final List<AddedFile> addedFiles;
	private final List<RemovedFile> removedFiles;

	CommitMessageBuilder(CommitMessageFactoryManager factoryManager) {
		this.factoryManager = factoryManager;
		this.changedFiles = new ArrayList<ModifiedFile>();
		this.addedFiles = new ArrayList<AddedFile>();
		this.removedFiles = new ArrayList<RemovedFile>();
	}

	@Override
	public void addChangedFile(String path, IFileContent oldContent,
			IFileContent newContent) throws IOException {
		changedFiles.add(new ModifiedFile(path, oldContent, newContent));
	}

	@Override
	public void addDeletedFile(String path, IFileContent oldContent)
			throws IOException {
		removedFiles.add(new RemovedFile(path, oldContent));
	}

	@Override
	public void addAddedFile(String path, IFileContent newContent)
			throws IOException {
		addedFiles.add(new AddedFile(path, newContent));
	}

	@Override
	public String buildMessage() throws IOException {
		if (dirty) {
			throw new IllegalStateException(
					"buildMessage() has already been called! Create a new builder!");
		}
		dirty = true;

		FileSetDelta delta = new FileSetDelta(changedFiles, addedFiles,
				removedFiles);

		for (ICommitMessageFactory factory : getFactories()) {
			String message = factory.build(delta);
			if (message != null) {
				return message;
			}
		}
		throw new IOException(
				"There was no commit message factory which could provide a commit message");
	}

	public Iterable<ICommitMessageFactory> getFactories() {
		Map<String, IConfigurationElement> facotoryConfigMap = getFactoryConfigurations();

		IExtensionPoint profileExtensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint("de.fkoeberle.autocommit.message.profile");
		IConfigurationElement[] profileConfigurations = profileExtensionPoint
				.getConfigurationElements();
		if (profileConfigurations.length == 0) {
			return Collections.emptyList();
		}
		IConfigurationElement firstProfileConfiguration = profileConfigurations[0];
		List<ICommitMessageFactory> factories = new ArrayList<ICommitMessageFactory>();
		for (IConfigurationElement factoryIdConfig : firstProfileConfiguration
				.getChildren()) {
			final String factoryId = factoryIdConfig.getAttribute("id");
			IConfigurationElement factoryConfig = facotoryConfigMap
					.get(factoryId);
			if (factoryConfig == null) {
				// TODO log info about missing factory
			} else {
				Object o;
				try {
					o = factoryConfig.createExecutableExtension("class");
					if (o instanceof ICommitMessageFactory) {
						factories.add((ICommitMessageFactory) o);
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return factories;

	}

	private Map<String, IConfigurationElement> getFactoryConfigurations() {
		IExtensionPoint factoryExtensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint("de.fkoeberle.autocommit.message.factory");
		IConfigurationElement[] factoryConfigurations = factoryExtensionPoint
				.getConfigurationElements();

		Map<String, IConfigurationElement> facotoryConfigMap = new HashMap<String, IConfigurationElement>(
				factoryConfigurations.length);
		for (IConfigurationElement element : factoryConfigurations) {
			String id = element.getAttribute("id");
			facotoryConfigMap.put(id, element);
		}
		return facotoryConfigMap;
	}

}
