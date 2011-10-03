package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;

public class CommitMessageBuilder implements ICommitMessageBuilder {
	private List<ChangedFile> changedFiles;
	private List<AddedFile> addedFiles;
	private List<RemovedFile> removedFiles;
	
	@Override
	public void addChangedFile(IProject project, String path,
			IFileContent oldContent, IFileContent newContent)
			throws IOException {
		changedFiles.add(new ChangedFile(path,oldContent,newContent));
	}

	@Override
	public void addDeletedFile(IProject project, String path,
			IFileContent oldContent) throws IOException {
		removedFiles.add(new RemovedFile(path,oldContent));
	}

	@Override
	public void addAddedFile(IProject project, String path,
			IFileContent newContent) throws IOException {
		addedFiles.add(new AddedFile(path,newContent));
	}

	@Override
	public String buildMessage() throws IOException {
		FileSetDeltaDescription fileSetDelta = new FileSetDeltaDescription(changedFiles, addedFiles, removedFiles);
		
		return fileSetDelta.buildMessage();
	}

}
