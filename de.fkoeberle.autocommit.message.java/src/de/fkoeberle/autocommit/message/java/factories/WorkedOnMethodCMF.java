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

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.helper.SingleChangedBodyDeclarationView;
import de.fkoeberle.autocommit.message.java.helper.delta.MethodDelta;

public class WorkedOnMethodCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnMethodMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnConstructorMessage;

	@InjectedBySession
	SingleChangedBodyDeclarationView singleChangedMethodView;

	@Override
	public String createMessage() throws IOException {
		MethodDelta methodDelta = singleChangedMethodView.getMethodDelta();
		if (methodDelta == null) {
			return null;
		}

		String fullTypeName = methodDelta.getFullTypeName();
		String methodName = methodDelta.getMethodName();
		String parameterTypes = methodDelta.getParameterTypes();
		String typeName = methodDelta.getSimpleTypeName();
		CommitMessageTemplate messageTemplate;
		if (methodDelta.getOldDeclaration().isConstructor()) {
			messageTemplate = workedOnConstructorMessage;
		} else {
			messageTemplate = workedOnMethodMessage;
		}
		return messageTemplate.createMessageWithArgs(fullTypeName, methodName,
				parameterTypes, typeName);
	}

}
