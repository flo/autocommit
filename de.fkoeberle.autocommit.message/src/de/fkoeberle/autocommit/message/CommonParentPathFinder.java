/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

public class CommonParentPathFinder {
	private String firstPath;
	private int lengthOfPrefix;

	public void checkPath(String currentPath) {
		if (firstPath == null) {
			firstPath = currentPath;
			lengthOfPrefix = currentPath.length();
		} else {
			for (int i = 0; i < lengthOfPrefix; i++) {
				if ((i >= currentPath.length())) {
					lengthOfPrefix = firstPath.lastIndexOf('/', i) + 1;
					return;
				} else if ((firstPath.charAt(i) != currentPath.charAt(i))) {
					lengthOfPrefix = firstPath.lastIndexOf('/', i - 1) + 1;
					return;
				}
			}
			if (lengthOfPrefix < currentPath.length()) {
				lengthOfPrefix = currentPath.lastIndexOf('/', lengthOfPrefix) + 1;
			}
		}
	}

	public String getCommonPath() {
		if ((firstPath == null) || (lengthOfPrefix == -1))
			return null;
		if (lengthOfPrefix == 0)
			return ".";
		return firstPath.substring(0, lengthOfPrefix);
	}
}
