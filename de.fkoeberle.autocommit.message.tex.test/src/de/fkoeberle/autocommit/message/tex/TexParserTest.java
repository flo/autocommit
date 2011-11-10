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
		assertEquals("\\chapter{Hello World}", rootNode.getText());

		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("Hello World", chapterNode.getCaption());
		assertEquals("\\chapter{Hello World}", chapterNode.getText());
	}

	@Test
	public void testTwoEmptySectionsInChapter() {
		TexParser texParser = new TexParser();
		String document = "\\chapter{First Chapter}\\section{First Section}\\section{Second Section}";
		OutlineNode rootNode = texParser.parse(FILE_NAME, document);

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());
		assertEquals(1, rootNode.getChildNodes().size());
		assertEquals(document, rootNode.getText());

		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());
		assertEquals(document, chapterNode.getText());

		OutlineNode firstSection = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, firstSection.getType());
		assertEquals("First Section", firstSection.getCaption());
		assertEquals(0, firstSection.getChildNodes().size());
		assertEquals("\\section{First Section}", firstSection.getText());

		OutlineNode secondSection = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, secondSection.getType());
		assertEquals("Second Section", secondSection.getCaption());
		assertEquals(0, secondSection.getChildNodes().size());
		assertEquals("\\section{Second Section}", secondSection.getText());
	}

	@Test
	public void testTwoNonEmptySectionsInChapter() {
		TexParser texParser = new TexParser();
		String document = "\\chapter{First Chapter}\n This is the first chapter.\n\\section{First Section}\nSome text for the first chapter\\section{Second Section} Text for 2nd section";
		OutlineNode rootNode = texParser.parse(FILE_NAME, document);

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());
		assertEquals(1, rootNode.getChildNodes().size());
		assertEquals(document, rootNode.getText());

		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());
		assertEquals(document, chapterNode.getText());

		OutlineNode firstSection = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, firstSection.getType());
		assertEquals("First Section", firstSection.getCaption());
		assertEquals(0, firstSection.getChildNodes().size());
		assertEquals(
				"\\section{First Section}\nSome text for the first chapter",
				firstSection.getText());

		OutlineNode secondSection = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, secondSection.getType());
		assertEquals("Second Section", secondSection.getCaption());
		assertEquals(0, secondSection.getChildNodes().size());
		assertEquals("\\section{Second Section} Text for 2nd section",
				secondSection.getText());

	}

	@Test
	public void testEndingASubSubSectionWithASection() {
		TexParser texParser = new TexParser();
		String document = "\\chapter{First Chapter}\n This is the first chapter.\n\\section{First Section}\nSome text for the first chapter\\subsection{First Subsection} Text for subsection. \\subsubsection{The subsubsection headline} \\section{Ending Section} text";
		OutlineNode rootNode = texParser.parse(FILE_NAME, document);

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());
		assertEquals(1, rootNode.getChildNodes().size());
		assertEquals(document, rootNode.getText());

		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());
		assertEquals(document, chapterNode.getText());

		OutlineNode sectionNode = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, sectionNode.getType());
		assertEquals("First Section", sectionNode.getCaption());
		assertEquals(1, sectionNode.getChildNodes().size());
		assertEquals(
				"\\section{First Section}\nSome text for the first chapter\\subsection{First Subsection} Text for subsection. \\subsubsection{The subsubsection headline} ",
				sectionNode.getText());

		OutlineNode subsectionNode = sectionNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SUBSECTION, subsectionNode.getType());
		assertEquals("First Subsection", subsectionNode.getCaption());
		assertEquals(1, subsectionNode.getChildNodes().size());
		assertEquals(
				"\\subsection{First Subsection} Text for subsection. \\subsubsection{The subsubsection headline} ",
				subsectionNode.getText());

		OutlineNode subsubsectionNode = subsectionNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SUBSUBSECTION, subsubsectionNode.getType());
		assertEquals("The subsubsection headline",
				subsubsectionNode.getCaption());
		assertEquals(0, subsubsectionNode.getChildNodes().size());
		assertEquals("\\subsubsection{The subsubsection headline} ",
				subsubsectionNode.getText());

		OutlineNode endingSectionNode = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, endingSectionNode.getType());
		assertEquals("Ending Section", endingSectionNode.getCaption());
		assertEquals(0, endingSectionNode.getChildNodes().size());
		assertEquals("\\section{Ending Section} text",
				endingSectionNode.getText());
	}

	@Test
	public void testDisabledSecondSectionOfThreeSections() {
		TexParser texParser = new TexParser();
		String document = "\\chapter{First Chapter}\n This is the first chapter.\n\\section{Section A}\n some text for first section\n%\\section{Section B}\n some text for second section\n\\section{Section C}\n some text for third section\n";
		OutlineNode rootNode = texParser.parse(FILE_NAME, document);

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());
		assertEquals(1, rootNode.getChildNodes().size());
		assertEquals(document, rootNode.getText());

		OutlineNode chapterNode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterNode.getType());
		assertEquals("First Chapter", chapterNode.getCaption());
		assertEquals(2, chapterNode.getChildNodes().size());
		assertEquals(document, chapterNode.getText());

		OutlineNode sectionANode = chapterNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.SECTION, sectionANode.getType());
		assertEquals("Section A", sectionANode.getCaption());
		assertEquals(0, sectionANode.getChildNodes().size());
		assertEquals(
				"\\section{Section A}\n some text for first section\n%\\section{Section B}\n some text for second section\n",
				sectionANode.getText());

		OutlineNode sectionCNode = chapterNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.SECTION, sectionCNode.getType());
		assertEquals("Section C", sectionCNode.getCaption());
		assertEquals(0, sectionCNode.getChildNodes().size());
		assertEquals("\\section{Section C}\n some text for third section\n",
				sectionCNode.getText());

	}

	@Test
	public void testFirstChapterOfThreeChaptersIsDisabled() {
		TexParser texParser = new TexParser();
		String document = "%\\chapter{ChapterA}\r This is the first chapter.\r\\chapter{Chapter B}\r some text for chapter B\r\\chapter{Chapter C}\r";
		OutlineNode rootNode = texParser.parse(FILE_NAME, document);

		assertEquals(OutlineNodeType.DOCUMENT, rootNode.getType());
		assertEquals(FILE_NAME, rootNode.getCaption());
		assertEquals(2, rootNode.getChildNodes().size());
		assertEquals(document, rootNode.getText());

		OutlineNode chapterANode = rootNode.getChildNodes().get(0);
		assertEquals(OutlineNodeType.CHAPTER, chapterANode.getType());
		assertEquals("Chapter B", chapterANode.getCaption());
		assertEquals(0, chapterANode.getChildNodes().size());
		assertEquals("\\chapter{Chapter B}\r some text for chapter B\r",
				chapterANode.getText());

		OutlineNode chapterBNode = rootNode.getChildNodes().get(1);
		assertEquals(OutlineNodeType.CHAPTER, chapterBNode.getType());
		assertEquals("Chapter C", chapterBNode.getCaption());
		assertEquals(0, chapterBNode.getChildNodes().size());
		assertEquals("\\chapter{Chapter C}\r", chapterBNode.getText());
	}

	@Test
	public void testCommentRemoval() {
		String inputText = "This\n is\r% a comment\nbut \n\rnot here\r\n.Another starts%here\r.";
		String actualResult = TexParser.withoutComments(inputText).toString();
		String expectedResult = "This\n is\r%%%%%%%%%%%\nbut \n\rnot here\r\n.Another starts%%%%%\r.";
		assertEquals(expectedResult, actualResult);
	}
}
