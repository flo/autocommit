package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChangedTextFileTest {

	@Test
	public void testChangedRangeOfAddedText() {
		String oldContent = "HelloWorld";
		String newContent = "HelNEWloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testChangedRangeOfAddedTextAtStart() {
		String oldContent = "HelloWorld";
		String newContent = "NEWHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testChangedRangeOfAddedTextAtEnd() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorldNEW";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testChangedRangeOfRemovedText() {
		String oldContent = "HelOLDloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testChangedRangeOfRemovedTextAtStart() {
		String oldContent = "OLDHelloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testChangedRangeOfRemovedTextAtEnd() {
		String oldContent = "HelloWorldOLD";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testChangedRangeOfReplacedText() {
		String oldContent = "HelOLDloWorld";
		String newContent = "HelNEWloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testChangedRangeOfReplacedTextAtStart() {
		String oldContent = "OLDHelloWorld";
		String newContent = "NEWHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testChangedRangeOfReplacedTextAtEnd() {
		String oldContent = "HelloWorldOLD";
		String newContent = "HelloWorldNEW";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("OLD", removedText);
		assertEquals("NEW", addedText);
	}

	@Test
	public void testChangedRangeOfAddedAtStartOrMiddle() {
		String oldContent = "HelloWorld";
		String newContent = "HelloHelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("Hello", addedText);
		assertEquals(5, changedRange.getFirstIndex());
	}

	@Test
	public void testChangedRangeOfAddedAtMiddleOrEnd() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorldWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("World", addedText);
		assertEquals(10, changedRange.getFirstIndex());
	}

	@Test
	public void testChangedRangeWithSame() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("", addedText);
	}

	@Test
	public void testChangedRangeAddedTextAtTwoPossibleLocationsInMiddle() {
		String oldContent = "HelloWorld";
		String newContent = "HelloWoWorld";
		ChangedTextFile changedTextFile = new ChangedTextFile("/path",
				oldContent, newContent);
		ChangedRange changedRange = changedTextFile.getChangedRange();

		String addedText = newContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfNew());
		String removedText = oldContent.substring(changedRange.getFirstIndex(),
				changedRange.getExlusiveEndOfOld());
		assertEquals("", removedText);
		assertEquals("Wo", addedText);
		assertEquals(7, changedRange.getFirstIndex());
	}
}
