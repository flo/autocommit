package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class AddedSectionCMF implements ICommitMessageFactory {

	@InjectedBySession
	private SingleAddedSectionView singleAddedSectionView;

	@InjectedAfterConstruction
	CommitMessageTemplate addedChapterMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedSectionMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedSubsectionMessage;

	@InjectedAfterConstruction
	CommitMessageTemplate addedSubsubsectionMessage;

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
