package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WorkedOnPathCMFTest {

	private WorkedOnPathCMF createFactory(FileSetDelta delta) {
		WorkedOnPathCMF factory = new WorkedOnPathCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnPathCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/project1/org/example/Test.java");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemovedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnPathCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/project1/org/example/Test.java");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testChangedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/some/Path/test.txt", "old content",
				"new content");

		WorkedOnPathCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/some/Path/test.txt");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testOneAddedAndOneChangedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/some/Path/test.txt", "old content",
				"new content");
		builder.addAddedFile("/some/Point.txt", "(0, 1)");

		WorkedOnPathCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/some/");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedFilesWithNoCommonPath() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/some/Point.txt", "(0, 1)");
		builder.addAddedFile("/other.txt", "(0, 1)");

		WorkedOnPathCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/");
		assertEquals(expectedMessage, actualMessage);
	}
}
