package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

public class ProfileDescription {
	private final List<CommitMessageFactoryDescription> factoryDescriptions;

	public ProfileDescription(List<CommitMessageFactoryDescription> list) {
		this.factoryDescriptions = list;

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

}
