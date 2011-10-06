package de.fkoeberle.autocommit.message;




public final class ModifiedPathCMF implements
 ICommitMessageFactory {
	StringBuilder stringBuilder = new StringBuilder();

	@Override
	public String build(FileSetDelta delta) {
		return "Worked on " + findCommonPrefix(delta);
	}

	private String findCommonPrefix(FileSetDelta delta) {
		CommonPrefixFinder finder = new CommonPrefixFinder();
		for (ModifiedFile file : delta.getChangedFiles()) {
			finder.checkForShorterPrefix(file.getPath());
		}
		for (AddedFile file : delta.getAddedFiles()) {
			finder.checkForShorterPrefix(file.getPath());
		}
		for (RemovedFile file : delta.getRemovedFiles()) {
			finder.checkForShorterPrefix(file.getPath());
		}
		return finder.getPrefix();
	}

}