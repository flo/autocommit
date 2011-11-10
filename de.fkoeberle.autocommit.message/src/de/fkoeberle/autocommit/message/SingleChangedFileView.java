package de.fkoeberle.autocommit.message;


public class SingleChangedFileView {
	private boolean changedFileDetermined;
	private ChangedFile changedFile;

	@InjectedBySession
	private FileSetDelta delta;


	public ChangedFile getChangedFile() {
		if (!changedFileDetermined) {
			changedFileDetermined = true;
			if (delta.getChangedFiles().size() != 1) {
				return null;
			}
			if (delta.getRemovedFiles().size() != 0) {
				return null;
			}
			if (delta.getAddedFiles().size() != 0) {
				return null;
			}

			changedFile = delta.getChangedFiles().get(0);
		}
		return changedFile;
	}
}
