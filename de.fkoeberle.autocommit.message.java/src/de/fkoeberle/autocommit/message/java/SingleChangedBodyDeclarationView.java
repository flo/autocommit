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

public class SingleChangedBodyDeclarationView extends
		AbstractViewWithCache<DeclarationDelta<?>> {

	@InjectedBySession
	private SingleChangedTypeView singleChangedTypeView;

	@Override
	protected DeclarationDelta<?> determineCachableValue() throws IOException {
		TypeDelta typeDelta = singleChangedTypeView.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		if (!typeDelta.isDeclarationListOnlyChange()) {
			return null;
		}
		DeclarationListDelta declarationListDelta = typeDelta
				.getDeclarationListDelta();
		if (declarationListDelta.getAddedDeclarations().size() != 0) {
			return null;
		}
		if (declarationListDelta.getRemovedDeclarations().size() != 0) {
			return null;
		}
		if (declarationListDelta.getChangedDeclarations().size() != 1) {
			return null;
		}
		return declarationListDelta.getChangedDeclarations().get(0);
	}

	/**
	 * 
	 * @return an instance of {@link MethodDelta} or null if there was not
	 *         (only) a method change.
	 */
	public MethodDelta getMethodDelta() throws IOException {
		DeclarationDelta<?> delta = getCachableValue();
		if (delta instanceof MethodDelta) {
			return (MethodDelta) delta;
		} else {
			return null;
		}
	}

	public DeclarationDelta<?> getDelta() throws IOException {
		return getCachableValue();
	}

}
