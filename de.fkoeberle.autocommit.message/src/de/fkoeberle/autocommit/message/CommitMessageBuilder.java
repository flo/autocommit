package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommitMessageBuilder implements ICommitMessageBuilder {
	private final ProfileManager profileManager;
	private boolean dirty;
	private final List<ChangedFile> changedFiles;
	private final List<AddedFile> addedFiles;
	private final List<RemovedFile> removedFiles;
	private final Session session;

	CommitMessageBuilder(ProfileManager factoryManager) {
		this.profileManager = factoryManager;
		this.session = new Session();
		this.changedFiles = new ArrayList<ChangedFile>();
		this.addedFiles = new ArrayList<AddedFile>();
		this.removedFiles = new ArrayList<RemovedFile>();
	}

	@Override
	public void addChangedFile(String path, IFileContent oldContent,
			IFileContent newContent) throws IOException {
		changedFiles.add(new ChangedFile(path, oldContent, newContent));
	}

	@Override
	public void addDeletedFile(String path, IFileContent oldContent)
			throws IOException {
		removedFiles.add(new RemovedFile(path, oldContent));
	}

	@Override
	public void addAddedFile(String path, IFileContent newContent)
			throws IOException {
		addedFiles.add(new AddedFile(path, newContent));
	}

	@Override
	public void addSessionData(Object data) {
		session.add(data);
	}

	@Override
	public String buildMessage() throws IOException {
		if (dirty) {
			throw new IllegalStateException(
					"buildMessage() has already been called! Create a new builder!");
		}
		dirty = true;

		FileSetDelta delta = new FileSetDelta(changedFiles, addedFiles,
				removedFiles);

		List<ICommitMessageFactory> factories = profileManager
				.getFirstProfileFactories();

		session.add(delta);
		for (ICommitMessageFactory factory : factories) {
			session.injectSessionData(factory);
			String message = factory.createMessage();
			if (message != null) {
				return message;
			}
		}
		return null;
	}

}
