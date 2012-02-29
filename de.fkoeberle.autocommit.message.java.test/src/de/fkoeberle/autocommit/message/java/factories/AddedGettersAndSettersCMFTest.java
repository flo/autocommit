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

public class AddedGettersAndSettersCMFTest {

	private AddedGettersAndSettersCMF createFactory(FileSetDelta delta) {
		AddedGettersAndSettersCMF factory = new AddedGettersAndSettersCMF();
		DummyCommitMessageUtil.insertUniqueCommitMessagesWithNArgs(factory, 1);
		Session session = new Session();
		session.add(delta);
		session.injectSessionData(factory);
		return factory;
	}

	@Test
	public void testAddedTwoGetters() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n"
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedGettersMessage
				.createMessageWithArgs("Test.Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoSetters() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n"
						+ "void setId(int id) {this.id = id;}\n"
						+ "void setName(String name) {this.name = name;}}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedSettersMessage
				.createMessageWithArgs("Test.Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedGettersAndSetters() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n" + ""
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n"
						+ "void setId(int id) {this.id = id;}\n"
						+ "void setName(String name) {this.name = name;}}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedGettersAndSettersMessage
				.createMessageWithArgs("Test.Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedAGetterAndTwoSetters() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n" + ""
						+ "int getId() {return id;}\n"
						+ "void setId(int id) {this.id = id;}\n"
						+ "void setName(String name) {this.name = name;}}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedAGetterAndSettersMessage
				.createMessageWithArgs("Test.Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedGettersAndASetter() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n" + ""
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n"
						+ "void setName(String name) {this.name = name;}}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedGettersAndASetterMessage
				.createMessageWithArgs("Test.Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedAGettersAndSetter() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n" + ""
						+ "String getName() {return name;}\n"
						+ "void setName(String name) {this.name = name;}}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = factory.addedAGetterAndSetterMessage
				.createMessageWithArgs("Test.Inner");
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGettersOneWithOneParameter() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n"
						+ "int getId(int v) {return id;}\n"
						+ "String getName() {return name;}\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGettersOneWithNoReturnValue() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n" + "void getId() {}\n"
						+ "String getName() {return name;}\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGetterAndOneConstructor() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n" + "Inner() {}\n"
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGetterAndOneOtherGetterLikeMethod()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n"
						+ "int size() {return 1;}\n"
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoSettersOneSetterWithoutArgument()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n"
						+ "void setId(int id) {this.id = id;}\n"
						+ "void setToDefault() {this.id = 0;}\n"
						+ "void setName(String name) {this.name = name;}}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoSettersAndOneSetterOneWithReturnValue()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n"
						+ "String name;\n"
						+ "String other;\n"
						+ "void setId(int id) {this.id = id;}\n"
						+ "String setOther(String other) {this.other = other; return other;}\n"
						+ "void setName(String name) {this.name = name;}}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGettersToEnum() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {enum Inner{ONE,TWO; int id; String name;}}",
				"package org.example;\n\nclass Test {enum Inner{ONE,TWO; \n"
						+ "int id;\n" + "String name;\n"
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGettersToInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("project1/org/example/Test.java",
				"package org.example;\n\nclass Test {interface Inner{}}",
				"package org.example;\n\nclass Test {interface Inner{"
						+ "int getId();\n" + "String getName();\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGettersAndModifierToClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\npublic class Test {class Inner{\n"
						+ "int id;\n" + "String name;\n"
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n" + "}}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testAddedTwoGettersAndAddedTopLevelClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int id; String name;}}",
				"package org.example;\n\nclass Test {class Inner{\n"
						+ "int id;\n" + "String name;\n"
						+ "int getId() {return id;}\n"
						+ "String getName() {return name;}\n" + "}}\n"
						+ "class Other{}");
		AddedGettersAndSettersCMF factory = createFactory(builder.build());

		String actualMessage = factory.createMessage();
		String expectedMessage = null;
		assertEquals(expectedMessage, actualMessage);
	}
}
