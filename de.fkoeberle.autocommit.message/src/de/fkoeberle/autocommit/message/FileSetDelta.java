package de.fkoeberle.autocommit.message;

import java.util.List;

public final class FileSetDelta {
	private final List<ChangedFile> changedFiles;
	private final List<AddedFile> addedFiles;
	private final List<RemovedFile> removedFiles;
	
	public FileSetDelta(List<ChangedFile> changedFiles,
			List<AddedFile> addedFiles, List<RemovedFile> removedFiles) {
		this.changedFiles = changedFiles;
		this.addedFiles = addedFiles;
		this.removedFiles = removedFiles;
	}

	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	public List<AddedFile> getAddedFiles() {
		return addedFiles;
	}

	public List<RemovedFile> getRemovedFiles() {
		return removedFiles;
	}
	
}
