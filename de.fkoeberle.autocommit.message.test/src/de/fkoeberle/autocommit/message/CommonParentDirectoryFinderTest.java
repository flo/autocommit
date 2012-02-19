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

public class CommonParentDirectoryFinderTest {

	@Test
	public void testFileStartingWithDot() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath(".test");
		assertEquals("./", finder.getCommonDirectory());
	}

	@Test
	public void testOneFile() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("test");
		assertEquals("./", finder.getCommonDirectory());
	}

	@Test
	public void testOneFileInDirectory() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("hello/test");
		assertEquals("hello/", finder.getCommonDirectory());
	}

	@Test
	public void testOneFileInSubDirectory() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("hello/world/test");
		assertEquals("hello/world/", finder.getCommonDirectory());
	}

	@Test
	public void testSimilarSuffix() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("org/example/test1");
		finder.handleFilePath("org/example/test2");
		assertEquals("org/example/", finder.getCommonDirectory());
	}

	@Test
	public void testDifferInMiddle() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("org/example/te1st/deep");
		finder.handleFilePath("org/example/te2st/deep");
		assertEquals("org/example/", finder.getCommonDirectory());
	}

	@Test
	public void testSameSuffixButDifferentLength1() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("org/example/test");
		finder.handleFilePath("org/example/test2");
		assertEquals("org/example/", finder.getCommonDirectory());
	}

	@Test
	public void testSameSuffixButDifferentLength2() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("org/example/test2");
		finder.handleFilePath("org/example/test");
		assertEquals("org/example/", finder.getCommonDirectory());
	}

	@Test
	public void testTwoCompleteDifferentPathsWithSameSuffix() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("helloworld");
		finder.handleFilePath("world");
		assertEquals("./", finder.getCommonDirectory());
	}

	@Test
	public void testThreeCompleteDifferentPaths() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("hello");
		finder.handleFilePath("world");
		finder.handleFilePath("org/example");
		assertEquals("./", finder.getCommonDirectory());
	}

	@Test
	public void testSmallerAndSmallerCommonPath() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("org/example/test/hello/world");
		finder.handleFilePath("org/example/test/hello/x");
		finder.handleFilePath("org/example/hello/x");
		assertEquals("org/example/", finder.getCommonDirectory());
	}

	@Test
	public void testSamePrefixButNotADirectory() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("org/exampl");
		finder.handleFilePath("org/example/test");
		assertEquals("org/", finder.getCommonDirectory());
	}

	@Test
	public void testThreeDifferentPrefixes() {
		CommonParentDirectoryFinder finder = new CommonParentDirectoryFinder();
		finder.handleFilePath("org/example/test0");
		finder.handleFilePath("org/example/test");
		finder.handleFilePath("org/example/test1");
		assertEquals("org/example/", finder.getCommonDirectory());
	}

}
