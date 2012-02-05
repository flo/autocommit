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
import de.fkoeberle.autocommit.message.java.SingleChangedTypeView;
import de.fkoeberle.autocommit.message.java.TypeDelta;

public class WorkedOnTypeCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnClassMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnInterfaceMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnEnumMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnAnnotationMessage;

	@InjectedBySession
	private SingleChangedTypeView view;

	@Override
	public String createMessage() throws IOException {
		TypeDelta typeDelta = view.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		CommitMessageTemplate messageTemplate = getMessageTemplateFor(typeDelta);
		String simpleName = typeDelta.getSimpleTypeName();
		String fullName = typeDelta.getFullTypeName();
		return messageTemplate.createMessageWithArgs(simpleName, fullName);
	}

	CommitMessageTemplate getMessageTemplateFor(TypeDelta typeDelta) {
		switch (typeDelta.getType()) {
		case CLASS:
			return workedOnClassMessage;
		case INTERFACE:
			return workedOnInterfaceMessage;
		case ENUM:
			return workedOnEnumMessage;
		case ANNOTATION:
			return workedOnAnnotationMessage;
		default:
			throw new RuntimeException("Unhandled type");
		}
	}
}
