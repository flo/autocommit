package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.ISession;
import de.fkoeberle.autocommit.message.java.WorkedOnPackageCMF;

public class WorkedOnPackageCMFTest {
	private ISession session;

	@Before
	public void initialize() {
		session = new TestSession();
	}

	@Test
	public void testSingleAddedFile() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testSingleRemovedFile() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testSingleModifiedFile() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addModifiedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x;}",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedAndModifiedFileInSamePackage() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass {}");
		builder.addModifiedFile("/project1/org/example/Mod.java",
				"package org.example;\n\nclass Mod {int x;}",
				"package org.example;\n\nclass Mod {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testMovedFileBetweenPackages() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/oldpackage/Test.java",
				"package org.example.oldpackage;\n\nclass Test {}");
		builder.addAddedFile("/project1/org/example/newpackage/Test.java",
				"package org.example.newpackage;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnSubPackages
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testMoveToSamePackageInOtherFolder() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		builder.addAddedFile("/project2/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnPackage
				.createMessageWithArgs("org.example");
		assertEquals(expected, message);
	}

	@Test
	public void testDefaultPackage() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/Test.java", "class Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnDefaultPackage
				.createMessageWithArgs();
		assertEquals(expected, message);
	}

	@Test
	public void testNonMatchingPath() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/other/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = null;
		assertEquals(expected, message);
	}

	/**
	 * The aim of this test is to show that that the implementation does not
	 * look at the content of the second file thus does not notice the error.
	 * It's important that not all files get parsed but only one per source
	 * folder so that the time can be saved to analyze all files.
	 */
	@Test
	public void testThatPerformanceOptimizationGotUsed() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/One.java",
				"package org.example;\n\nclass One {}");
		builder.addAddedFile("/project1/org/other/Two.java",
				"package org.example;\n\nclass Two {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.workedOnSubPackages
				.createMessageWithArgs("org");
		assertEquals(expected, message);
	}

	@Test
	public void testThatASecondNonMatchingPackageGetsDetectedIfItsInOtherSourceFolder() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/One.java",
				"package org.example;\n\nclass One {}");
		builder.addAddedFile("/project2/org/other/Two.java",
				"package org.example;\n\nclass Two {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = null;
		assertEquals(expected, message);
	}

}
