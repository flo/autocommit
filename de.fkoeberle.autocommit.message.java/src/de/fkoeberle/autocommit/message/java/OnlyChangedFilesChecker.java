package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class OnlyChangedFilesChecker extends AbstractViewWithCache<Boolean> {
	@InjectedBySession
	private FileSetDelta fileSetDelta;

	@Override
	protected Boolean determineCachableValue() throws java.io.IOException {
		if (fileSetDelta.getAddedFiles().size() != 0) {
			return Boolean.FALSE;
		}
		if (fileSetDelta.getRemovedFiles().size() != 0) {
			return Boolean.FALSE;
		}
		if (fileSetDelta.getChangedFiles().size() == 0) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;

	};

	/**
	 * 
	 * @return true if there are added or removed files or if there is no
	 *         changed file. If that's not the case and there are indeed only
	 *         changed files it returns false.
	 */
	public boolean checkFailed() throws IOException {
		return getCachableValue() == Boolean.FALSE;
	}
}
