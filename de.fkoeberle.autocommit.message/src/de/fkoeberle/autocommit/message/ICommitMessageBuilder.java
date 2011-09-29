package de.fkoeberle.autocommit.message;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public interface ICommitMessageBuilder {

	void addChangedTextFile(IProject project, IPath path, String oldContent,
			String newContent);

	void addDeletedTextFile(IProject project, IPath path, String oldContent);

	void addAddedTextFile(IProject project, IPath path, String newContent);

	void addAddedBinaryFile(IProject project, IPath path);

	void addChangedBinaryFile(IProject project, IPath path);

	void addDeletedBinaryFile(IProject project, IPath path);

	String buildMessage();

}
