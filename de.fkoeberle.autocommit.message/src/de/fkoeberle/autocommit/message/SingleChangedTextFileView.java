package de.fkoeberle.autocommit.message;

import java.io.IOException;

public class SingleChangedTextFileView extends
		AbstractViewWithCache<ChangedTextFile> {
	@InjectedBySession
	private SingleChangedFileView singleChangedFileView;

	@InjectedBySession
	private FileContentReader fileContentReader;

	@Override
	protected ChangedTextFile determineCachableValue() throws IOException {
		ChangedFile changedFile = singleChangedFileView.getChangedFile();
		if (changedFile == null) {
			return null;
		}
		String oldContent = fileContentReader.getStringFor(changedFile
				.getOldContent());
		String newContent = fileContentReader.getStringFor(changedFile
				.getNewContent());
		String path = changedFile.getPath();
		return new ChangedTextFile(path, oldContent, newContent);
	}

	public ChangedTextFile getChangedTextFile() throws IOException {
		return getCachableValue();
	}

}
