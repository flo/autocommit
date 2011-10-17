package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class WorkedOnPathCMFTest {
	private Session session;

	@Before
	public void initialize() {
		session = new Session();
	}

	@Test
	public void testAddedFile() {
		WorkedOnPathCMF factory = new WorkedOnPathCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();

		String actualMessage = factory.createMessageFor(delta, session);
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/project1/org/example/Test.java");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemovedFile() {
		WorkedOnPathCMF factory = new WorkedOnPathCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();

		String actualMessage = factory.createMessageFor(delta, session);
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/project1/org/example/Test.java");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testChangedFile() {
		WorkedOnPathCMF factory = new WorkedOnPathCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addModifiedFile("/some/Path/test.txt",
				"old content","new content");

		FileSetDelta delta = builder.build();

		String actualMessage = factory.createMessageFor(delta, session);
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/some/Path/test.txt");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testOneAddedAndOneChangedFile() {
		WorkedOnPathCMF factory = new WorkedOnPathCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addModifiedFile("/some/Path/test.txt", "old content",
				"new content");
		builder.addAddedFile("/some/Point.txt", "(0, 1)");

		FileSetDelta delta = builder.build();

		String actualMessage = factory.createMessageFor(delta, session);
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("/some/");
		assertEquals(expectedMessage, actualMessage);
	}
}
