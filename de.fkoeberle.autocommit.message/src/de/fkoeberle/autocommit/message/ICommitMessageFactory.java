package de.fkoeberle.autocommit.message;

import java.io.IOException;

public interface ICommitMessageFactory {
	String createMessage() throws IOException;
}
