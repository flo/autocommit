package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class SingleChangedSectionView extends
		AbstractViewWithCache<OutlineNodeDelta> {

	@InjectedBySession
	private SingleChangedTexFileView singleChangedTexFileView;

	@Override
	protected OutlineNodeDelta determineCachableValue() throws IOException {
		OutlineNodeDelta delta = singleChangedTexFileView.getDelta();
		if (delta == null) {
			return delta;
		}
		return delta.findMostSpecificDelta();
	}

	public OutlineNodeDelta getDelta() throws IOException {
		return getCachableValue();
	}

}
