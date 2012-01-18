package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.DummyCommitMessageUtil;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class AddedSectionCMFTest {

	private AddedSectionCMF create(FileSetDelta delta) {
		Session session = new Session();
		session.add(delta);
		AddedSectionCMF factory = session.getInstanceOf(AddedSectionCMF.class);
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		return factory;
	}

	@Test
	public void testAddedChapter() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/hello.tex", "", "\\chapter{First Chapter}");
		FileSetDelta fileSetDelta = builder.build();

		AddedSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedChapterMessage
				.createMessageWithArgs("First Chapter");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedSection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{New}\\section{Two}");
		FileSetDelta fileSetDelta = builder.build();

		AddedSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedSectionMessage
				.createMessageWithArgs("New");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedSubsection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\subsection{New}\\section{Two}");
		FileSetDelta fileSetDelta = builder.build();

		AddedSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedSubsectionMessage
				.createMessageWithArgs("New");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedSubsectionToWhitespaceArea() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\n\n\n\n\n\\section{Two}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\n\n\\subsection{New}\n\n\n\\section{Two}");
		FileSetDelta fileSetDelta = builder.build();

		AddedSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedSubsectionMessage
				.createMessageWithArgs("New");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedSubsubsection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\subsubsection{New}\\section{Two}");
		FileSetDelta fileSetDelta = builder.build();

		AddedSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedSubsubsectionMessage
				.createMessageWithArgs("New");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedNoSectionButText() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}X\\section{Two}");
		FileSetDelta fileSetDelta = builder.build();

		AddedSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}

	@Test
	public void testAddedSectionToNonTexFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.txt",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\subsubsection{New}\\section{Two}");
		FileSetDelta fileSetDelta = builder.build();

		AddedSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}
}
