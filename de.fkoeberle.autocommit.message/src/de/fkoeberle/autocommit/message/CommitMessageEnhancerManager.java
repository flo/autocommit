package de.fkoeberle.autocommit.message;

import java.util.Collection;
import java.util.Collections;

public class CommitMessageEnhancerManager {
	/**
	 * 
	 * @param description the {@link ICommitDescription} to get enhancers for.
	 * @return a collection of {@link ICommitMessageEnhancer} which are sorted from high priority to low priority enhancers.
	 */
	Collection<ICommitMessageEnhancer> getEnhancersFor(ICommitDescription description) {
		return Collections.emptySet();
	}
}
