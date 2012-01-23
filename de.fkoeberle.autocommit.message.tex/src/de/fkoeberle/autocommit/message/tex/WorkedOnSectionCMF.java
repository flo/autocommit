/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnSectionCMF implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnChapterMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnSectionMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnSubsectionMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOnSubsubsectionMessage;

	@InjectedBySession
	private SingleChangedSectionView singleChangedHeadlineView;

	@Override
	public String createMessage() throws IOException {
		OutlineNodeDelta delta = singleChangedHeadlineView.getDelta();
		if (delta == null) {
			return null;
		}
		OutlineNodeType oldType = delta.getNewOutlineNode().getType();
		OutlineNodeType newType = delta.getOldOutlineNode().getType();
		if (oldType != newType) {
			return null;
		}
		String oldCaption = delta.getNewOutlineNode().getCaption();
		String newCaption = delta.getNewOutlineNode().getCaption();
		if (oldCaption != newCaption) {
			return null;
		}
		switch (oldType) {
		case CHAPTER:
			return workedOnChapterMessage.createMessageWithArgs(oldCaption);
		case SECTION:
			return workedOnSectionMessage.createMessageWithArgs(oldCaption);
		case SUBSECTION:
			return workedOnSubsectionMessage.createMessageWithArgs(oldCaption);
		case SUBSUBSECTION:
			return workedOnSubsubsectionMessage
					.createMessageWithArgs(oldCaption);
		default:
			return null;
		}
	}

}
