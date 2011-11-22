package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;

public class ProfileDescription {
	private final List<CommitMessageFactoryDescription> unobservedFactoryDescriptions;
	private final WritableList factoryDescriptions;

	public ProfileDescription(List<CommitMessageFactoryDescription> list) {
		this.unobservedFactoryDescriptions = list;
		this.factoryDescriptions = new WritableList(list,
				CommitMessageFactoryDescription.class);
	}

	public WritableList getFactoryDescriptions() {
		return factoryDescriptions;
	}

	public Profile createProfile() {
		List<ICommitMessageFactory> factories = new ArrayList<ICommitMessageFactory>();
		for (CommitMessageFactoryDescription factoryDescription : unobservedFactoryDescriptions) {
			ICommitMessageFactory factory = factoryDescription.createFactory();
			factories.add(factory);
		}
		return new Profile(factories);
	}

}
