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
import java.util.List;

import de.fkoeberle.autocommit.message.AbstractViewWithCache;
import de.fkoeberle.autocommit.message.ChangedRange;
import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.Session;

/**
 * This is a helper class to determine if only one section got added to a latex
 * document. It should be used as an field annotated with
 * {@link InjectedBySession} which in turn gets initialized by a {@link Session}
 * object.
 * 
 */
public class SingleAddedSectionView extends
		AbstractViewWithCache<AddedSectionInfo> {

	@InjectedBySession
	private SingleChangedSectionView singleChangedSectionView;

	@Override
	protected AddedSectionInfo determineCachableValue() throws IOException {
		OutlineNodeDelta parentDelta = singleChangedSectionView
				.getSpecificDelta();
		if (parentDelta == null) {
			return null;
		}

		List<OutlineNode> oldChilds = parentDelta.getOldOutlineNode()
				.getChildNodes();
		List<OutlineNode> newChilds = parentDelta.getNewOutlineNode()
				.getChildNodes();

		ChangedRange changedChildIndices = parentDelta.getChangedChildIndices();
		int countNew = changedChildIndices.getExlusiveEndOfNew()
				- changedChildIndices.getFirstIndex();
		int countOld = changedChildIndices.getExlusiveEndOfOld()
				- changedChildIndices.getFirstIndex();

		int addedSectionIndex;
		if (countNew == 1 && countOld == 0) {
			addedSectionIndex = changedChildIndices.getFirstIndex();
		} else if (countNew == 2 && countOld == 1) {
			/*
			 * When a new section gets added then often the section below or
			 * above it gets modified too.
			 */
			OutlineNode oldChild = oldChilds.get(changedChildIndices
					.getFirstIndex());
			OutlineNode newChild0 = newChilds.get(changedChildIndices
					.getFirstIndex());
			OutlineNode newChild1 = newChilds.get(changedChildIndices
					.getFirstIndex() + 1);
			if (oldChild.getCaption().equals(newChild0.getCaption())) {
				addedSectionIndex = changedChildIndices.getFirstIndex() + 1;
			} else if (oldChild.getCaption().equals(newChild1.getCaption())) {
				addedSectionIndex = changedChildIndices.getFirstIndex();
			} else {
				return null;
			}
		} else if (countNew == 3 && countOld == 2) {
			/*
			 * Handle the case that a section below and above got modified.
			 */
			OutlineNode oldChild0 = oldChilds.get(changedChildIndices
					.getFirstIndex());
			OutlineNode oldChild1 = oldChilds.get(changedChildIndices
					.getFirstIndex() + 1);
			OutlineNode newChild0 = newChilds.get(changedChildIndices
					.getFirstIndex());
			OutlineNode newChild2 = newChilds.get(changedChildIndices
					.getFirstIndex() + 2);
			if ((oldChild0.getCaption().equals(newChild0.getCaption()))
					&& (oldChild1.getCaption().equals(newChild2.getCaption()))) {
				addedSectionIndex = changedChildIndices.getFirstIndex() + 1;
			} else {
				return null;
			}
		} else {
			return null;
		}
		OutlineNode addedSection = newChilds.get(addedSectionIndex);
		ChangedRange charRange = parentDelta.getSmartChangedCharacterRange();
		String oldContent = parentDelta.getChangedTextFile().getOldContent();
		String newContent = parentDelta.getChangedTextFile().getNewContent();

		String textAddedBefore = newContent.substring(
				charRange.getFirstIndex(), addedSection.getFirstIndex());
		String textAddedAfter = newContent.substring(
				addedSection.getExlusiveEndIndex(),
				Math.max(addedSection.getExlusiveEndIndex(),
						charRange.getExlusiveEndOfNew()));
		String textRemoved = oldContent.substring(charRange.getFirstIndex(),
				charRange.getExlusiveEndOfOld());

		return new AddedSectionInfo(parentDelta, addedSectionIndex,
				textAddedBefore, textAddedAfter, textRemoved);
	}

	/**
	 * 
	 * @return a description of the added section. It tries to return a value
	 *         even if the surroundings of the added section got modified too.
	 *         When no single section got added this method returns null.
	 */
	public AddedSectionInfo getAddedSectionInfo() throws IOException {
		return getCachableValue();
	}

}
