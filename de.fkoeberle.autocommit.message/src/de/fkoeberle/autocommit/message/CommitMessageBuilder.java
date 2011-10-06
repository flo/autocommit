package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CommitMessageBuilder implements ICommitMessageBuilder {
	private final CommitMessageFactoryManager factoryManager;
	private boolean dirty;
	private final List<ChangedFile> changedFiles;
	private final List<AddedFile> addedFiles;
	private final List<RemovedFile> removedFiles;

	CommitMessageBuilder(CommitMessageFactoryManager factoryManager) {
		this.factoryManager = factoryManager;
		this.changedFiles = new ArrayList<ChangedFile>();
		this.addedFiles = new ArrayList<AddedFile>();
		this.removedFiles = new ArrayList<RemovedFile>();
	}

	@Override
	public void addChangedFile(String path, IFileContent oldContent,
			IFileContent newContent)
			throws IOException {
		changedFiles.add(new ChangedFile(path, oldContent, newContent));
	}

	@Override
	public void addDeletedFile(String path, IFileContent oldContent) throws IOException {
		removedFiles.add(new RemovedFile(path, oldContent));
	}

	@Override
	public void addAddedFile(String path, IFileContent newContent) throws IOException {
		addedFiles.add(new AddedFile(path, newContent));
	}

	@Override
	public String buildMessage() throws IOException {
		if (dirty) {
			throw new IllegalStateException("buildMessage() has already been called! Create a new builder!");
		}
		dirty = true;
		
		FileSetDelta delta = new FileSetDelta(
				changedFiles, addedFiles, removedFiles);
		
		for (ICommitMessageFactory factory : factoryManager) {
			return factory.build(delta);
		}
		throw new IOException("No commit message factory specified");
	}

}
