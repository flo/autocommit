/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class SingleAddedInnerBodyDeclarationViewTest {
	private SingleAddedInnerBodyDeclarationView createView(FileSetDelta delta)
			throws IOException {
		Session session = new Session();
		session.add(delta);
		SingleAddedInnerBodyDeclarationView view = session
				.getInstanceOf(SingleAddedInnerBodyDeclarationView.class);
		return view;
	}

	@Test
	public void testAddedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java",
				"class Test {int x() {\nreturn 0;}\n}",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertTrue(addedDeclaration instanceof MethodDeclaration);
		MethodDeclaration methodDeclaration = (MethodDeclaration) addedDeclaration;
		assertEquals("y", TypeUtil.nameOfMethod(methodDeclaration));
	}

	@Test
	public void testAddedFieldAndJavadoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java", "class Test {}",
				"/** Use me! */class Test {int x;}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testAddedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java", "class Test {\n}",
				"class Test {int x;\n}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertTrue(addedDeclaration instanceof FieldDeclaration);
		FieldDeclaration fieldDeclaration = (FieldDeclaration) addedDeclaration;
		assertEquals("int",
				TypeUtil.typeRefAsString(fieldDeclaration.getType()));
	}

	@Test
	public void testAddedFieldAndMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java", "class Test {\n}",
				"class Test {int x; int getX(){return x;};\n}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testAddedTwoFields() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java", "class Test {}",
				"class Test {int x; int y;\n}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testRemovedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}}",
				"class Test {int x() {\nreturn 0;}\n");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 0;}\n}}",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java",
				"class Test {int x() {\nreturn 0;}\nint y;}\n}}",
				"class Test {int x() {\nreturn 0;}\n@Changed int y;\n}}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testAddedAnInitializer() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java", "class Test {static int i;}",
				"class Test {static int i;  static {i = 1;}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();

		assertEquals(Initializer.class, addedDeclaration.getClass());
	}

	@Test
	public void testAddedAnInitializerAndChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java",
				"class Test {static int i;} int m() {return 0;}",
				"class Test {static int i;  static {i = 1;} int m() {return 1;}");

		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		assertEquals(null, view.getAddedDeclaration());
	}

	@Test
	public void testChangedTwoMethods() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("Test.java",
				"class Test {int m1() {return 0;} int m2() {return 0;}}",
				"class Test {int m1() {return 1;} int m2() {return 1;}} ");
		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		assertEquals(null, view.getAddedDeclaration());
	}

	@Test
	public void testChangedTwoFiles() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("TestA.java",
				"class Test {int m1() {return 0;}}",
				"class Test {int m1() {return 1;}} ");
		builder.addChangedFile("TestB.java",
				"class Test {int m2() {return 0;}}",
				"class Test {int m2() {return 1;}} ");
		SingleAddedInnerBodyDeclarationView view = createView(builder.build());
		assertEquals(null, view.getAddedDeclaration());
	}

}
