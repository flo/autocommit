package de.fkoeberle.autocommit.message;

import java.io.IOException;


public interface ICommitMessageBuilder {

	void addChangedFile(String path, IFileContent oldContent, IFileContent newContent) throws IOException;

	void addDeletedFile(String path, IFileContent oldContent) throws IOException;

	void addAddedFile(String path, IFileContent newContent) throws IOException;

	String buildMessage() throws IOException;

}
