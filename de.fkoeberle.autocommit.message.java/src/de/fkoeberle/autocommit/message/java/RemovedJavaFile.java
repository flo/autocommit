package de.fkoeberle.autocommit.message.java;

import de.fkoeberle.autocommit.message.RemovedFile;

public class RemovedJavaFile {
	private final JavaFileContent oldJavaContent;

	public RemovedJavaFile(RemovedFile removedFile) {
		this.oldJavaContent = new JavaFileContent(removedFile.getOldContent());
	}

	public JavaFileContent getOldJavaContent() {
		return oldJavaContent;
	}

}
