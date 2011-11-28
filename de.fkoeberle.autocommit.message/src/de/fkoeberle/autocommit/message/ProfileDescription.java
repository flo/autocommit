package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

public class ProfileDescription {
	private final String defaultProfileId;
	private final List<CommitMessageFactoryDescription> factoryDescriptions;

	public ProfileDescription(List<CommitMessageFactoryDescription> list,
			String defaultProfileId) {
		this.factoryDescriptions = list;
		this.defaultProfileId = defaultProfileId;
	}

	public List<CommitMessageFactoryDescription> getFactoryDescriptions() {
		return factoryDescriptions;
	}

	public Profile createProfile() {
		List<ICommitMessageFactory> factories = new ArrayList<ICommitMessageFactory>();
		for (CommitMessageFactoryDescription factoryDescription : factoryDescriptions) {
			ICommitMessageFactory factory = factoryDescription.createFactory();
			factories.add(factory);
		}
		return new Profile(factories);
	}

	public String getDefaultProfileId() {
		return defaultProfileId;
	}
}
