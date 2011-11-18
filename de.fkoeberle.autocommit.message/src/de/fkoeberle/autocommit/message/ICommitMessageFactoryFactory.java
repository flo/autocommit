package de.fkoeberle.autocommit.message;

public interface ICommitMessageFactoryFactory {

	ICommitMessageFactory createFactory(String id);
}
