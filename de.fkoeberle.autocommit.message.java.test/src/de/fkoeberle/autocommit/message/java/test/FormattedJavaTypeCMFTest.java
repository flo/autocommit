package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.FormattedJavaTypeCMF;

public class FormattedJavaTypeCMFTest {
	private FormattedJavaTypeCMF createFactory(FileSetDelta delta) {
		FormattedJavaTypeCMF factory = new FormattedJavaTypeCMF();
		Session session = new Session(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testFormattedSimpleClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}",
				"package org.example;\n\nclass Test { }");
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

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
		FormattedJavaTypeCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedClassMessage
				.createMessageWithArgs("Test.Middle.Inner");
		assertEquals(expectedMessage, actualMessage);
	}
}
