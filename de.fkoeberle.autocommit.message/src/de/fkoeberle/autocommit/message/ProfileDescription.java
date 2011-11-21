package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;

public class ProfileDescription {
	private final WritableList factoryDescriptions;

	public ProfileDescription(Profile profile) {
		List<?> list = new ArrayList<CommitMessageFactoryDescription>(profile
				.getFactories().size());
		factoryDescriptions = new WritableList(list,
				CommitMessageFactoryDescription.class);

		for (ICommitMessageFactory factory : profile.getFactories()) {
			CommitMessageFactoryDescription factoryDescription = new CommitMessageFactoryDescription(
					factory);
			factoryDescriptions.add(factoryDescription);
		}

	}

	public WritableList getFactoryDescriptions() {
		return factoryDescriptions;
	}

}
