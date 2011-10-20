package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

public class FileDeltaBuilder {
	private final List<ChangedFile> changedFiles = new ArrayList<ChangedFile>();
	private final List<AddedFile> addedFiles = new ArrayList<AddedFile>();
	private final List<RemovedFile> removedFiles = new ArrayList<RemovedFile>();

	public void addAddedFile(String path, String content) {
		addedFiles.add(new AddedFile(path, new FileContent(content)));
	}

	public void addRemovedFile(String path, String content) {
		removedFiles.add(new RemovedFile(path, new FileContent(content)));
	}

	public void addChangedFile(String path, String oldContent,
			String newContent) {
		changedFiles.add(new ChangedFile(path, new FileContent(oldContent),
				new FileContent(newContent)));
	}

	public FileSetDelta build() {
		return new FileSetDelta(changedFiles, addedFiles, removedFiles);
	}


}
