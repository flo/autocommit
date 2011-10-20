package de.fkoeberle.autocommit.message;



public final class WorkedOnPathCMF implements ICommitMessageFactory {
	StringBuilder stringBuilder = new StringBuilder();

	@InjectedBySession
	private FileSetDelta delta;

	@CommitMessage
	public CommitMessageTemplate workedOn = new CommitMessageTemplate(
			Translations.WorkedOnPathCMF_workedOn);


	@Override
	public String createMessage() {
		String prefix = findCommonPrefix();
		return workedOn.createMessageWithArgs(prefix);
	}

	private String findCommonPrefix() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		for (ChangedFile file : delta.getChangedFiles()) {
			finder.checkPath(file.getPath());
		}
		for (AddedFile file : delta.getAddedFiles()) {
			finder.checkPath(file.getPath());
		}
		for (RemovedFile file : delta.getRemovedFiles()) {
			finder.checkPath(file.getPath());
		}
		return finder.getCommonPath();
	}


}