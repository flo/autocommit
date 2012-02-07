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

import java.io.IOException;

import org.junit.Test;

public class WorkedOnFileCMFTest {

	private WorkedOnFileCMF createFactory(FileSetDelta delta) {
		WorkedOnFileCMF factory = new WorkedOnFileCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}

	@Test
	public void testRemovedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}

	@Test
	public void testChangedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("some/Path/test.txt", "old content",
				"new content");

		WorkedOnFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.workedOn
				.createMessageWithArgs("some/Path/test.txt");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testOneAddedAndOneChangedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("some/Path/test.txt", "old content",
				"new content");
		builder.addAddedFile("some/Point.txt", "(0, 1)");

		WorkedOnFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		assertEquals(null, actualMessage);
	}
}
