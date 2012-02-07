/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.factories;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.helper.SingleAddedInnerBodyDeclarationView;
import de.fkoeberle.autocommit.message.java.helper.TypeUtil;

public class AddedMethodCMF implements ICommitMessageFactory {
	@InjectedAfterConstruction
	CommitMessageTemplate addedMethodMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedConstructorMessage;

	@InjectedBySession
	private SingleAddedInnerBodyDeclarationView singleAddedBodyDeclarationView;

	@Override
	public String createMessage() throws IOException {
		BodyDeclaration addedDeclaration = singleAddedBodyDeclarationView
				.getAddedDeclaration();
		if (!(addedDeclaration instanceof MethodDeclaration)) {
			return null;
		}
		MethodDeclaration addedMethod = (MethodDeclaration) addedDeclaration;

		AbstractTypeDeclaration type = (AbstractTypeDeclaration) (addedMethod
				.getParent());

		String fullTypeName = TypeUtil.fullTypeNameOf(type);
		String methodName = TypeUtil.nameOfMethod(addedMethod);
		String parameterTypes = TypeUtil.parameterTypesOf(addedMethod);
		String typeName = TypeUtil.nameOf(type);
		CommitMessageTemplate message;
		if (addedMethod.isConstructor()) {
			message = addedConstructorMessage;
		} else {

			message = addedMethodMessage;
		}

		return message.createMessageWithArgs(fullTypeName, methodName,
				parameterTypes, typeName);
	}

}
