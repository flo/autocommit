package de.fkoeberle.autocommit.message;

public interface ICommitMessageFactory {
	String build(FileSetDelta delta);
}
