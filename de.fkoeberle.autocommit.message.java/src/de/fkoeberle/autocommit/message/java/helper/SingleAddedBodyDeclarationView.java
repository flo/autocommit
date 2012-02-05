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

import org.eclipse.jdt.core.dom.BodyDeclaration;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.helper.delta.DeclarationListDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.TypeDelta;

public class SingleAddedBodyDeclarationView extends
		AbstractViewWithCache<BodyDeclaration> {

	@InjectedBySession
	private SingleChangedTypeView singleChangedTypeView;


	@Override
	protected BodyDeclaration determineCachableValue() throws IOException {
		TypeDelta typeDelta = singleChangedTypeView.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}

		if (!typeDelta.isDeclarationListOnlyChange()) {
			return null;
		}
		DeclarationListDelta declarationListDelta = typeDelta
				.getDeclarationListDelta();

		if (declarationListDelta.getAddedDeclarations().size() != 1) {
			return null;
		}

		if (declarationListDelta.getChangedDeclarations().size() != 0) {
			return null;
		}

		if (declarationListDelta.getRemovedDeclarations().size() != 0) {
			return null;
		}

		return declarationListDelta.getAddedDeclarations().get(0);
	}

	public BodyDeclaration getAddedDeclaration() throws IOException {
		return getCachableValue();
	}

}
