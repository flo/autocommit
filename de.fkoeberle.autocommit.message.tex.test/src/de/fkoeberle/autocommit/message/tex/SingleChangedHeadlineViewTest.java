package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class SingleChangedHeadlineViewTest {

	private SingleChangedSectionView create(FileSetDelta delta) {
		Session session = new Session();
		session.add(delta);
		return session.getInstanceOf(SingleChangedSectionView.class);
	}

	@Test
	public void testAddedXToTwoSectionsOfAChapter() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\section{Three}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}X\\section{Two}X\\subsection{Hello}\\section{Three}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedSectionView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals(
				"\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\section{Three}",
				delta.getOldOutlineNode().getText());
		assertEquals(
				"\\chapter{Chapter Two}\\section{One}X\\section{Two}X\\subsection{Hello}\\section{Three}",
				delta.getNewOutlineNode().getText());
	}

	@Test
	public void testAddedXToSection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\section{Three}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}X\\subsection{Hello}\\section{Three}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedSectionView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals("\\section{Two}\\subsection{Hello}", delta
				.getOldOutlineNode().getText());
		assertEquals("\\section{Two}X\\subsection{Hello}", delta
				.getNewOutlineNode().getText());
	}

	@Test
	public void testAddedXToSubsection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\section{Three}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}X\\section{Three}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedSectionView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals("\\subsection{Hello}", delta.getOldOutlineNode().getText());
		assertEquals("\\subsection{Hello}X", delta.getNewOutlineNode()
				.getText());
	}

	@Test
	public void testAddedXToSubsubsection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\subsubsection{sub}\\section{Three}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\subsubsection{sub}X\\section{Three}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedSectionView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals("\\subsubsection{sub}", delta.getOldOutlineNode()
				.getText());
		assertEquals("\\subsubsection{sub}X", delta.getNewOutlineNode()
				.getText());
	}

	@Test
	public void testChangingJavaFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 0;}}}",
				"package org.example;\n\nclass Test {class Inner{/** New Docu*/ int myMethod(String s, int i) {return 0;}}}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedSectionView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals(null, delta);
	}
}
