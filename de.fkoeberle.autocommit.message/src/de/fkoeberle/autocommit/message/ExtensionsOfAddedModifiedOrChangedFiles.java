/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ExtensionsOfAddedModifiedOrChangedFiles extends
		AbstractViewWithCache<Set<String>> {
	@InjectedBySession
	private FileSetDelta fileSetDelta;

	private static String fileExtensionOf(String path) {
		int lastDot = path.lastIndexOf(".");
		int lastSlash = path.lastIndexOf("/");
		if (lastDot == -1) {
			return "";
		}
		if ((lastSlash != -1) && (lastSlash > lastDot)) {
			return "";
		}
		return path.substring(lastDot + 1, path.length());
	}

	@Override
	protected Set<String> determineCachableValue() throws IOException {
		Set<String> fileExtensions = new HashSet<String>();
		for (ChangedFile file : fileSetDelta.getChangedFiles()) {
			fileExtensions.add(fileExtensionOf(file.getPath()));
		}
		for (AddedFile file : fileSetDelta.getAddedFiles()) {
			fileExtensions.add(fileExtensionOf(file.getPath()));
		}
		for (RemovedFile file : fileSetDelta.getRemovedFiles()) {
			fileExtensions.add(fileExtensionOf(file.getPath()));
		}
		return fileExtensions;
	}

	public Set<String> getFileExtensions() throws IOException {
		return getCachableValue();
	}

	/**
	 * 
	 * @param fileExtension
	 *            extension of the file without the dot.
	 * @return true if there are only changed, modified or added files with the
	 *         specified file extension.
	 */
	public boolean containsOnly(String fileExtension) throws IOException {
		return getFileExtensions().size() == 1
				&& getFileExtensions().contains(fileExtension);
	}
}
