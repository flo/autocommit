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

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.java.JavaFileDelta;
import de.fkoeberle.autocommit.message.java.JavaFormatationChecker;
import de.fkoeberle.autocommit.message.java.SingleChangedJavaFileView;

public class FormattedJavaFileCMF implements ICommitMessageFactory {
	@InjectedAfterConstruction
	CommitMessageTemplate formattedJavaFileMessage;

	@InjectedBySession
	private SingleChangedJavaFileView singleChangedJavaFileView;

	@InjectedBySession
	private JavaFormatationChecker formatationChecker;

	@Override
	public String createMessage() throws IOException {
		JavaFileDelta javaFileDelta = singleChangedJavaFileView.getDelta();
		if (javaFileDelta == null) {
			return null;
		}

		ChangedFile changedFile = javaFileDelta.getChangedFile();

		if (!formatationChecker.foundJavaFormatationChangesOnly(javaFileDelta)) {
			return null;
		}

		return formattedJavaFileMessage.createMessageWithArgs(changedFile
				.getPath());
	}
}
