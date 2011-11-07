package de.fkoeberle.autocommit.message.refactor;

import java.io.IOException;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class RefactoringCommentCMF implements ICommitMessageFactory {

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
		return comment;
	}
}
