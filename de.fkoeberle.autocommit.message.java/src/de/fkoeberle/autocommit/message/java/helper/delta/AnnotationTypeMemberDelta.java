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

import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Type;

import de.fkoeberle.autocommit.message.java.helper.ASTCompareUtil;


public final class AnnotationTypeMemberDelta extends
		DeclarationDelta<AnnotationTypeMemberDeclaration> {

	/**
	 * 
	 * @param oldDeclaration
	 *            must have the same name as newDeclaration.
	 * @param newDeclaration
	 *            must have the same name as oldDeclaration.
	 */
	public AnnotationTypeMemberDelta(
			AnnotationTypeMemberDeclaration oldDeclaration,
			AnnotationTypeMemberDeclaration newDeclaration) {
		super(oldDeclaration, newDeclaration);
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		EnumSet<BodyDeclarationChangeType> result = EnumSet.noneOf(BodyDeclarationChangeType.class);
		if (containsDefaultChanges()) {
			result.add(BodyDeclarationChangeType.ANNOTATION_MEMBER_DEFAULT);
		}
		if (containsTypeChanges()) {
			result.add(BodyDeclarationChangeType.ANNOTATION_MEMBER_TYPE);
		}
		return result;
	}

	private boolean containsTypeChanges() {
		Type oldType = oldDeclaration.getType();
		Type newType = newDeclaration.getType();
		return ASTCompareUtil.astNodesDiffer(oldType, newType);
	}

	private boolean containsDefaultChanges() {
		Expression oldExpression = oldDeclaration.getDefault();
		Expression newExpression = newDeclaration.getDefault();
		return ASTCompareUtil.astNodesDiffer(oldExpression, newExpression);
	}

}
