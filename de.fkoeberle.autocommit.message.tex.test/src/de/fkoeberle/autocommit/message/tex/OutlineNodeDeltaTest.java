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
		return view.getDelta();
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{X}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(4, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(4, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith2AddedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{X}\\section{Y}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(5, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith2RemovedInMiddle()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}\\section{Y}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(5, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
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
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(4, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedNearEnd()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{X}\\section{C}",
				"intro\\section{A}\\section{B}\\section{C}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
		assertEquals(4, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedAtEnd() throws IOException {
		OutlineNodeDelta delta = create("intro\\section{A}\\section{B}",
				"intro\\section{A}\\section{B}\\section{X}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(1, childIndices.getFirstIndex());
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
		assertEquals(1, childIndices.getFirstIndex());
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
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(2, childIndices.getExlusiveEndOfOld());
		assertEquals(3, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedNearStart()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{X}\\section{B}\\section{C}\\section{D}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(3, childIndices.getExlusiveEndOfOld());
		assertEquals(2, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1AddedAtStart()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}",
				"intro\\section{X}\\section{A}\\section{B}\\section{C}\\section{D}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(1, childIndices.getExlusiveEndOfOld());
		assertEquals(2, childIndices.getExlusiveEndOfNew());
	}

	@Test
	public void testGetChangedChildIndicesWith1RemovedAtStart()
			throws IOException {
		OutlineNodeDelta delta = create(
				"intro\\section{X}\\section{A}\\section{B}\\section{C}\\section{D}",
				"intro\\section{A}\\section{B}\\section{C}\\section{D}");
		ChangedRange childIndices = delta.getChangedChildIndices();
		assertEquals(0, childIndices.getFirstIndex());
		assertEquals(2, childIndices.getExlusiveEndOfOld());
		assertEquals(1, childIndices.getExlusiveEndOfNew());
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
}
