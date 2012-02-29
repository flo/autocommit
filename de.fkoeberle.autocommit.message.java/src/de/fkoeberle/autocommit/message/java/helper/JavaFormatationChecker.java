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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.helper.delta.JavaFileDelta;

/**
 * A helper class which offers a method
 * {@link #foundJavaFormatationChangesOnly(JavaFileDelta)}. It should be used as
 * an field annotated with {@link InjectedBySession} which in turn gets
 * initialized by a {@link Session} object.
 * 
 * 
 */
public class JavaFormatationChecker extends
		AbstractViewWithCache<Map<JavaFileDelta, Boolean>> {
	/**
	 * Checks for a given file if there are only whitespace changes.
	 * 
	 * @param changedFile
	 *            the file to check.
	 * @return true if it can be guaranteed that there were only formation
	 *         changes and false otherwise.
	 * @throws IOException
	 */
	public boolean foundJavaFormatationChangesOnly(JavaFileDelta javaFileDelta)
			throws IOException {
		Map<JavaFileDelta, Boolean> map = getCachableValue();
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

	@Override
	protected Map<JavaFileDelta, Boolean> determineCachableValue()
			throws IOException {
		return new HashMap<JavaFileDelta, Boolean>();
	}

	private static boolean containsProblems(CompilationUnit compUnit) {
		return compUnit.getProblems().length != 0;
	}
}
