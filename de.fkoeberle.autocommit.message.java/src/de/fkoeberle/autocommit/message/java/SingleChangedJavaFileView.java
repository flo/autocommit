package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.SingleChangedFileView;

public class SingleChangedJavaFileView extends
		AbstractViewWithCache<JavaFileDelta> {
	@InjectedBySession
	private JavaFileDeltaProvider javaFileDeltaProvider;

	@InjectedBySession
	private SingleChangedFileView singleChangedFileView;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;

	@Override
	protected JavaFileDelta determineCachableValue() throws IOException {
		if (!extensions.containsOnly("java")) {
			return null;
		}
		ChangedFile changedFile = singleChangedFileView.getChangedFile();
		if (changedFile == null) {
			return null;
		}
		return javaFileDeltaProvider.getDeltaFor(changedFile);
	}

	public JavaFileDelta getDelta() throws IOException {
		return getCachableValue();
	}
}
