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
import de.fkoeberle.autocommit.message.java.helper.JavaDocSearchResult;
import de.fkoeberle.autocommit.message.java.helper.JavaDocSearchUtility;
import de.fkoeberle.autocommit.message.java.helper.SingleChangedTypeView;
import de.fkoeberle.autocommit.message.java.helper.delta.TypeDelta;

public class DocumentedTypeCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate documentedClassMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate documentedInterfaceMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate documentedEnumMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate documentedAnnotationMessage;

	@InjectedBySession
	private SingleChangedTypeView singleChangedTypeView;

	@InjectedBySession
	private JavaDocSearchUtility javaDocSearch;

	@Override
	public String createMessage() throws IOException {
		TypeDelta typeDelta = singleChangedTypeView.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		JavaDocSearchResult searchResult = javaDocSearch.search(typeDelta);
		if (searchResult != JavaDocSearchResult.GOT_ADDED_OR_MODIFIED_ONLY) {
			return null;
		}

		switch (typeDelta.getType()) {
		case CLASS:
			return documentedClassMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		case INTERFACE:
			return documentedInterfaceMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		case ENUM:
			return documentedEnumMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		case ANNOTATION:
			return documentedAnnotationMessage.createMessageWithArgs(typeDelta
					.getFullTypeName());
		default:
			return null;
		}
	}

}
