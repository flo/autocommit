/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class SingleChangedTypeView extends AbstractViewWithCache<TypeDelta> {

	@InjectedBySession
	private SingleChangedJavaFileView view;

	@Override
	protected TypeDelta determineCachableValue() throws IOException {
		JavaFileDelta javaFileDelta = view.getDelta();
		if (javaFileDelta == null) {
			return null;
		}
		DeclarationListDelta declarationListDelta = javaFileDelta
				.getDeclarationListDelta();
		return findTypeDeltaAtAnyDepth(declarationListDelta);
	}

	private TypeDelta findTypeDeltaAtAnyDepth(
			DeclarationListDelta declationListDelta) {
		if (declationListDelta.getAddedDeclarations().size() != 0) {
			return null;
		}
		if (declationListDelta.getRemovedDeclarations().size() != 0) {
			return null;
		}
		if (declationListDelta.getChangedDeclarations().size() != 1) {
			return null;
		}
		DeclarationDelta<?> declarationDelta = declationListDelta
				.getChangedDeclarations().get(0);
		if (!(declarationDelta instanceof TypeDelta)) {
			return null;
		}
		TypeDelta typeDelta = (TypeDelta) declarationDelta;

		if (typeDelta.isDeclarationListOnlyChange()) {
			DeclarationListDelta subDeclarationList = typeDelta
					.getDeclarationListDelta();
			TypeDelta subTypeDelta = findTypeDeltaAtAnyDepth(subDeclarationList);
			if (subTypeDelta != null) {
				return subTypeDelta;
			}
		}

		return typeDelta;
	}

	public TypeDelta getTypeDelta() throws IOException {
		return getCachableValue();
	}

}
