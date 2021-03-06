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

public class AddedTypeCMFTest {

	private AddedTypeCMF createFactory(FileSetDelta delta) {
		AddedTypeCMF factory = new AddedTypeCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedStubClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");
		AddedTypeCMF factory = createFactory(builder.build());

		String message = factory.createMessage();
		final String expected = factory.addedStubClassMessage
				.createMessageWithArgs("Test");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedClassMessage
				.createMessageWithArgs("AddedClass");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedInterfaceMessage
				.createMessageWithArgs("MyInterface");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedEnum() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("project1/org/example/MyEnum.java",
				"package org.example;\n\npublic enum MyEnum {\n\n}\n");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedEnumMessage
				.createMessageWithArgs("MyEnum");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedAnnotationType() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/MyFieldAnnotation.java",
				"pacakge org.example\n\n@Target(ElementType.FIELD)\npublic @interface MyFieldAnnotation {}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = factory.addedAnotationMessage
				.createMessageWithArgs("MyFieldAnnotation");
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addAddedFile("project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndRemovedInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addRemovedFile("project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testRemovedInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addRemovedFile("project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testAddedClassAndModifiedInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile(
				"/project1/org/example/AddedClass.java",
				"package org.example;\n\nclass AddedClass { String test() { return \"real value\";}}");
		builder.addChangedFile("project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

	@Test
	public void testModifiedInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("project1/org/example/MyInterface.java",
				"package org.example;\n\ninterface MyInterface {}",
				"package org.example;\n\ninterface MyInterface {int m();}");

		AddedTypeCMF factory = createFactory(builder.build());
		String message = factory.createMessage();
		final String expected = null;
		assertEquals(expected, message);
	}

}
