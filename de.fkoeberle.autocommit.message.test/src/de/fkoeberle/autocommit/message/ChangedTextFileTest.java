/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChangedTextFileTest {

	@Test
	public void testEarliestChangedRangeOfAddedText() {
		String oldContent = "HelloWorld";
		String newContent = "HelNEWloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testLatestChangedRangeOfAddedText() {
		String oldContent = "HelloWorld";
		String newContent = "HelNEWloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfAddedTextAtStart() {
		String oldContent = "HelloWorld";
		String newContent = "NEWHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testLatestChangedRangeOfAddedTextAtStart() {
		String oldContent = "HelloWorld";
		String newContent = "NEWHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfAddedTextAtEnd() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorldNEW";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testLatestChangedRangeOfAddedTextAtEnd() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorldNEW";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfRemovedText() {
		String oldContent = "HelOLDloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testLatestChangedRangeOfRemovedText() {
		String oldContent = "HelOLDloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfRemovedTextAtStart() {
		String oldContent = "OLDHelloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testLatestChangedRangeOfRemovedTextAtStart() {
		String oldContent = "OLDHelloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfRemovedTextAtEnd() {
		String oldContent = "HelloWorldOLD";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testLatestChangedRangeOfRemovedTextAtEnd() {
		String oldContent = "HelloWorldOLD";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfReplacedText() {
		String oldContent = "HelOLDloWorld";
		String newContent = "HelNEWloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testLatestChangedRangeOfReplacedText() {
		String oldContent = "HelOLDloWorld";
		String newContent = "HelNEWloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfReplacedTextAtStart() {
		String oldContent = "OLDHelloWorld";
		String newContent = "NEWHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testLatestChangedRangeOfReplacedTextAtStart() {
		String oldContent = "OLDHelloWorld";
		String newContent = "NEWHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testEearliestChangedRangeOfReplacedTextAtEnd() {
		String oldContent = "HelloWorldOLD";
		String newContent = "HelloWorldNEW";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testLatestChangedRangeOfReplacedTextAtEnd() {
		String oldContent = "HelloWorldOLD";
		String newContent = "HelloWorldNEW";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testEarliestChangedRangeOfAddedAtStartOrMiddle() {
		String oldContent = "HelloWorld";
		String newContent = "HelloHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("Hello", addedText);
		assertEquals(0, changedRange.getFirstIndex());
	}

	@Test
	public void testLatestChangedRangeOfAddedAtStartOrMiddle() {
		String oldContent = "HelloWorld";
		String newContent = "HelloHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("Hello", addedText);
		assertEquals(5, changedRange.getFirstIndex());
	}

	@Test
	public void testEarliestChangedRangeOfAddedAtMiddleOrEnd() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorldWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("World", addedText);
		assertEquals(5, changedRange.getFirstIndex());
	}

	@Test
	public void testLatestChangedRangeOfAddedAtMiddleOrEnd() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorldWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("World", addedText);
		assertEquals(10, changedRange.getFirstIndex());
	}

	@Test
	public void testEarliestChangedRangeWithSame() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testLatestChangedRangeWithSame() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testEarliestChangedRangeAddedTextAtTwoPossibleLocationsInMiddle() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWoWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getEarliestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("oW", addedText);
		assertEquals(4, changedRange.getFirstIndex());
	}

	@Test
	public void testLatestChangedRangeAddedTextAtTwoPossibleLocationsInMiddle() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWoWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getLatestChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("Wo", addedText);
		assertEquals(7, changedRange.getFirstIndex());
	}
}
