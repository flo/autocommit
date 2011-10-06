package de.fkoeberle.autocommit.message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FileSetDelta {
	private final List<ChangedFile> changedFiles;
	private final List<AddedFile> addedFiles;
	private final List<RemovedFile> removedFiles;
	private Set<String> fileExtensions;
	
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
