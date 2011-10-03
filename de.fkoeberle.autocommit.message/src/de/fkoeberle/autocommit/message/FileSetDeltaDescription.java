package de.fkoeberle.autocommit.message;

import java.util.List;

public class FileSetDeltaDescription implements ICommitDescription {
	private List<ChangedFile> changedFiles;
	private List<AddedFile> addedFiles;
	private List<RemovedFile> removedFiles;
	public FileSetDeltaDescription(List<ChangedFile> changedFiles,
			List<AddedFile> addedFiles, List<RemovedFile> removedFiles) {
		this.changedFiles = changedFiles;
		this.addedFiles = addedFiles;
		this.removedFiles = removedFiles;
	}
	
	@Override
	public String buildMessage() {
		return "Worked on " + findCommonPrefix();
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
	
	public String findCommonPrefix() {
		CommonPrefixFinder finder = new CommonPrefixFinder();
		for (ChangedFile file: changedFiles) {
			finder.checkForShorterPrefix(file.getPath());
		}
		for (AddedFile file: addedFiles) {
			finder.checkForShorterPrefix(file.getPath());
		}
		for (RemovedFile file: removedFiles) {
			finder.checkForShorterPrefix(file.getPath());
		}
		return finder.getPrefix();
	}
	
}
