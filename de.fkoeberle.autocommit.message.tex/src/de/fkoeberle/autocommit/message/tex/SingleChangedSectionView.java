package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.InjectedBySession;

public class SingleChangedSectionView {

	@InjectedBySession
	private SingleChangedTexFileView singleChangedTexFileView;

	public OutlineNodeDelta getDelta() throws IOException {
		OutlineNodeDelta delta = singleChangedTexFileView.getDelta();
		if (delta == null) {
			return delta;
		}
		return delta.findMostSpecificDelta();
	}

}
