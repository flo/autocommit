package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TexParserTest {

	private static final String FILE_NAME = "test.tex";

	@Test
	public void testOneChapterStatementOnly() {
		TexParser texParser = new TexParser();
		OutlineNode rootNode = texParser.parse(FILE_NAME,
				"\\chapter{Hello World}");

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());

		assertEquals(1, rootNode.getChildNodes().size());
		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("Hello World", chapterNode.getCaption());

	}

	@Test
	public void testTwoEmptySectionsInChapter() {
		TexParser texParser = new TexParser();
		OutlineNode rootNode = texParser
				.parse(FILE_NAME,
						"\\chapter{First Chapter}\\section{First Section}\\section{Second Section}");

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());

		assertEquals(1, rootNode.getChildNodes().size());
		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());

		OutlineNode firstSection = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, firstSection.getType());
		assertEquals("First Section", firstSection.getCaption());
		assertEquals(0, firstSection.getChildNodes().size());

		OutlineNode secondSection = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, secondSection.getType());
		assertEquals("Second Section", secondSection.getCaption());
		assertEquals(0, secondSection.getChildNodes().size());
	}

	@Test
	public void testTwoNonEmptySectionsInChapter() {
		TexParser texParser = new TexParser();
		OutlineNode rootNode = texParser
				.parse(FILE_NAME,
						"\\chapter{First Chapter}\n This is the first chapter.\n\\section{First Section}\nSome text for the first chapter\\section{Second Section} Text for 2nd section");

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());

		assertEquals(1, rootNode.getChildNodes().size());
		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());

		OutlineNode firstSection = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, firstSection.getType());
		assertEquals("First Section", firstSection.getCaption());
		assertEquals(0, firstSection.getChildNodes().size());

		OutlineNode secondSection = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, secondSection.getType());
		assertEquals("Second Section", secondSection.getCaption());
		assertEquals(0, secondSection.getChildNodes().size());
	}

	@Test
	public void testEndingASubSubSectionWithASection() {
		TexParser texParser = new TexParser();
		OutlineNode rootNode = texParser
				.parse(FILE_NAME,
						"\\chapter{First Chapter}\n This is the first chapter.\n\\section{First Section}\nSome text for the first chapter\\subsection{First Subsection} Text for subsection. \\subsubsection{The subsubsection headline} \\section{Ending Section} text");

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());

		assertEquals(1, rootNode.getChildNodes().size());
		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());

		OutlineNode sectionNode = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, sectionNode.getType());
		assertEquals("First Section", sectionNode.getCaption());
		assertEquals(1, sectionNode.getChildNodes().size());

		OutlineNode subsectionNode = sectionNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SUBSECTION, subsectionNode.getType());
		assertEquals("First Subsection", subsectionNode.getCaption());
		assertEquals(1, subsectionNode.getChildNodes().size());

		OutlineNode subsubsectionNode = subsectionNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SUBSUBSECTION, subsubsectionNode.getType());
		assertEquals("The subsubsection headline",
				subsubsectionNode.getCaption());
		assertEquals(0, subsubsectionNode.getChildNodes().size());

		OutlineNode endingSectionNode = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, endingSectionNode.getType());
		assertEquals("Ending Section", endingSectionNode.getCaption());
		assertEquals(0, endingSectionNode.getChildNodes().size());
	}

	@Test
	public void testDisabledSecondSectionOfThreeSectionsWithUnixLineBreaks() {
		TexParser texParser = new TexParser();
		OutlineNode rootNode = texParser
				.parse(FILE_NAME,
						"\\chapter{First Chapter}\n This is the first chapter.\n\\section{Section A}\n some text for first section\n%\\section{Section B}\n some text for second section\n\\section{Section C}\n some text for third section\n");

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());

		assertEquals(1, rootNode.getChildNodes().size());
		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());

		OutlineNode sectionANode = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, sectionANode.getType());
		assertEquals("Section A", sectionANode.getCaption());
		assertEquals(0, sectionANode.getChildNodes().size());

		OutlineNode sectionCNode = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, sectionCNode.getType());
		assertEquals("Section C", sectionCNode.getCaption());
		assertEquals(0, sectionCNode.getChildNodes().size());

	}

	@Test
	public void testCommentRemoval() {
		String inputText = "This\n is\r% a comment\nbut \n\rnot here\r\n.Another starts%here\r.";
		String actualResult = TexParser.withoutComments(inputText).toString();
		String expectedResult = "This\n is\r%%%%%%%%%%%\nbut \n\rnot here\r\n.Another starts%%%%%\r.";
		assertEquals(expectedResult, actualResult);
	}
}
