package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ProfileManager {
	private static final String PROFILE_EXTENSION_POINT_ID = "de.fkoeberle.autocommit.message.profile";
	private static final String FACTORY_EXTENSION_POINT_ID = "de.fkoeberle.autocommit.message.factory";
	private final SoftReference<Profile> defaultProfile;
	private final IRegistryEventListener factoryExtensionPointListener;
	private final IRegistryEventListener profileExtensionPointListener;

	public ProfileManager() throws CoreException {
		defaultProfile = new SoftReference<Profile>(null);

		factoryExtensionPointListener = new RegistryEventListener();
		profileExtensionPointListener = new RegistryEventListener();
		Platform.getExtensionRegistry().addListener(
				factoryExtensionPointListener, FACTORY_EXTENSION_POINT_ID);
		Platform.getExtensionRegistry().addListener(
				profileExtensionPointListener, PROFILE_EXTENSION_POINT_ID);
	}

	public void dispose() {
		Platform.getExtensionRegistry().removeListener(
				factoryExtensionPointListener);
		Platform.getExtensionRegistry().removeListener(
				profileExtensionPointListener);
	}

	private final class RegistryEventListener implements IRegistryEventListener {
		@Override
		public void added(IExtension[] extensions) {
			defaultProfile.clear();
		}

		@Override
		public void removed(IExtension[] extensions) {
			defaultProfile.clear();
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

	public Profile createFirstProfile() throws IOException {
		IExtensionPoint profileExtensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(PROFILE_EXTENSION_POINT_ID);
		IConfigurationElement[] profileConfigurations = profileExtensionPoint
				.getConfigurationElements();
		if (profileConfigurations.length == 0) {
			throw new RuntimeException("No profile found");
		}
		IConfigurationElement firstProfileConfiguration = profileConfigurations[0];
		return createProfileFor(firstProfileConfiguration);

	}

	private Profile createProfileFor(IConfigurationElement configurationElement)
			throws IOException {
		ProfileXml profileXml = createProfileXmlFor(configurationElement);
		CMFFactory cmfFactory = new CMFFactory();
		return profileXml.createProfile(cmfFactory);
	}

	private ProfileXml createProfileXmlFor(
			IConfigurationElement configurationElement) throws IOException {
		String path = configurationElement.getAttribute("path");
		String contributorName = configurationElement.getContributor()
				.getName();
		Bundle contributorBundle = Platform.getBundle(contributorName);
		URL resource = contributorBundle.getResource(path);
		if (resource == null) {
			throw new RuntimeException("Unable to read resource " + path
					+ " of bundle " + contributorName);
		}
		ProfileXml profileXml = ProfileXml.createFrom(resource);
		return profileXml;
	}

	public Profile getDefault() throws IOException {
		return createFirstProfile();
	}

	private Map<String, IConfigurationElement> createFactoryIdToConfigurationMap() {
		IExtensionPoint factoryExtensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(FACTORY_EXTENSION_POINT_ID);
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

	private class CMFFactory implements ICommitMessageFactoryFactory {
		Map<String, IConfigurationElement> map = createFactoryIdToConfigurationMap();

		@Override
		public ICommitMessageFactory createFactory(String id) {
			if (id == null) {
				throw new NullPointerException("id must not be null");
			}
			IConfigurationElement element = map.get(id);
			if (element == null) {
				throw new RuntimeException("There is no factory with the id "
						+ id);
			}
			Object object;
			try {
				object = element.createExecutableExtension("class");
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
			return (ICommitMessageFactory) object;
		}
	}

}
