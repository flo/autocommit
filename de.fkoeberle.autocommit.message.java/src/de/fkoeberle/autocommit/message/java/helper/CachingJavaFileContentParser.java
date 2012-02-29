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
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.fkoeberle.autocommit.message.FileContentReader;
import de.fkoeberle.autocommit.message.IFileContent;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.SoftReferenceOrNull;

/**
 * 
 * This class offers a {@link #getInstanceFor(IFileContent)} method which can be
 * used to obtain a {@link CompilationUnit} instance for a {@link IFileContent}
 * instance. The object obtained this way should only be used for read only
 * purposes since it gets cached.
 * 
 * This class has fields annotated with {@link InjectedBySession}. Thus it
 * should be used as an field annotated with {@link InjectedBySession} which in
 * turn gets initialized by a {@link Session} object.
 * 
 */
public class CachingJavaFileContentParser {
	private final WeakHashMap<IFileContent, SoftReferenceOrNull<CompilationUnit>> cache;

	@InjectedBySession
	private FileContentReader reader;

	public CachingJavaFileContentParser() {
		this.cache = new WeakHashMap<IFileContent, SoftReferenceOrNull<CompilationUnit>>();
	}

	private static CompilationUnit createCompilationUnit(char[] fileContent) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileContent);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map<?, ?> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit;
	}

	/**
	 * 
	 * @param fileContent
	 *            a {@link IFileContent} object that represents a java file
	 * @return a {@link CompilationUnit} object that must not be modified.
	 */
	public CompilationUnit getInstanceFor(IFileContent fileContent)
			throws IOException {
		SoftReferenceOrNull<CompilationUnit> softReferenceOrNull = cache
				.get(fileContent);
		CompilationUnit compUnit = null;
		if (softReferenceOrNull != null) {
			SoftReference<CompilationUnit> softReference = softReferenceOrNull
					.getSoftReference();
			if (softReference == null) {
				return null;
			} else {
				compUnit = softReference.get();
			}

		}
		if (compUnit == null) {
			String s = reader.getStringFor(fileContent);
			char[] chars = s.toCharArray();
			compUnit = createCompilationUnit(chars);
			cache.put(fileContent, new SoftReferenceOrNull<CompilationUnit>(
					compUnit));
		}
		return compUnit;
	}

}
