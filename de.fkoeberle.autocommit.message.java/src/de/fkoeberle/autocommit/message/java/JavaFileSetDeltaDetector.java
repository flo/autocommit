package de.fkoeberle.autocommit.message.java;

import java.util.Collections;
import java.util.Set;

import de.fkoeberle.autocommit.message.FileSetDeltaDescription;
import de.fkoeberle.autocommit.message.ICommitDescription;
import de.fkoeberle.autocommit.message.ICommitMessageEnhancer;

public class JavaFileSetDeltaDetector implements ICommitMessageEnhancer {

	public JavaFileSetDeltaDetector() {
	}

	@Override
	public ICommitDescription enhance(ICommitDescription description) {
		if (!(description instanceof FileSetDeltaDescription)) {
			return null;
		}
		FileSetDeltaDescription fileSetDelta = (FileSetDeltaDescription) description;
		Set<String> fileExtensions = fileSetDelta.getFileExtensions();
		if (!fileExtensions.equals(Collections.singleton("java"))) {
			return null;
		}
		
		return new JavaFileSetDeltaDescription(fileSetDelta);
	}

}
