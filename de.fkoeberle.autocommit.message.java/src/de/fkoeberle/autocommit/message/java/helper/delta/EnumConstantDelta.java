/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper.delta;

import static de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType.ENUM_CONSTANTS_ARGUMENTS;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jdt.core.dom.EnumConstantDeclaration;

import de.fkoeberle.autocommit.message.java.helper.ASTCompareUtil;

/**
 * This class represents the result of comparison between an old and new version
 * of a {@link EnumConstantDeclaration}.
 * 
 * When the arguments of the enum constant have changed then the set returned by
 * the method {@link #getChangeTypes()} will contain an instance of
 * {@link BodyDeclarationChangeType#ENUM_CONSTANTS_ARGUMENTS}.
 * 
 */
public final class EnumConstantDelta extends
		DeclarationDelta<EnumConstantDeclaration> {

	public EnumConstantDelta(EnumConstantDeclaration oldDeclaration,
			EnumConstantDeclaration newDeclaration) {
		super(oldDeclaration, newDeclaration);
	}

	private boolean containsArgumentChanges() {
		List<?> oldArguments = oldDeclaration.arguments();
		List<?> newArguments = newDeclaration.modifiers();
		return ASTCompareUtil.listsOfASTNodesDiffer(oldArguments, newArguments);
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		EnumSet<BodyDeclarationChangeType> changes = EnumSet
				.noneOf(BodyDeclarationChangeType.class);
		if (containsArgumentChanges()) {
			changes.add(ENUM_CONSTANTS_ARGUMENTS);
		}
		return changes;
	}

}
