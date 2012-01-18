package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class SingleAddedSectionViewTest {
	private SingleAddedSectionView create(String oldText, String newText)
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/hello.tex", oldText, newText);
		FileSetDelta fileSetDelta = builder.build();
		Session session = new Session();
		session.add(fileSetDelta);
		return session.getInstanceOf(SingleAddedSectionView.class);
	}

	@Test
	public void testAddedSectionInMiddle() throws IOException {
		SingleAddedSectionView view = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{X}\\section{C}\\section{D}\\section{E}");

		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSectionInMiddleWithPrefix() throws IOException {
		SingleAddedSectionView view = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}now comes x\\section{X}\\section{C}\\section{D}\\section{E}");

		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("now comes x", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSectionInMiddleWithChangesInNextSection()
			throws IOException {
		SingleAddedSectionView view = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}\\section{X}\\section{C}x was so cool\\section{D}\\section{E}");

		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("\\section{C}x was so cool",
				addedSectionInfo.getCharactersAddedAfter());
		assertEquals("\\section{C}", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSectionInMiddleWithChangesInPreviousAndNextSection()
			throws IOException {
		SingleAddedSectionView view = create(
				"intro\\section{A}\\section{B}\\section{C}\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}now comes x\\section{X}\\section{C}x was so cool\\section{D}\\section{E}");

		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("now comes x", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("\\section{C}x was so cool",
				addedSectionInfo.getCharactersAddedAfter());
		assertEquals("\\section{C}", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSectionInMiddleWithChangesInPreviousAndNextLargerSection()
			throws IOException {
		SingleAddedSectionView view = create(
				"intro\\section{A}\\section{B}this is b;\\section{C}this is c;\\section{D}\\section{E}",
				"intro\\section{A}\\section{B}this is b;now comes x;\\section{X}\\section{C}x was so cool;this is c;\\section{D}\\section{E}");

		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("now comes x;",
				addedSectionInfo.getCharactersAddedBefore());
		assertEquals("\\section{C}x was so cool;",
				addedSectionInfo.getCharactersAddedAfter());
		assertEquals("\\section{C}", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSectionAfterIntro() throws IOException {
		SingleAddedSectionView view = create("intro\\section{A}\\section{B}",
				"intro\\section{X}\\section{A}\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSectionAfterModifiedIntro() throws IOException {
		SingleAddedSectionView view = create("intro\\section{A}\\section{B}",
				"new intro\\section{X}\\section{A}\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("new intro", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("intro", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testRemovedSectionAfterModifiedIntro() throws IOException {
		SingleAddedSectionView view = create(
				"new intro\\section{X}\\section{A}\\section{B}",
				"intro\\section{A}\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNull(addedSectionInfo);
	}

	@Test
	public void testAddedFirstSubSection() throws IOException {
		SingleAddedSectionView view = create("\\section{A}\\section{B}",
				"\\section{A}\\subsection{X}\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSecondSubSection() throws IOException {
		SingleAddedSectionView view = create(
				"\\section{A}\\subsection{a}\\section{B}",
				"\\section{A}\\subsection{a}\\subsection{X}\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedTextAndSubsection() throws IOException {
		SingleAddedSectionView view = create("\\section{A}\\section{B}",
				"\\section{A}hello\\subsection{X}\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("hello", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSubsectionAndModifiedNextSubsection()
			throws IOException {
		SingleAddedSectionView view = create(
				"\\section{A}\\subsection{a}\\section{B}",
				"\\section{A}\\subsection{X}\\subsection{a}hello x\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("\\subsection{a}hello x",
				addedSectionInfo.getCharactersAddedAfter());
		assertEquals("\\subsection{a}", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSubsectionWithTextAndModifySubsectionsAroundIt()
			throws IOException {
		SingleAddedSectionView view = create(
				"\\section{A}\\subsection{a}text of a;\\subsection{b}text of b;\\section{B}",
				"\\section{A}\\subsection{a}text of a;x comes;\\subsection{X}I am X!\\subsection{b}text after x;text of b;\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("x comes;", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("\\subsection{b}text after x;",
				addedSectionInfo.getCharactersAddedAfter());
		assertEquals("\\subsection{b}", addedSectionInfo.getCharactersRemoved());
	}

	@Test
	public void testAddedSubsectionToWhitespaceArea() throws IOException {
		SingleAddedSectionView view = create(
				"\\section{A}\n\n\n\n\n\\section{B}",
				"\\section{A}\n\n\\subsection{X}\n\n\n\\section{B}");
		AddedSectionInfo addedSectionInfo = view.getAddedSectionInfo();
		assertNotNull(addedSectionInfo);
		assertEquals("X", addedSectionInfo.getAddedSection().getCaption());
		assertEquals("", addedSectionInfo.getCharactersAddedBefore());
		assertEquals("", addedSectionInfo.getCharactersAddedAfter());
		assertEquals("", addedSectionInfo.getCharactersRemoved());
	}

}
