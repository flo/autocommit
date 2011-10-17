package de.fkoeberle.autocommit.message;



public final class WorkedOnPathCMF implements ICommitMessageFactory {
	StringBuilder stringBuilder = new StringBuilder();

	@CommitMessage
	public CommitMessageTemplate workedOn = new CommitMessageTemplate(
			Translations.WorkedOnPathCMF_workedOn);

	@Override
	public String createMessageFor(FileSetDelta delta, Session session) {
		String prefix = findCommonPrefix(delta);
		return workedOn.createMessageWithArgs(prefix);
	}

	private String findCommonPrefix(FileSetDelta delta) {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		for (ModifiedFile file : delta.getChangedFiles()) {
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