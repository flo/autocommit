package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.AddedClassCMF;

public class AddedClassCMFTest {

	private AddedClassCMF createFactory(FileSetDelta delta) {
		AddedClassCMF factory = new AddedClassCMF();
		Session session = new Session(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedStubClass() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		AddedClassCMF factory = createFactory(builder.build());

		String message = factory.createMessage();
		final String expected = factory.addedStubClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClass() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedClassMessage
				.createMessageWithArgs("AddedClass");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedInterface() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedInterfaceMessage
				.createMessageWithArgs("MyInterface");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedEnum() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/MyEnum.java",
				"package org.example;\n\npublic enum MyEnum {\n\n}\n");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedEnumMessage
				.createMessageWithArgs("MyEnum");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedAnnotationType() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/MyFieldAnnotation.java",
				"pacakge org.example\n\n@Target(ElementType.FIELD)\npublic @interface MyFieldAnnotation {}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedAnotationMessage
				.createMessageWithArgs("MyFieldAnnotation");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndInterface() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addAddedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndRemovedInterface() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addRemovedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testRemovedInterface() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndModifiedInterface() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addModifiedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testModifiedInterface() {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addModifiedFile("/project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		AddedClassCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

}
