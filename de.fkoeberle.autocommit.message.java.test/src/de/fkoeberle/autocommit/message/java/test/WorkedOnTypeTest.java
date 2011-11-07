package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.WorkedOnTypeCMF;

public class WorkedOnTypeTest {
	private WorkedOnTypeCMF createFactory(FileSetDelta delta) {
		WorkedOnTypeCMF factory = new WorkedOnTypeCMF();
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testWorkedOnClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {int x;}",
				"package org.example;\n\nclass Test {int y;}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnClass
				.createMessageWithArgs("Test");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnInnerClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class MyInnerClass{int x;}}",
				"package org.example;\n\nclass Test {class MyInnerClass{int y;}}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnInnerClass
				.createMessageWithArgs("MyInnerClass", "Test");
		assertEquals(expected, message);
	}

	@Test
	public void testChangedModifierAndInnerClasses() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class MyInnerClass{int x;}}",
				"package org.example;\n\npublic class Test {class MyInnerClass{int y;}}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnClass
				.createMessageWithArgs("Test");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnInnerInnerClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Hello.java",
				"package org.example;\n\nclass Hello {class World{class MyInnerClass{int x;}}}",
				"package org.example;\n\nclass Hello {class World{class MyInnerClass{int y;}}}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnInnerClass
				.createMessageWithArgs("MyInnerClass", "Hello.World");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnEnum() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/MyEnum.java",
				"package org.example;\n\nenum MyEnum {X;}",
				"package org.example;\n\nenum MyEnum {X,Y;}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnEnum
				.createMessageWithArgs("MyEnum");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnInnerEnum() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Hello.java",
				"package org.example;\n\nclass Hello {enum MyEnum {X;}}",
				"package org.example;\n\nclass Hello {enum MyEnum {X,Y;}}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnInnerEnum
				.createMessageWithArgs("MyEnum", "Hello");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnAnnotation() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/MyAnnotation.java",
				"package org.example;\n\n@interface MyAnnotation {}",
				"package org.example;\n\n@interface MyAnnotation {int x();}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnAnnotation
				.createMessageWithArgs("MyAnnotation");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnInnerAnnotation() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Hello.java",
				"package org.example;\n\nclass Hello {@interface MyAnnotation {}}",
				"package org.example;\n\nclass Hello {@interface MyAnnotation {int x();}}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnInnerAnnotation
				.createMessageWithArgs("MyAnnotation", "Hello");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/project1/org/example/Test.java",
				"package org.example;\n\ninterface Test {int x();}",
				"package org.example;\n\ninterface Test {int y();}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnInterface
				.createMessageWithArgs("Test");
		assertEquals(expected, message);
	}

	@Test
	public void testWorkedOnInnerInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Hello.java",
				"package org.example;\n\nclass Hello {interface Test {int x();}}",
				"package org.example;\n\nclass Hello {interface Test {int y();}}");

		WorkedOnTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.workedOnInnerInterface
.createMessageWithArgs(
				"Test", "Hello");
		assertEquals(expected, message);
	}
}
