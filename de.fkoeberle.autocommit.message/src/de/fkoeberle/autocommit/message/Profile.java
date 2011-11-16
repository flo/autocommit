package de.fkoeberle.autocommit.message;

import java.util.Iterator;
import java.util.List;

public class Profile implements Iterable<ICommitMessageFactory> {
	private final List<ICommitMessageFactory> factories;

	public Profile(List<ICommitMessageFactory> factories) {
		this.factories = factories;
	}

	public List<ICommitMessageFactory> getFactories() {
		return factories;
	}

	@Override
	public Iterator<ICommitMessageFactory> iterator() {
		return factories.iterator();
	}
}
