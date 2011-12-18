package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedAfterConstruction;

public class DummyCommitMessageFactory0 implements ICommitMessageFactory {

	@InjectedAfterConstruction
	CommitMessageTemplate message;

	@Override
	public String createMessage() throws IOException {
		return message.createMessageWithArgs();
	}

}
