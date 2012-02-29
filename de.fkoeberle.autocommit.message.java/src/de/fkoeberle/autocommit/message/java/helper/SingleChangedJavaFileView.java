/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper;

import java.io.IOException;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.SingleChangedFileView;
import de.fkoeberle.autocommit.message.java.helper.delta.JavaFileDelta;

/**
 * This is a helper class to determine if only one java file has changed. It
 * should be used as an field annotated with {@link InjectedBySession} which in
 * turn gets initialized by a {@link Session} object.
 * 
 */
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

	/**
	 * 
	 * @return the changed java file or null if there was not just this single
	 *         change.
	 */
	public JavaFileDelta getDelta() throws IOException {
		return getCachableValue();
	}
}
