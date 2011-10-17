package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

public class FileDeltaBuilder {
	private final List<ModifiedFile> modifiedFiles = new ArrayList<ModifiedFile>();
	private final List<AddedFile> addedFiles = new ArrayList<AddedFile>();
	private final List<RemovedFile> removedFiles = new ArrayList<RemovedFile>();

	public void addAddedFile(String path, String content) {
		addedFiles.add(new AddedFile(path, new FileContent(content)));
	}

	public void addRemovedFile(String path, String content) {
		removedFiles.add(new RemovedFile(path, new FileContent(content)));
	}

	public void addModifiedFile(String path, String oldContent,
			String newContent) {
		modifiedFiles.add(new ModifiedFile(path, new FileContent(oldContent),
				new FileContent(newContent)));
	}

	public FileSetDelta build() {
		return new FileSetDelta(modifiedFiles, addedFiles, removedFiles);
	}


}
