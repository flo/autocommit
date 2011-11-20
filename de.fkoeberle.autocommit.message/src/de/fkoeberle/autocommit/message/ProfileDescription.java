package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;


public class ProfileDescription {
	private final List<CommitMessageFactoryDescription> factoryDescriptions;

	public ProfileDescription(Profile profile) {
		factoryDescriptions = new ArrayList<CommitMessageFactoryDescription>(profile
				.getFactories().size());
		for (ICommitMessageFactory factory : profile.getFactories()) {
			CommitMessageFactoryDescription factoryDescription = new CommitMessageFactoryDescription(factory);
			factoryDescriptions.add(factoryDescription);
		}

	}

	public List<CommitMessageFactoryDescription> getFactoryDescriptions() {
		return factoryDescriptions;
	}
}
