package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnSectionCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate workedOnChapterMessage = new CommitMessageTemplate(
			Translations.WorkedOnHeadlineCMF_workedOnChapter);
	public final CommitMessageTemplate workedOnSectionMessage = new CommitMessageTemplate(
			Translations.WorkedOnHeadlineCMF_workedOnSection);
	public final CommitMessageTemplate workedOnSubsectionMessage = new CommitMessageTemplate(
			Translations.WorkedOnHeadlineCMF_workedOnSubsection);
	public final CommitMessageTemplate workedOnSubsubsectionMessage = new CommitMessageTemplate(
			Translations.WorkedOnHeadlineCMF_workedOnSubsubsection);

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
