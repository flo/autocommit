package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class FormattedJavaFileCMF implements ICommitMessageFactory {
	public final CommitMessageTemplate formattedJavaFileMessage = new CommitMessageTemplate(
			Translations.FormattedJavaFileCMF_formattedJavaFileMessage);

	@InjectedBySession
	private SingleChangedJavaFileView singleChangedJavaFileView;

	@InjectedBySession
	private JavaFormatationChecker formatationChecker;

	@Override
	public String createMessage() {
		ChangedFile changedFile = singleChangedJavaFileView.getChangedFile();
		if (changedFile == null) {
			return null;
		}
		try {
			if (!formatationChecker
					.foundJavaFormatationChangesOnly(changedFile)) {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return formattedJavaFileMessage.createMessageWithArgs(changedFile
				.getPath());
	}
}
