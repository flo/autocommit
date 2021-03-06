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

public class WorkedOnDirectoryCMFTest {

	private WorkedOnDirectoryCMF createFactory(FileSetDelta delta) {
		WorkedOnDirectoryCMF factory = new WorkedOnDirectoryCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnDirectoryCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("project1/org/example/");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testRemovedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnDirectoryCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("project1/org/example/");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testChangedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("some/Path/test.txt", "old content",
				"new content");

		WorkedOnDirectoryCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("some/Path/");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testOneAddedAndOneChangedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("some/Path/test.txt", "old content",
				"new content");
		builder.addAddedFile("some/Point.txt", "(0, 1)");

		WorkedOnDirectoryCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("some/");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedFilesWithNoCommonPath() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("some/Point.txt", "(0, 1)");
		builder.addAddedFile("other.txt", "(0, 1)");

		WorkedOnDirectoryCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("./");
		assertEquals(expectedMessage, actualMessage);
	}
}
