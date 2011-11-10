package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.ChangedFile;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.FileContentReader;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.SingleChangedFileView;

public class SingleChangedTexFileView {
	private boolean deltaDetermined;
	private OutlineNodeDelta outlineNodeDelta;

	@InjectedBySession
	private TexParser parser;

	@InjectedBySession
	private FileContentReader reader;

	@InjectedBySession
	private SingleChangedFileView singleChangedFileView;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;


	public OutlineNodeDelta getDelta() throws IOException {
		if (!deltaDetermined) {
			deltaDetermined = true;
			if (!extensions.containsOnly("tex")) {
				return null;
			}
			ChangedFile changedFile = singleChangedFileView.getChangedFile();
			if (changedFile == null) {
				return null;
			}
			String oldString = reader.getStringFor(changedFile.getOldContent());
			String newString = reader.getStringFor(changedFile.getNewContent());
			OutlineNode oldOutlineNode = parser.parse(changedFile.getPath(),
					oldString);
			OutlineNode newOutlineNode = parser.parse(changedFile.getPath(),
					newString);
			this.outlineNodeDelta = new OutlineNodeDelta(
					oldOutlineNode, newOutlineNode);
		}
		return this.outlineNodeDelta;
	}

}
