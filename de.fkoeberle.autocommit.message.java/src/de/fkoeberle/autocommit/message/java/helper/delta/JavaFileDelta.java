/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper.delta;

import static de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType.DECLARATION_LIST;
import static de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType.IMPORTS;
import static de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType.PACKAGE;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.java.helper.ASTCompareUtil;
import de.fkoeberle.autocommit.message.java.helper.CachingJavaFileContentParser;

/**
 * This class represents the result of comparison between an old and new version
 * of an java file.
 * 
 * When the import list got changed then the set returned by the method
 * {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#IMPORTS}. When the list of declared types
 * got changed then {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#DECLARATION_LIST}. When package declaration
 * got added, removed or changed then {@link #getChangeTypes()} will contain an
 * instance of {@link BodyDeclarationChangeType#PACKAGE}.
 * 
 */
public class JavaFileDelta implements IDelta {
	private final ChangedFile changedFile;
	private SoftReference<CompilationUnit> oldCompilationUnitRef;
	private SoftReference<CompilationUnit> newCompilationUnitRef;
	private SoftReference<DeclarationListDelta> declarationListDeltaRef;
	private PackageDeclationDelta packageDeclationDelta;
	private EnumSet<BodyDeclarationChangeType> changeTypes;
	private final CachingJavaFileContentParser parser;

	public JavaFileDelta(ChangedFile changedFile,
			CachingJavaFileContentParser parser) {
		this.changedFile = changedFile;
		this.parser = parser;
	}

	@Override
	public EnumSet<BodyDeclarationChangeType> getChangeTypes()
			throws IOException {
		if (changeTypes == null) {
			changeTypes = determineChangeTypes();
		}
		return changeTypes;
	}

	public EnumSet<BodyDeclarationChangeType> determineChangeTypes()
			throws IOException {
		EnumSet<BodyDeclarationChangeType> changesFound = EnumSet
				.noneOf(BodyDeclarationChangeType.class);
		if (containsImportChanges()) {
			changesFound.add(IMPORTS);
		}
		if (containsPackageChanges()) {
			changesFound.add(PACKAGE);
		}
		if (containsTypeDeclarationListChanges()) {
			changesFound.add(DECLARATION_LIST);
		}
		return changesFound;
	}

	private boolean containsImportChanges() throws IOException {
		final List<?> oldImports = getOldDeclaration().imports();
		final List<?> newImports = getNewDeclaration().imports();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldImports, newImports);
	}

	private boolean containsPackageChanges() throws IOException {
		PackageDeclaration oldPackage = getOldDeclaration().getPackage();
		PackageDeclaration newPackage = getNewDeclaration().getPackage();
		return ASTCompareUtil.astNodesDiffer(oldPackage, newPackage);
	}

	private boolean containsTypeDeclarationListChanges() throws IOException {
		List<?> oldTypes = getOldDeclaration().types();
		List<?> newTypes = getNewDeclaration().types();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldTypes, newTypes);
	}

	/**
	 * 
	 * @return the compilation unit of the new version of the file or null if
	 *         {@link #isValid(FileSetDelta)} returns false.
	 * @throws IOException
	 *             if the compilation unit got parsed and an IOException
	 *             occured.
	 */
	public CompilationUnit getNewDeclaration() throws IOException {
		CompilationUnit compilationUnit = null;
		if (newCompilationUnitRef != null) {
			compilationUnit = newCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			compilationUnit = parser
					.getInstanceFor(changedFile.getNewContent());
			newCompilationUnitRef = new SoftReference<CompilationUnit>(
					compilationUnit);
		}
		return compilationUnit;
	}

	/**
	 * 
	 * @return the compilation unit of the new version of the file or null if
	 *         {@link #isValid(FileSetDelta)} returns false.
	 * @throws IOException
	 *             if the compilation unit got parsed and an IOException
	 *             occurred.
	 */
	public CompilationUnit getOldDeclaration() throws IOException {
		CompilationUnit compilationUnit = null;
		if (oldCompilationUnitRef != null) {
			compilationUnit = oldCompilationUnitRef.get();
		}
		if (compilationUnit == null) {
			compilationUnit = parser
					.getInstanceFor(changedFile.getOldContent());
			oldCompilationUnitRef = new SoftReference<CompilationUnit>(
					compilationUnit);
		}
		return compilationUnit;
	}

	/**
	 * 
	 * @return an instance of {@link DeclarationListDelta} or null if
	 *         {@link #isValid()} returns false.
	 * @throws IOException
	 */
	public DeclarationListDelta getDeclarationListDelta() throws IOException {
		DeclarationListDelta declarationListDelta = null;
		if (declarationListDeltaRef != null) {
			declarationListDelta = declarationListDeltaRef.get();
		}
		if (declarationListDelta == null) {
			CompilationUnit oldCompilationUnit = getOldDeclaration();
			CompilationUnit newCompilationUnit = getNewDeclaration();
			declarationListDelta = new DeclarationListDelta(oldCompilationUnit,
					newCompilationUnit);
			declarationListDeltaRef = new SoftReference<DeclarationListDelta>(
					declarationListDelta);

		}
		return declarationListDelta;
	}

	public PackageDeclationDelta getPackageDelta() throws IOException {
		if (packageDeclationDelta == null) {
			packageDeclationDelta = new PackageDeclationDelta(
					getOldDeclaration().getPackage(), getNewDeclaration()
							.getPackage());
		}
		return packageDeclationDelta;
	}

	public ChangedFile getChangedFile() {
		return changedFile;
	}

}
