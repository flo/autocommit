package de.fkoeberle.autocommit.message.java.test;

import java.util.ArrayList;
import java.util.List;

import de.fkoeberle.autocommit.message.AddedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ModifiedFile;
import de.fkoeberle.autocommit.message.RemovedFile;

public class FileDeltaBuilder {
	List<ModifiedFile> modifiedFiles = new ArrayList<ModifiedFile>();
	List<AddedFile> addedFiles = new ArrayList<AddedFile>();
	List<RemovedFile> removedFiles = new ArrayList<RemovedFile>();

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
