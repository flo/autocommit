/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

public class CommonParentDirectoryFinder {
	private String firstPath;
	private int lengthOfDirectoryPath;

	public void handleFilePath(String filePath) {
		if (firstPath == null) {
			firstPath = filePath;
			// The case that there is no "/" results in 0 which is okay
			lengthOfDirectoryPath = firstPath.lastIndexOf('/') + 1;
		} else {
			for (int i = 0; i < lengthOfDirectoryPath; i++) {
				if ((i >= filePath.length())) {
					lengthOfDirectoryPath = firstPath.lastIndexOf('/', i) + 1;
					return;
				} else if ((firstPath.charAt(i) != filePath.charAt(i))) {
					lengthOfDirectoryPath = firstPath.lastIndexOf('/', i - 1) + 1;
					return;
				}
			}
			if (lengthOfDirectoryPath < filePath.length()) {
				lengthOfDirectoryPath = filePath.lastIndexOf('/',
						lengthOfDirectoryPath) + 1;
			}
		}
	}

	/**
	 * 
	 * @return the common directory of the files passed to
	 *         {@link #handleFilePath(String)}. The return directory always ends
	 *         with "/". If there is no common directory "./" is returned.
	 */
	public String getCommonDirectory() {
		if ((firstPath == null) || (lengthOfDirectoryPath == -1))
			return null;
		if (lengthOfDirectoryPath == 0)
			return "./";
		return firstPath.substring(0, lengthOfDirectoryPath);
	}
}
