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
