package de.fkoeberle.autocommit.message;

import java.io.IOException;

public class WorkedOnFileCMF implements ICommitMessageFactory {
	@InjectedBySession
	private SingleChangedFileView singleChangedFileView;

	@Override
	public String createMessage() throws IOException {
		ChangedFile changedFile = singleChangedFileView.getChangedFile();
		if (changedFile == null) {
			return null;
		}
		return "Worked on file " + changedFile.getPath();
	}
}
