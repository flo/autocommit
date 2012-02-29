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

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.helper.delta.DeclarationDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.DeclarationListDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.JavaFileDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.TypeDelta;

/**
 * This is a helper class to determine if only one type has changed. It should
 * be used as an field annotated with {@link InjectedBySession} which in turn
 * gets initialized by a {@link Session} object.
 * 
 */
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

	/**
	 * 
	 * @return the changed type or null, if that were all changes. If there is
	 *         no single changed type this method return null.
	 */
	public TypeDelta getTypeDelta() throws IOException {
		return getCachableValue();
	}

}
