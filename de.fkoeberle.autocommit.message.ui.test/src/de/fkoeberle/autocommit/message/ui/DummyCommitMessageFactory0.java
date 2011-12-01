package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;

public class DummyCommitMessageFactory0 implements ICommitMessageFactory {

	public final CommitMessageTemplate message = new CommitMessageTemplate(
			"default");

	@Override
	public String createMessage() throws IOException {
		return message.createMessageWithArgs();
	}

}
