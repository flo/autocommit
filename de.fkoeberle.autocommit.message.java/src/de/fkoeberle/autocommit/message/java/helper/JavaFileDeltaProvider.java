/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.helper.delta.JavaFileDelta;

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
