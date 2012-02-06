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
import de.fkoeberle.autocommit.message.java.factories.FormattedTypeCMF;

public class FormattedJavaTypeCMFTest {
	private FormattedTypeCMF createFactory(FileSetDelta delta) {
		FormattedTypeCMF factory = new FormattedTypeCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testFormattedSimpleClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package org.example;\n\nclass Test { }");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedSimpleEnum() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nenum Test {}",
				"package org.example;\n\nenum Test { }");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedEnumMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedSimpleAnnotation() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\n@interface Test {}",
				"package org.example;\n\n@interface Test { }");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedAnnotationMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedSimpleInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\ninterface Test {}",
				"package org.example;\n\ninterface Test { }");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedInterfaceMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedPackageWithOneType() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package  org.example;\n\nclass Test {}");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedPackageWithTwoTypes() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {} class Other{}",
				"package  org.example;\n\nclass Test {} class Other{}");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedMainWithTwoTopLevelClasses() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {} class Second {}",
				"package org.example;\n\nclass Test { } class Second {}");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedMainAsSecondTopLevelClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass First{} class Test {}",
				"package org.example;\n\nclass First{} class Test { }");
		FormattedTypeCMF factory = createFactory(builder.build());

		String expectedMessage = factory.createMessage();
		final String actualMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedOtherTopLevelClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass First{ } class Test {}",
				"package org.example;\n\nclass First{} class Test {}");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("First");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedFirstInnerClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class First { } class Second{}}",
				"package org.example;\n\nclass Test {class First {} class Second{}}");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test.First");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedSecondInnerClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class First {} class Second{int x;}}",
				"package org.example;\n\nclass Test {class First {} class Second{int  x;}}");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test.Second");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testFormattedInnerInnerClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Middle {class Inner{}}}",
				"package org.example;\n\nclass Test {class Middle {class Inner{\n}}}");
		FormattedTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test.Middle.Inner");
		assertEquals(expectedMessage, actualMessage);
	}
}
