/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.ChangedRange;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class OutlineNodeDeltaTest {
	private OutlineNodeDelta create(String oldText, String newText)
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/hello.tex", oldText, newText);
		FileSetDelta fileSetDelta = builder.build();
		Session session = new Session();
		session.add(fileSetDelta);
		SingleChangedTexFileView view = session
				.getInstanceOf(SingleChangedTexFileView.class);
		return view.getRootDelta();
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{X}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(2, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(2, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith2AddedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{X}\\section{Y}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(2, childIndices.getExlusiveEndOfOld());
		assertEquals(4, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith2RemovedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}\\section{Y}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(4, childIndices.getExlusiveEndOfOld());
		assertEquals(2, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1ReplacedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{Y}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedNearEnd()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}",
				"intro\\section{A}\\section{B}\\section{X}\\section{C}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(2, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedNearEnd()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}\\section{C}",
				"intro\\section{A}\\section{B}\\section{C}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(2, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedAtEnd() throws IOException {
		OutlineNodeDelta delta = create("intro\\section{A}\\section{B}",
				"intro\\section{A}\\section{B}\\section{X}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(2, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedAtEnd()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}",
				"intro\\section{A}\\section{B}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(2, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(2, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedNearStart()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}",
				"intro\\section{A}\\section{X}\\section{B}\\section{C}\\section{D}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(1, childIndices.getExlusiveEndOfOld());
		assertEquals(2, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedNearStart()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{X}\\section{B}\\section{C}\\section{D}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(2, childIndices.getExlusiveEndOfOld());
		assertEquals(1, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedAtStart()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}",
				"intro\\section{X}\\section{A}\\section{B}\\section{C}\\section{D}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(0, childIndices.getExlusiveEndOfOld());
		assertEquals(1, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedAtStart()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{X}\\section{A}\\section{B}\\section{C}\\section{D}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(1, childIndices.getExlusiveEndOfOld());
		assertEquals(0, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedWithoutOtherSections()
			throws IOException {
		OutlineNodeDelta delta = create("intro", "intro\\section{X}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(0, childIndices.getExlusiveEndOfOld());
		assertEquals(1, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedWithoutOtherSections()
			throws IOException {
		OutlineNodeDelta delta = create("intro\\section{X}", "intro");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(1, childIndices.getExlusiveEndOfOld());
		assertEquals(0, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedWithoutAnything()
			throws IOException {
		OutlineNodeDelta delta = create("", "\\section{X}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(0, childIndices.getExlusiveEndOfOld());
		assertEquals(1, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedWithoutAnything()
			throws IOException {
		OutlineNodeDelta delta = create("\\section{X}", "");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(1, childIndices.getExlusiveEndOfOld());
		assertEquals(0, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithOneAddedAndOneModifiedSection()
			throws IOException {
		String oldContent = "intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}";
		String newContent = "intro\\section{A}\\section{B}\\section{X}\\section{C}x was so cool\\section{D}\\section{E}";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();

		assertEquals(
				"\\section{X}\\section{C}x was so cool",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = "intro\\section{A}\\section{B}".length();
		int expectedExlusiveEndOfOld = "intro\\section{A}\\section{B}\\section{C}"
				.length();
		int expectedExlusiveEndOfNew = "intro\\section{A}\\section{B}\\section{X}\\section{C}x was so cool"
				.length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithOneRemovedAndOneModifiedSection()
			throws IOException {
		String oldContent = "intro\\section{A}\\section{B}\\section{X}\\section{C}x was so cool\\section{D}\\section{E}";
		String newContent = "intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();

		assertEquals(
				"\\section{X}\\section{C}x was so cool",
				oldContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfOld()));
		int expectedFirstIndex = "intro\\section{A}\\section{B}".length();
		int expectedExlusiveEndOfOld = "intro\\section{A}\\section{B}\\section{X}\\section{C}x was so cool"
				.length();
		int expectedExlusiveEndOfNew = "intro\\section{A}\\section{B}\\section{C}"
				.length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithOneModifiedAndOneAddedSection()
			throws IOException {
		String oldContent = "intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}";
		String newContent = "intro\\section{A}\\section{B}now comes x\\section{X}\\section{C}\\section{D}\\section{E}";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();

		assertEquals(
				"now comes x\\section{X}",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = "intro\\section{A}\\section{B}".length();
		int expectedExlusiveEndOfOld = "intro\\section{A}\\section{B}".length();
		int expectedExlusiveEndOfNew = "intro\\section{A}\\section{B}now comes x\\section{X}"
				.length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithOneModifiedAndOneRemovedSection()
			throws IOException {
		String oldContent = "intro\\section{A}\\section{B}now comes x\\section{X}\\section{C}\\section{D}\\section{E}";
		String newContent = "intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();

		assertEquals(
				"now comes x\\section{X}",
				oldContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfOld()));
		int expectedFirstIndex = "intro\\section{A}\\section{B}".length();
		int expectedExlusiveEndOfOld = "intro\\section{A}\\section{B}now comes x\\section{X}"
				.length();
		int expectedExlusiveEndOfNew = "intro\\section{A}\\section{B}".length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithAddedSectionAfterModifiedIntro()
			throws IOException {
		String oldContent = "intro\\section{A}\\section{B}";
		String newContent = "new intro\\section{X}\\section{A}\\section{B}";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();

		assertEquals(
				"new intro\\section{X}",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = 0;
		int expectedExlusiveEndOfOld = "intro".length();
		int expectedExlusiveEndOfNew = "new intro\\section{X}".length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithRemovedSectionAfterModifiedIntro()
			throws IOException {
		String oldContent = "old intro\\section{X}\\section{A}\\section{B}";
		String newContent = "intro\\section{A}\\section{B}";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();
		assertEquals(
				"old intro\\section{X}",
				oldContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfOld()));
		assertEquals(
				"intro",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = 0;
		int expectedExlusiveEndOfOld = "new intro\\section{X}".length();
		int expectedExlusiveEndOfNew = "intro".length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWith1AddedWithoutAnything()
			throws IOException {
		String oldContent = "";
		String newContent = "\\section{X}";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();
		assertEquals(
				"",
				oldContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfOld()));
		assertEquals(
				"\\section{X}",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = 0;
		int expectedExlusiveEndOfOld = 0;
		int expectedExlusiveEndOfNew = "\\section{X}".length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWith1RemovedWithoutAnything()
			throws IOException {
		String oldContent = "\\section{X}";
		String newContent = "";
		OutlineNodeDelta delta = create(oldContent, newContent);
		ChangedRange range = delta.getSmartChangedCharacterRange();
		assertEquals(
				"\\section{X}",
				oldContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfOld()));
		assertEquals(
				"",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = 0;
		int expectedExlusiveEndOfOld = "\\section{X}".length();
		int expectedExlusiveEndOfNew = 0;
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithFirstAddedSubsection()
			throws IOException {
		String oldContent = "\\section{A}\\section{B}";
		String newContent = "\\section{A}\\subsection{X}\\section{B}";
		OutlineNodeDelta parentDelta = create(oldContent, newContent);
		OutlineNodeDelta delta = parentDelta.findMostSpecificDelta();
		ChangedRange range = delta.getSmartChangedCharacterRange();
		assertEquals(
				"",
				oldContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfOld()));
		assertEquals(
				"\\subsection{X}",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = "\\section{A}".length();
		int expectedExlusiveEndOfOld = "\\section{A}".length();
		int expectedExlusiveEndOfNew = "\\section{A}\\subsection{X}".length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}

	@Test
	public void testGetSmartChangedCharacterRangeWithFirstRemovedSubsection()
			throws IOException {
		String oldContent = "\\section{A}\\subsection{X}\\section{B}";
		String newContent = "\\section{A}\\section{B}";
		OutlineNodeDelta parentDelta = create(oldContent, newContent);
		OutlineNodeDelta delta = parentDelta.findMostSpecificDelta();
		ChangedRange range = delta.getSmartChangedCharacterRange();
		assertEquals(
				"\\subsection{X}",
				oldContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfOld()));
		assertEquals(
				"",
				newContent.substring(range.getFirstIndex(),
						range.getExlusiveEndOfNew()));
		int expectedFirstIndex = "\\section{A}".length();
		int expectedExlusiveEndOfOld = "\\section{A}\\subsection{X}".length();
		int expectedExlusiveEndOfNew = "\\section{A}".length();
		assertEquals(expectedFirstIndex, range.getFirstIndex());
		assertEquals(expectedExlusiveEndOfOld, range.getExlusiveEndOfOld());
		assertEquals(expectedExlusiveEndOfNew, range.getExlusiveEndOfNew());
	}
}
