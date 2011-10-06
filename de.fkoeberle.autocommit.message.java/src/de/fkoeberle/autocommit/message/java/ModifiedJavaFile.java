package de.fkoeberle.autocommit.message.java;

import de.fkoeberle.autocommit.message.ModifiedFile;

public class ModifiedJavaFile {
	private final JavaFileContent oldJavaContent;
	private final JavaFileContent newJavaContent;

	public ModifiedJavaFile(ModifiedFile modifiedFile) {
		this.oldJavaContent = new JavaFileContent(modifiedFile.getOldContent());
		this.newJavaContent = new JavaFileContent(modifiedFile.getNewContent());
	}

	public JavaFileContent getOldJavaContent() {
		return oldJavaContent;
	}

	public JavaFileContent getNewJavaContent() {
		return newJavaContent;
	}

}
