package de.fkoeberle.autocommit.message.java.test;

import java.util.ArrayList;
import java.util.List;

import de.fkoeberle.autocommit.message.AddedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ModifiedFile;
import de.fkoeberle.autocommit.message.RemovedFile;

public class FileDeltaBuilder {
	List<ModifiedFile> changedFiles = new ArrayList<ModifiedFile>();
	List<AddedFile> addedFiles = new ArrayList<AddedFile>();
	List<RemovedFile> removedFiles = new ArrayList<RemovedFile>();

	public void addAddedFile(String path, String content) {
		addedFiles.add(new AddedFile(path, new FileContent(content)));
	}

	public FileSetDelta build() {
		return new FileSetDelta(changedFiles, addedFiles, removedFiles);
	}
}
