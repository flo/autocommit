/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.factories;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.DummyCommitMessageUtil;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.factories.WorkedOnPackageCMF;

public class WorkedOnPackageCMFTest {

	private WorkedOnPackageCMF createFactory(FileSetDelta delta) {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 4);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testSingleAddedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testSingleRemovedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testSingleModifiedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x;}",
				"package org.example;\n\nclass Test {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedAndModifiedFileInSamePackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass {}");
		builder.addChangedFile("/project1/org/example/Mod.java",
				"package org.example;\n\nclass Mod {int x;}",
				"package org.example;\n\nclass Mod {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testMovedFileBetweenPackages() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/oldpackage/Test.java",
				"package org.example.oldpackage;\n\nclass Test {}");
		builder.addAddedFile("/project1/org/example/newpackage/Test.java",
				"package org.example.newpackage;\n\nclass Test {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnSubPackages
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testMoveToSamePackageInOtherFolder() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		builder.addAddedFile("/project2/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/Test.java", "class Test {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnDefaultPackage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testNonMatchingPath() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/other/Test.java",
				"package org.example;\n\nclass Test {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	/**
	 * The aim of this test is to show that that the implementation does not
	 * look at the content of the second file thus does not notice the error.
	 * It's important that not all files get parsed but only one per source
	 * folder so that the time can be saved to analyze all files.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testThatPerformanceOptimizationGotUsed() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/One.java",
				"package org.example;\n\nclass One {}");
		builder.addAddedFile("/project1/org/other/Two.java",
				"package org.example;\n\nclass Two {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnSubPackages
				.createMessageWithArgs("org");
		assertEquals(expected, message);
	}

	@Test
	public void testThatASecondNonMatchingPackageGetsDetectedIfItsInOtherSourceFolder()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/One.java",
				"package org.example;\n\nclass One {}");
		builder.addAddedFile("/project2/org/other/Two.java",
				"package org.example;\n\nclass Two {}");

		WorkedOnPackageCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

}
