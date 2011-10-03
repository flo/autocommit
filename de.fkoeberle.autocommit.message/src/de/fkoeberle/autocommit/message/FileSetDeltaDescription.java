package de.fkoeberle.autocommit.message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileSetDeltaDescription implements ICommitDescription {
	private List<ChangedFile> changedFiles;
	private List<AddedFile> addedFiles;
	private List<RemovedFile> removedFiles;
	private Set<String> fileExtensions;
	
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

	private static String fileExtensionOf(String path) {
		int lastDot = path.lastIndexOf(".");
		int lastSlash = path.lastIndexOf("/");
		if (lastDot == -1) {
			return "";
		}
		if ((lastSlash != -1) && (lastSlash > lastDot)) {
			return "";
		}
		return path.substring(lastDot + 1, path.length());
	}
	public Set<String> getFileExtensions() {
		if (fileExtensions == null) {
			fileExtensions = new HashSet<String>();
			for (ChangedFile file: changedFiles) {
				fileExtensions.add(fileExtensionOf(file.getPath()));
			}
			for (AddedFile file: addedFiles) {
				fileExtensions.add(fileExtensionOf(file.getPath()));
			}
			for (RemovedFile file: removedFiles) {
				fileExtensions.add(fileExtensionOf(file.getPath()));
			}
		}
		return fileExtensions;
	}
	
}
