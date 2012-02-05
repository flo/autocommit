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

import org.eclipse.jdt.core.dom.Initializer;

public final class InitializerDelta extends DeclarationDelta<Initializer> {

	public InitializerDelta(Initializer oldDeclaration,
			Initializer newDeclaration) {
		super(oldDeclaration, newDeclaration);
	}

	@Override
	protected EnumSet<BodyDeclarationChangeType> determineOtherChangeTypes() {
		return EnumSet.noneOf(BodyDeclarationChangeType.class);
	}

}
