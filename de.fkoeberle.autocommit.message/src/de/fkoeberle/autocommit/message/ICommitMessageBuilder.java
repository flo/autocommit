package de.fkoeberle.autocommit.message;

import java.io.IOException;

import org.eclipse.core.resources.IProject;

public interface ICommitMessageBuilder {

	void addChangedFile(IProject project, String path, IFileContent oldContent,
			IFileContent newContent) throws IOException;

	void addDeletedFile(IProject project, String path, IFileContent oldContent) throws IOException;

	void addAddedFile(IProject project, String path, IFileContent newContent) throws IOException;

	String buildMessage() throws IOException;

}
