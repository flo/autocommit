/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper;

import java.util.Set;

/**
 * One time use helper class to find in a collection of packages the parent
 * package. The pacakges needs to be passed by calling
 * {@link #checkPackage(String)} or {@link #checkPackages(Set)}. Once all
 * packages got passed, the method {@link #getCommonPackage()} can be called to
 * obtain the common parent package.
 * 
 * 
 */
public class CommonParentPackageFinder {
	private String first;
	private int lengthOfPrefix;

	public void checkPackage(String p) {
		if (first == null) {
			first = p;
			lengthOfPrefix = p.length();
		} else {
			// default package handling:
			if ((lengthOfPrefix == 0)) {
				if (p.length() != 0) {
					lengthOfPrefix = -1;
				}
				return;
			}
			for (int i = 0; i < lengthOfPrefix; i++) {
				if ((i >= p.length())) {
					lengthOfPrefix = first.lastIndexOf('.', i);
					return;
				} else if ((first.charAt(i) != p.charAt(i))) {
					lengthOfPrefix = first.lastIndexOf('.', i - 1);
					return;
				}
			}
			if (lengthOfPrefix < p.length()) {
				lengthOfPrefix = p.lastIndexOf('.', lengthOfPrefix);
			}
		}
	}

	/**
	 * 
	 * @return the common parent package or null.
	 */
	public String getCommonPackage() {
		if ((first == null) || (lengthOfPrefix == -1))
			return null;
		return first.substring(0, lengthOfPrefix);
	}

	public void checkPackages(Set<String> packages) {
		for (String p : packages) {
			checkPackage(p);
		}
	}
}
