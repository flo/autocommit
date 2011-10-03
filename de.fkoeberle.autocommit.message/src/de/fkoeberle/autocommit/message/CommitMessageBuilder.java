package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class CommitMessageBuilder implements ICommitMessageBuilder {
	private CommitMessageEnhancerManager enhancerManager;
	private boolean dirty;
	private List<ChangedFile> changedFiles;
	private List<AddedFile> addedFiles;
	private List<RemovedFile> removedFiles;

	CommitMessageBuilder(CommitMessageEnhancerManager enhancerManager) {
		this.enhancerManager = enhancerManager;
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
		
		ICommitDescription description = new FileSetDeltaDescription(
				changedFiles, addedFiles, removedFiles);
		
		boolean enhanced;
		do {
			enhanced = false;
			Collection<ICommitMessageEnhancer> enhancers = enhancerManager.getEnhancersFor(description);
			Iterator<ICommitMessageEnhancer> enhancersIterator = enhancers.iterator();
			while (!enhanced && enhancersIterator.hasNext()) {
				ICommitMessageEnhancer enhancer = enhancersIterator.next();
				ICommitDescription newDescription = enhancer.enhance(description);
				if (newDescription != null) {
					enhanced = true;
					description = newDescription;
				}
			}
		} while (enhanced);
		
		return description.buildMessage();
	}

}
