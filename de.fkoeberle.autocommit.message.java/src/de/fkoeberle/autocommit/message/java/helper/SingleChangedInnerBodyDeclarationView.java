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
import de.fkoeberle.autocommit.message.java.helper.delta.MethodDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.TypeDelta;

/**
 * This is a helper class to determine if only one declaration has changed. It
 * should be used as an field annotated with {@link InjectedBySession} which in
 * turn gets initialized by a {@link Session} object.
 * 
 */
public class SingleChangedInnerBodyDeclarationView extends
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

	/**
	 * 
	 * @return an {@link DeclarationDelta} instance if the only change was the
	 *         modification of that declaration. Otherwise this method returns
	 *         null.
	 */
	public DeclarationDelta<?> getDelta() throws IOException {
		return getCachableValue();
	}

}
