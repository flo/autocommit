package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.FormattedJavaFileCMF;

public class FormattedJavaFileCMFTest {

	private FormattedJavaFileCMF createFactory(FileSetDelta delta) {
		FormattedJavaFileCMF factory = new FormattedJavaFileCMF();
		Session session = new Session(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedSpacesToInterface() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyInterface.java";
		builder.addChangedFile(path,
				"package org.example;\n\ninterface MyInterface {int  m();}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.formattedJavaFileMessage
				.createMessageWithArgs(path);
		assertEquals(expected, message);
	}

	@Test
	public void testAddedLineBreaksToClass() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(path,
				"package org.example;\n\nclass MyClass {\nint m(){}\n}",
				"package org.example;\n\nclass MyClass {\nint m(){\n}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedJavaFileMessage
				.createMessageWithArgs(path);
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedLineBreakAfterImport() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(
				path,
				"package org.example;\nimport org.example.Test;\nclass MyClass {\nint m(){}\n}",
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nint m(){}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedJavaFileMessage
				.createMessageWithArgs(path);
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testNoChange() {
		FileDeltaBuilder builder = new FileDeltaBuilder();

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedIfStatement() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(
				path,
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){return \"Hello\";}\n}",
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){if (false) return \"Hello\";}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testWhitespaceChangeInString() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(
				path,
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){return \"Hel lo\";}\n}",
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){return \"Hel  lo\";}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testCompareOfFilesWithErrors() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(
				path,
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){return \"Hello\"}\n}",
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){return \"Hello!\"}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testCompareOfFilesWithErrorInOld() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(
				path,
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nvoid m(){System.out.println(\"Hello\")}\n}",
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nvoid m(){}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testCompareOfFilesWithErrorInNew() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(
				path,
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nvoid m(){}\n}",
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nvoid m(){System.out.println(\"Hello\")}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Ignore("The current implementation doesn't support this corner case which isn't important.")
	@Test
	public void testCompareOfFilesWithErrorsWhichAreTheSameExceptWhitespace() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		final String path = "/project1/org/example/MyClass.java";
		builder.addChangedFile(
				path,
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){return \"Hello\"}\n}",
				"package org.example;\nimport org.example.Test;\n\nclass MyClass {\nString m(){return  \"Hello\"}\n}");

		FormattedJavaFileCMF factory = createFactory(builder.build());
		String actualMessage = factory.createMessage();
		final String expectedMessage = factory.formattedJavaFileMessage
				.createMessageWithArgs(path);
		assertEquals(expectedMessage, actualMessage);
	}
}
