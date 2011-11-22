package de.fkoeberle.autocommit.message.refactor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class RefactoringCommentCMF implements ICommitMessageFactory {

	@CommitMessage
	public final CommitMessageTemplate message = new CommitMessageTemplate(
			"{1}");

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
