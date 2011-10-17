package de.fkoeberle.autocommit.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class CommonParentPathFinderTest {

	@Test
	public void testSimilarSuffix() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/org/example/test1");
		finder.checkPath("/org/example/test2");
		assertEquals("/org/example/", finder.getCommonPath());
	}

	@Test
	public void testDifferInMiddle() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/org/example/te1st/deep");
		finder.checkPath("/org/example/te2st/deep");
		assertEquals("/org/example/", finder.getCommonPath());
	}

	@Test
	public void testSameSuffixButDifferentLength1() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/org/example/test");
		finder.checkPath("/org/example/test2");
		assertEquals("/org/example/", finder.getCommonPath());
	}

	@Test
	public void testSameSuffixButDifferentLength2() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/org/example/test2");
		finder.checkPath("/org/example/test");
		assertEquals("/org/example/", finder.getCommonPath());
	}

	@Test
	public void testTwoCompleteDifferentPathsWithSameSuffix() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/helloworld");
		finder.checkPath("/world");
		assertEquals("/", finder.getCommonPath());
	}

	@Test
	public void testThreeCompleteDifferentPaths() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/hello");
		finder.checkPath("/world");
		finder.checkPath("/org/example");
		assertEquals("/", finder.getCommonPath());
	}

	@Test
	public void testSmallerAndSmallerCommonPath() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/org/example/test/hello/world");
		finder.checkPath("/org/example/test/hello/x");
		finder.checkPath("/org/example/hello/x");
		assertEquals("/org/example/", finder.getCommonPath());
	}

	@Test
	public void testSamePrefixButNotADirectory() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/org/exampl");
		finder.checkPath("/org/example/test");
		assertEquals("/org/", finder.getCommonPath());
	}

	@Test
	public void testThreeDifferentPrefixes() {
		CommonParentPathFinder finder = new CommonParentPathFinder();
		finder.checkPath("/org/example/test0");
		finder.checkPath("/org/example/test");
		finder.checkPath("/org/example/test1");
		assertEquals("/org/example/", finder.getCommonPath());
	}

}
