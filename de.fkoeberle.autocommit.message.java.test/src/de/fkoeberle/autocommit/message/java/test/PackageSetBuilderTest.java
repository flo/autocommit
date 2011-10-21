package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.CachingJavaFileContentParser;
import de.fkoeberle.autocommit.message.java.PackageSetBuilder;

public class PackageSetBuilderTest {
	private PackageSetBuilder createBuilder()
			throws IOException {
		Session session = new Session();
		CachingJavaFileContentParser parser = session.getInstanceOf(CachingJavaFileContentParser.class);
		PackageSetBuilder builder = new PackageSetBuilder(parser);
		return builder;
	}

	@Test
	public void testSingleAddedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList("org.example"));
		assertEquals(expected, result);
	}

	@Test
	public void testSingleRemovedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList("org.example"));
		assertEquals(expected, result);
	}

	@Test
	public void testSingleModifiedFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x;}",
				"package org.example;\n\nclass Test {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList("org.example"));
		assertEquals(expected, result);
	}

	@Test
	public void testAddedAndModifiedFileInSamePackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass {}");
		builder.addChangedFile("/project1/org/example/Mod.java",
				"package org.example;\n\nclass Mod {int x;}",
				"package org.example;\n\nclass Mod {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList("org.example"));
		assertEquals(expected, result);
	}

	@Test
	public void testMovedFileBetweenPackages() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/oldpackage/Test.java",
				"package org.example.oldpackage;\n\nclass Test {}");
		builder.addAddedFile("/project1/org/example/newpackage/Test.java",
				"package org.example.newpackage;\n\nclass Test {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList(
				"org.example.oldpackage", "org.example.newpackage"));
		assertEquals(expected, result);
	}

	@Test
	public void testMoveToSamePackageInOtherFolder() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		builder.addAddedFile("/project2/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		packageSetBuilder.addPackagesOf(builder.build());
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList("org.example"));
		assertEquals(expected, result);
	}

	@Test
	public void testDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/Test.java", "class Test {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList(""));
		assertEquals(expected, result);
	}

	@Test
	public void testTwoTimesDefaultPackage() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/IClass.java", "class MyClass {}",
				"class MyClass {void m() {\n};}");
		builder.addChangedFile("/project1/MyInterface.java",
				"interface MyInterface {int  m();}",
				"interface MyInterface {int m();}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList(""));
		assertEquals(expected, result);
	}

	@Test
	public void testNonMatchingPath() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/other/Test.java",
				"package org.example;\n\nclass Test {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertFalse(success);
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

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertTrue(success);
		Set<String> result = packageSetBuilder.getPackages();
		Set<String> expected = new HashSet<String>(Arrays.asList("org.example",
				"org.other"));
		assertEquals(expected, result);
	}

	@Test
	public void testThatASecondNonMatchingPackageGetsDetectedIfItsInOtherSourceFolder()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/One.java",
				"package org.example;\n\nclass One {}");
		builder.addAddedFile("/project2/org/other/Two.java",
				"package org.example;\n\nclass Two {}");

		PackageSetBuilder packageSetBuilder = createBuilder();
		boolean success = packageSetBuilder.addPackagesOf(builder.build());
		assertFalse(success);
	}

}
