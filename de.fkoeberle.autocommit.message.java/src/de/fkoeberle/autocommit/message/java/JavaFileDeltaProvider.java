package de.fkoeberle.autocommit.message.java;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class JavaFileDeltaProvider {
	private final Map<ChangedFile, SoftReference<JavaFileDelta>> changedFileToDeltaRefMap;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	public JavaFileDeltaProvider() {
		this.changedFileToDeltaRefMap = new HashMap<ChangedFile, SoftReference<JavaFileDelta>>();
	}

	public JavaFileDelta getDeltaFor(ChangedFile changedFile) {
		SoftReference<JavaFileDelta> ref = changedFileToDeltaRefMap
				.get(changedFile);
		JavaFileDelta delta = null;
		if (ref != null) {
			delta = ref.get();
		}
		if (delta == null) {
			delta = new JavaFileDelta(changedFile, parser);
			changedFileToDeltaRefMap.put(changedFile,
					new SoftReference<JavaFileDelta>(delta));
		}

		return delta;
	}

}
