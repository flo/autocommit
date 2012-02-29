/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

import java.io.IOException;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.Session;

/**
 * This is a helper class to determine if only one section of a LaTeX document
 * got modified. It should be used as an field annotated with
 * {@link InjectedBySession} which in turn gets initialized by a {@link Session}
 * object.
 * 
 */
public class SingleChangedSectionView extends
		AbstractViewWithCache<OutlineNodeDelta> {

	@InjectedBySession
	private SingleChangedTexFileView singleChangedTexFileView;

	@Override
	protected OutlineNodeDelta determineCachableValue() throws IOException {
		OutlineNodeDelta delta = singleChangedTexFileView.getRootDelta();
		if (delta == null) {
			return delta;
		}
		return delta.findMostSpecificDelta();
	}

	public OutlineNodeDelta getSpecificDelta() throws IOException {
		return getCachableValue();
	}

}
