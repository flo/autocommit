/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.refactor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class RefactoringCommentCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate message;

	@InjectedBySession
	private RefactoringDescriptorContainer refactoringDescriptorContainer;

	@Override
	public String createMessage() throws IOException {
		RefactoringDescriptor descriptor = refactoringDescriptorContainer
				.getRefactoringDescriptor();
		if (descriptor == null) {
			return null;
		}
		String comment = descriptor.getComment();
		if (comment.equals("")) {
			return null;
		}
		StringReader stringReader = new StringReader(comment);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		String firstCommentLine = bufferedReader.readLine();
		return message.createMessageWithArgs(comment, firstCommentLine);
	}
}
