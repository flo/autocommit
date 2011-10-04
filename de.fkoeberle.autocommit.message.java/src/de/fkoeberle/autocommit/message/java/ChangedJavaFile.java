package de.fkoeberle.autocommit.message.java;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.IFileContent;

public class ChangedJavaFile extends ChangedFile {
	private final JavaFileContent oldJavaContent;
	private final JavaFileContent newJavaContent;

	public ChangedJavaFile(String path, IFileContent oldContent,
			IFileContent newContent) {
		super(path, oldContent, newContent);
		this.oldJavaContent = new JavaFileContent(oldContent);
		this.newJavaContent = new JavaFileContent(newContent);
	}

	public JavaFileContent getOldJavaContent() {
		return oldJavaContent;
	}

	public JavaFileContent getNewJavaContent() {
		return newJavaContent;
	}

}
