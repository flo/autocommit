/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.DummyCommitMessageUtil;
import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class WorkedOnHeadlineCMFTest {

	private WorkedOnSectionCMF create(FileSetDelta delta) {
		Session session = new Session();
		session.add(delta);
		WorkedOnSectionCMF factory = session
				.getInstanceOf(WorkedOnSectionCMF.class);
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		return factory;
	}

	@Test
	public void testAddedXToTwoSectionsOfAChapter() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\section{Three}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}X\\section{Two}X\\subsection{Hello}\\section{Three}");
		FileSetDelta fileSetDelta = builder.build();

		WorkedOnSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.workedOnChapterMessage
				.createMessageWithArgs("Chapter Two");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedXToSection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{One}\\section{One}\\section{Two}\\subsection{Hello}\\section{Three}",
				"\\chapter{One}\\section{One}\\section{Two}X\\subsection{Hello}\\section{Three}");
		FileSetDelta fileSetDelta = builder.build();

		WorkedOnSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.workedOnSectionMessage
				.createMessageWithArgs("Two");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedXToSubsection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\section{Three}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}X\\section{Three}");
		FileSetDelta fileSetDelta = builder.build();

		WorkedOnSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.workedOnSubsectionMessage
				.createMessageWithArgs("Hello");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedXToSubsubsection() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/hello.tex",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\subsubsection{sub}\\section{Three}",
				"\\chapter{Chapter One}\\chapter{Chapter Two}\\section{One}\\section{Two}\\subsection{Hello}\\subsubsection{sub}X\\section{Three}");
		FileSetDelta fileSetDelta = builder.build();

		WorkedOnSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = factory.workedOnSubsubsectionMessage
				.createMessageWithArgs("sub");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testChangingJavaFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 0;}}}",
				"package org.example;\n\nclass Test {class Inner{/** New Docu*/ int myMethod(String s, int i) {return 0;}}}");
		FileSetDelta fileSetDelta = builder.build();

		WorkedOnSectionCMF factory = create(fileSetDelta);
		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}
}
