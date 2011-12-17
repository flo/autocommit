package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.ChangedTextFile;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.SingleChangedTextFileView;

public class SingleChangedTexFileView {
	private boolean deltaDetermined;
	private OutlineNodeDelta outlineNodeDelta;

	@InjectedBySession
	private TexParser parser;

	@InjectedBySession
	private SingleChangedTextFileView singleChangedTextFileView;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;

	public OutlineNodeDelta getDelta() throws IOException {
		if (!deltaDetermined) {
			deltaDetermined = true;
			if (!extensions.containsOnly("tex")) {
				return null;
			}
			ChangedTextFile changedTextFile = singleChangedTextFileView
					.getChangedTextFile();
			if (changedTextFile == null) {
				return null;
			}
			OutlineNode oldOutlineNode = parser.parse(
					changedTextFile.getPath(), changedTextFile.getOldContent());
			OutlineNode newOutlineNode = parser.parse(
					changedTextFile.getPath(), changedTextFile.getNewContent());
			this.outlineNodeDelta = new OutlineNodeDelta(changedTextFile,
					oldOutlineNode, newOutlineNode);
		}
		return this.outlineNodeDelta;
	}

}
