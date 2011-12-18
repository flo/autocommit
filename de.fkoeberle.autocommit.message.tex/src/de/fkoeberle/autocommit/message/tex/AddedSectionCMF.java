package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class AddedSectionCMF implements ICommitMessageFactory {

	@InjectedBySession
	private SingleAddedSectionView singleAddedSectionView;

	public final CommitMessageTemplate addedChapterMessage = new CommitMessageTemplate(
			"Added chapter {0}");
	public final CommitMessageTemplate addedSectionMessage = new CommitMessageTemplate(
			"Added section {0}");
	public final CommitMessageTemplate addedSubsectionMessage = new CommitMessageTemplate(
			"Added subsection {0}");
	public final CommitMessageTemplate addedSubsubsectionMessage = new CommitMessageTemplate(
			"Added subsubsection {0} ");

	@Override
	public String createMessage() throws IOException {
		AddedSectionInfo addedSectionInfo = singleAddedSectionView
				.getAddedSectionInfo();
		if (addedSectionInfo == null) {
			return null;
		}
		OutlineNode addedSection = addedSectionInfo.getAddedSection();

		if (addedSectionInfo.getCharactersAddedBefore().trim().length() != 0) {
			return null;
		}
		if (addedSectionInfo.getCharactersRemoved().trim().length() != 0) {
			return null;
		}
		String addedSectionName = addedSection.getCaption();

		CommitMessageTemplate message;

		switch (addedSection.getType()) {
		case CHAPTER:
			message = addedChapterMessage;
			break;
		case SECTION:
			message = addedSectionMessage;
			break;
		case SUBSECTION:
			message = addedSubsectionMessage;
			break;
		case SUBSUBSECTION:
			message = addedSubsubsectionMessage;
			break;
		default:
			return null;
		}
		return message.createMessageWithArgs(addedSectionName);
	}
}
