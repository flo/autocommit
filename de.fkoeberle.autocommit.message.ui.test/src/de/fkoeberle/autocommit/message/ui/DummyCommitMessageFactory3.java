package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;

import de.fkoeberle.autocommit.message.ICommitMessageFactory;

public class DummyCommitMessageFactory3 implements ICommitMessageFactory {

	@Override
	public String createMessage() throws IOException {
		return null;
	}

}
