package de.fkoeberle.autocommit.git;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;

interface FileSetDeltaVisitor {
	void visitAddedFile(String path, ObjectId newObjectId)
			throws IOException;

	void visitRemovedFile(String path, ObjectId oldObjectId)
			throws IOException;

	void visitChangedFile(String path, ObjectId oldObjectId,
			ObjectId newObjectId) throws IOException;
}