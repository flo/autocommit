package de.fkoeberle.autocommit.message;

public interface ICommitMessageFactory {
	String createMessageFor(FileSetDelta delta, Session session);
}
