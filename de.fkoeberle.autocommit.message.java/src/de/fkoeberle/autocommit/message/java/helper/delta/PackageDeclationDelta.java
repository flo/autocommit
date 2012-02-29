/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper.delta;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import de.fkoeberle.autocommit.message.java.helper.ASTCompareUtil;

/**
 * This class represents the result of comparison between an old and new version
 * of a {@link PackageDeclaration}.
 * 
 * 
 * When the javadoc got changed then the set returned by the method
 * {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#JAVADOC}.
 * 
 * When the annotation of the package got changed then the set returned by the
 * method {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#PACKAGE_ANNOTATIONS}.
 * 
 * When the package name got changed then the set returned by the method
 * {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#PACKAGE_NAME}.
 * 
 */
public class PackageDeclationDelta implements IDelta {
	private final PackageDeclaration oldDeclaration;
	private final PackageDeclaration newDeclaration;
	private EnumSet<BodyDeclarationChangeType> changeTypes;

	public PackageDeclationDelta(PackageDeclaration oldDeclaration,
			PackageDeclaration newDeclaration) {
		this.oldDeclaration = oldDeclaration;
		this.newDeclaration = newDeclaration;
	}

	public PackageDeclaration getNewDeclaration() {
		return newDeclaration;
	}

	public PackageDeclaration getOldDeclaration() {
		return oldDeclaration;
	}

	@Override
	public EnumSet<BodyDeclarationChangeType> getChangeTypes() {
		if (changeTypes == null) {
			changeTypes = determineChangeTypes();
		}
		return changeTypes;
	}

	private EnumSet<BodyDeclarationChangeType> determineChangeTypes() {
		EnumSet<BodyDeclarationChangeType> changesFound = EnumSet
				.noneOf(BodyDeclarationChangeType.class);
		if ((oldDeclaration == null) != (newDeclaration == null)) {
			changesFound.add(BodyDeclarationChangeType.PACKAGE_EXISTANCE);
			return changesFound;
		}
		if (containsJavaDocChanges()) {
			changesFound.add(BodyDeclarationChangeType.JAVADOC);
		}
		if (containsAnnotationChanges()) {
			changesFound.add(BodyDeclarationChangeType.PACKAGE_ANNOTATIONS);
		}
		if (containsNameChanges()) {
			changesFound.add(BodyDeclarationChangeType.PACKAGE_NAME);
		}
		return changesFound;
	}

	private boolean containsJavaDocChanges() {
		final Javadoc oldJavaDoc = oldDeclaration.getJavadoc();
		final Javadoc newJavaDoc = newDeclaration.getJavadoc();
		return ASTCompareUtil.astNodesDiffer(oldJavaDoc, newJavaDoc);
	}

	private boolean containsAnnotationChanges() {
		final List<?> oldAnnotations = oldDeclaration.annotations();
		final List<?> newAnnotations = newDeclaration.annotations();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldAnnotations,
				newAnnotations);
	}

	private boolean containsNameChanges() {
		Name oldName = oldDeclaration.getName();
		Name newName = newDeclaration.getName();
		return ASTCompareUtil.astNodesDiffer(oldName, newName);
	}

}
