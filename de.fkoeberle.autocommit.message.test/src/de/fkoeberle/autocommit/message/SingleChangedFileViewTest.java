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
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class SingleChangedFileViewTest {
	private SingleChangedFileView create(FileSetDelta delta) {
		Session session = new Session();
		session.add(delta);
		return session.getInstanceOf(SingleChangedFileView.class);
	}

	@Test
	public void testOneChangedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/test.txt", "hello", "hello world");
		FileSetDelta fileSetDelta = builder.build();

		SingleChangedFileView view = create(fileSetDelta);
		ChangedFile actualChangedFile = view.getChangedFile();
		ChangedFile expectedChangedFile = fileSetDelta.getChangedFiles().get(0);
		assertEquals(expectedChangedFile, actualChangedFile);
		assertEquals("/test.txt", actualChangedFile.getPath());
		assertSame(actualChangedFile, view.getChangedFile());
	}

	@Test
	public void testAddedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		FileSetDelta fileSetDelta = builder.build();

		SingleChangedFileView view = create(fileSetDelta);
		assertEquals(null, view.getChangedFile());
		assertEquals(null, view.getChangedFile());
	}

	@Test
	public void testRemovedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		FileSetDelta fileSetDelta = builder.build();

		SingleChangedFileView view = create(fileSetDelta);
		assertEquals(null, view.getChangedFile());
		assertEquals(null, view.getChangedFile());
	}

	@Test
	public void testAddedAndChangedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		builder.addChangedFile("/test.txt", "hello", "hello world");
		FileSetDelta fileSetDelta = builder.build();

		SingleChangedFileView view = create(fileSetDelta);
		assertEquals(null, view.getChangedFile());
		assertEquals(null, view.getChangedFile());
	}

	@Test
	public void testRemovedAndChangedFile() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		builder.addChangedFile("/test.txt", "hello", "hello world");
		FileSetDelta fileSetDelta = builder.build();

		SingleChangedFileView view = create(fileSetDelta);
		assertEquals(null, view.getChangedFile());
		assertEquals(null, view.getChangedFile());
	}

	@Test
	public void testTwoChangedFiles() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/test.txt", "hello", "hello world");
		builder.addChangedFile("/test2.txt", "hello", "hello world");
		FileSetDelta fileSetDelta = builder.build();

		SingleChangedFileView view = create(fileSetDelta);
		assertEquals(null, view.getChangedFile());
		assertEquals(null, view.getChangedFile());
	}
}
