package de.fkoeberle.autocommit.message.java;

import de.fkoeberle.autocommit.message.AddedFile;

public class AddedJavaFile {
	private final JavaFileContent newJavaContent;

	public AddedJavaFile(AddedFile addedFile) {
		this.newJavaContent = new JavaFileContent(addedFile.getNewContent());
	}

	public JavaFileContent getNewJavaContent() {
		return newJavaContent;
	}

}
