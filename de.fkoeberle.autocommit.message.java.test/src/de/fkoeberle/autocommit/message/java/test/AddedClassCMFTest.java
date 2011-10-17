package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.AddedClassCMF;

public class AddedClassCMFTest {
	private Session session;

	@Before
	public void initialize() {
		session = new Session();
	}

	@Test
	public void testAddedStubClass() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.addedStubClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClass() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.addedClassMessage
				.createMessageWithArgs("AddedClass");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedInterface() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.addedInterfaceMessage
				.createMessageWithArgs("MyInterface");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedEnum() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/MyEnum.java",
				"package org.example;\n\npublic enum MyEnum {\n\n}\n");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.addedEnumMessage
				.createMessageWithArgs("MyEnum");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedAnnotationType() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/MyFieldAnnotation.java",
				"pacakge org.example\n\n@Target(ElementType.FIELD)\npublic @interface MyFieldAnnotation {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = factory.addedAnotationMessage
				.createMessageWithArgs("MyFieldAnnotation");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndInterface() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addAddedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndRemovedInterface() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addRemovedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testRemovedInterface() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndModifiedInterface() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addModifiedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testModifiedInterface() {
		AddedClassCMF factory = new AddedClassCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addModifiedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		FileSetDelta delta = builder.build();
		String message = factory.createMessageFor(delta, session);
		final String expected = null;
		assertEquals(expected, message);
	}

}
