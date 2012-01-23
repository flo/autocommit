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
import java.util.EnumSet;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class DocumentedMethodCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate documentedMethodMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate documentedConstructorMessage;

	@InjectedBySession
	SingleChangedBodyDeclarationView singleChangedMethodView;

	@Override
	public String createMessage() throws IOException {
		MethodDelta methodDelta = singleChangedMethodView.getMethodDelta();
		if (methodDelta == null) {
			return null;
		}
		if (!methodDelta.getChangeTypes().equals(
				EnumSet.of(BodyDeclarationChangeType.JAVADOC))) {
			return null;
		}
		CommitMessageTemplate message;
		if (methodDelta.getNewDeclaration().isConstructor()) {
			message = documentedConstructorMessage;
		} else {
			message = documentedMethodMessage;
		}
		String fullTypeName = methodDelta.getFullTypeName();
		String methodName = methodDelta.getMethodName();
		String parameterTypes = methodDelta.getParameterTypes();
		String typeName = methodDelta.getSimpleTypeName();
		return message.createMessageWithArgs(fullTypeName, methodName,
				parameterTypes, typeName);
	}
}
