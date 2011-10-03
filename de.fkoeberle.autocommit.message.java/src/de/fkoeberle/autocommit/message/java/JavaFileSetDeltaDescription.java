package de.fkoeberle.autocommit.message.java;

import de.fkoeberle.autocommit.message.FileSetDeltaDescription;
import de.fkoeberle.autocommit.message.ICommitDescription;

public class JavaFileSetDeltaDescription implements ICommitDescription {
	private FileSetDeltaDescription fileSetDelta;

	public JavaFileSetDeltaDescription(FileSetDeltaDescription fileSetDelta) {
		this.fileSetDelta = fileSetDelta;
	}

	@Override
	public String buildMessage() {
		return "Worked on java files";
	}

	public FileSetDeltaDescription getFileSetDelta() {
		return fileSetDelta;
	}
}
