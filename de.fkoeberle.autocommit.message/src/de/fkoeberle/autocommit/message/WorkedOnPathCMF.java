package de.fkoeberle.autocommit.message;

public final class WorkedOnPathCMF implements ICommitMessageFactory {

	@InjectedBySession
	private FileSetDelta delta;

	@InjectedAfterConstruction
	CommitMessageTemplate workedOn;

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