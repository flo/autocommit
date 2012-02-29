/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper.delta;

import static de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType.FIELD_TYPE;

import java.util.EnumSet;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Type;

/**
 * This class represents the result of comparison between an old and new version
 * of a {@link FieldDeclaration}.
 * 
 * When the field type has changed then the set returned by the method
 * {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#FIELD_TYPE}.
 * 
 */
public final class FieldDelta extends DeclarationDelta<FieldDeclaration> {

	/**
	 * 
	 * @param oldDeclaration
	 *            the old declaration which must have the same fragments as the
	 *            new declaration.
	 * @param newDeclaration
	 *            the new declaration which must have the same fragments as the
	 *            old declaration.
	 */
	public FieldDelta(FieldDeclaration oldDeclaration,
			FieldDeclaration newDeclaration) {
		super(oldDeclaration, newDeclaration);
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		EnumSet<BodyDeclarationChangeType> result = EnumSet
				.noneOf(BodyDeclarationChangeType.class);
		if (containsTypeChange()) {
			result.add(FIELD_TYPE);
		}
		return result;
	}

	private boolean containsTypeChange() {
		Type oldType = oldDeclaration.getType();
		Type newType = newDeclaration.getType();
		boolean sameType = oldType.subtreeMatch(new ASTMatcher(true), newType);
		return !sameType;
	}

}
