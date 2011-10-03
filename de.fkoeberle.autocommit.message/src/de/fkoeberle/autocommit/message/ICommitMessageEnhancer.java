package de.fkoeberle.autocommit.message;

public interface ICommitMessageEnhancer {
	ICommitDescription enhance(ICommitDescription description);
}
