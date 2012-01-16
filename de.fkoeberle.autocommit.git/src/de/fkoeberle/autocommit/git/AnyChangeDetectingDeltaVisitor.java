package de.fkoeberle.autocommit.git;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;

public class AnyChangeDetectingDeltaVisitor implements FileSetDeltaVisitor {
	private boolean detectedChange = false;

	@Override
	public void visitAddedFile(String path, ObjectId newObjectId)
			throws IOException {
		detectedChange = true;

	}

	@Override
	public void visitRemovedFile(String path, ObjectId oldObjectId)
			throws IOException {
		detectedChange = true;
	}

	@Override
	public void visitChangedFile(String path, ObjectId oldObjectId,
			ObjectId newObjectId) throws IOException {
		detectedChange = true;
	}

	public boolean hasDetectedChange() {
		return detectedChange;
	}

}
