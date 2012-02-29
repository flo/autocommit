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
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.helper.delta.JavaFileDelta;

/**
 * An helper class with a method {@link #getDeltaFor(ChangedFile)}.
 * 
 * This class has a field annotated with {@link InjectedBySession}. Thus it
 * should be used as an field annotated with {@link InjectedBySession} which in
 * turn gets initialized by a {@link Session} object.
 * 
 */
public class JavaFileDeltaProvider {
	private final Map<ChangedFile, SoftReference<JavaFileDelta>> changedFileToDeltaRefMap;

	@InjectedBySession
	private CachingJavaFileContentParser parser;

	public JavaFileDeltaProvider() {
		this.changedFileToDeltaRefMap = new HashMap<ChangedFile, SoftReference<JavaFileDelta>>();
	}

	/**
	 * 
	 * @param changedFile
	 *            the {@link ChangedFile} object which should be converted into
	 *            a {@link Java FileDelta} object.
	 * @return a {@link JavaFileDelta} instance which represents old and new
	 *         syntax tree of the specified java file. The result must not be
	 *         modified.
	 */
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
