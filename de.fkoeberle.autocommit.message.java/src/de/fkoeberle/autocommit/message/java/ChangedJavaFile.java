package de.fkoeberle.autocommit.message.java;

import de.fkoeberle.autocommit.message.IFileContent;

public class ChangedJavaFile {
	private final JavaFileContent oldJavaContent;
	private final JavaFileContent newJavaContent;

	public ChangedJavaFile(IFileContent oldContent, IFileContent newContent) {
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
