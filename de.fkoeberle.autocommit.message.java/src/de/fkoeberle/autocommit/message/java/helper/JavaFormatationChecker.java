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
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.java.helper.delta.JavaFileDelta;

public class JavaFormatationChecker {
	private SoftReference<Map<JavaFileDelta, Boolean>> cache;

	/**
	 * 
	 * @param changedFile
	 * @return true if it can be guaranteed that there were only formation
	 *         changes and false otherwise.
	 * @throws IOException
	 */
	public boolean foundJavaFormatationChangesOnly(JavaFileDelta javaFileDelta)
			throws IOException {

		Map<JavaFileDelta, Boolean> map = null;
		if (cache != null) {
			map = cache.get();
		}
		if (map == null) {
			map = new HashMap<JavaFileDelta, Boolean>();
			cache = new SoftReference<Map<JavaFileDelta, Boolean>>(map);
		}
		Boolean result = map.get(javaFileDelta);
		if (result == null) {
			CompilationUnit oldContent = javaFileDelta.getOldDeclaration();
			CompilationUnit newContent = javaFileDelta.getNewDeclaration();

			if (containsProblems(oldContent)) {
				return false;
			}
			if (containsProblems(newContent)) {
				return false;
			}
			boolean match = oldContent.subtreeMatch(new ASTMatcher(true),
					newContent);
			result = Boolean.valueOf(match);
			map.put(javaFileDelta, result);
		}

		return result.booleanValue();
	}

	private static boolean containsProblems(CompilationUnit compUnit) {
		return compUnit.getProblems().length != 0;
	}
}
