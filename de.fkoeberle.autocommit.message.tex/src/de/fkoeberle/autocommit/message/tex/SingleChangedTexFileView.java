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
import de.fkoeberle.autocommit.message.ChangedTextFile;
import de.fkoeberle.autocommit.message.ExtensionsOfAddedModifiedOrChangedFiles;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.SingleChangedTextFileView;

public class SingleChangedTexFileView extends
		AbstractViewWithCache<OutlineNodeDelta> {
	@InjectedBySession
	private TexParser parser;

	@InjectedBySession
	private SingleChangedTextFileView singleChangedTextFileView;

	@InjectedBySession
	private ExtensionsOfAddedModifiedOrChangedFiles extensions;

	@Override
	protected OutlineNodeDelta determineCachableValue() throws IOException {
		if (!extensions.containsOnly("tex")) {
			return null;
		}
		ChangedTextFile changedTextFile = singleChangedTextFileView
				.getChangedTextFile();
		if (changedTextFile == null) {
			return null;
		}
		OutlineNode oldOutlineNode = parser.parse(changedTextFile.getPath(),
				changedTextFile.getOldContent());
		OutlineNode newOutlineNode = parser.parse(changedTextFile.getPath(),
				changedTextFile.getNewContent());
		OutlineNodeDelta delta = new OutlineNodeDelta(changedTextFile,
				oldOutlineNode, newOutlineNode);
		return delta;
	}

	public OutlineNodeDelta getRootDelta() throws IOException {
		return getCachableValue();
	}

}
