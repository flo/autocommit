package de.fkoeberle.autocommit.message;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;

public class ProfileManager {
	private static final String PROFILE_EXTENSION_POINT_ID = "de.fkoeberle.autocommit.message.profile";
	private static final String FACTORY_EXTENSION_POINT_ID = "de.fkoeberle.autocommit.message.factory";
	private SoftReference<ProfileData> lastProfile;
	private final IRegistryEventListener factoryExtensionPointListener;
	private final IRegistryEventListener profileExtensionPointListener;

	private static final class ProfileData {
		private final List<ICommitMessageFactory> factories;

		public ProfileData(List<ICommitMessageFactory> factories) {
			this.factories = factories;
		}

		public List<ICommitMessageFactory> getFactories() {
			return factories;
		}
	}

	public ProfileManager() throws CoreException {
		lastProfile = new SoftReference<ProfileManager.ProfileData>(
				null);

		factoryExtensionPointListener = new RegistryEventListener();
		profileExtensionPointListener = new RegistryEventListener();
		Platform.getExtensionRegistry().addListener(
				factoryExtensionPointListener,
				FACTORY_EXTENSION_POINT_ID);
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
			lastProfile.clear();
		}

		@Override
		public void removed(IExtension[] extensions) {
			lastProfile.clear();
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

	public List<ICommitMessageFactory> getFirstProfileFactories() {
		ProfileData profile = lastProfile.get();
		if (profile == null) {
			profile = createFirstProfile();
			lastProfile = new SoftReference<ProfileManager.ProfileData>(profile);
		}
		
		return Collections.unmodifiableList(profile.getFactories());
	}

	public ProfileData createFirstProfile() {
		Map<String, IConfigurationElement> facotoryConfigMap = getFactoryConfigurations();

		IExtensionPoint profileExtensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(PROFILE_EXTENSION_POINT_ID);
		IConfigurationElement[] profileConfigurations = profileExtensionPoint
				.getConfigurationElements();
		if (profileConfigurations.length == 0) {
			throw new RuntimeException("No profile found");
		}
		IConfigurationElement firstProfileConfiguration = profileConfigurations[0];
		List<ICommitMessageFactory> factories = createFactories(
				firstProfileConfiguration, facotoryConfigMap);
		return new ProfileData(factories);

	}

	private List<ICommitMessageFactory> createFactories(
			IConfigurationElement firstProfileConfiguration,
			Map<String, IConfigurationElement> facotoryConfigMap) {
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

}
