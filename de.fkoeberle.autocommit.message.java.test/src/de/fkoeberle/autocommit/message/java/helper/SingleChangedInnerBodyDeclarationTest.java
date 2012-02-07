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

import java.io.IOException;
import java.util.EnumSet;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.helper.SingleChangedInnerBodyDeclarationView;
import de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType;
import de.fkoeberle.autocommit.message.java.helper.delta.FieldDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.MethodDelta;

public class SingleChangedInnerBodyDeclarationTest {
	private SingleChangedInnerBodyDeclarationView createView(FileSetDelta delta)
			throws IOException {
		Session session = new Session();
		session.add(delta);
		SingleChangedInnerBodyDeclarationView view = session
				.getInstanceOf(SingleChangedInnerBodyDeclarationView.class);
		return view;
	}

	@Test
	public void noChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test {int y;}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals(null, methodDelta);
	}

	@Test
	public void twoChangedMethods() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 0;}\n}",
				"class Test {int x() {\nreturn 1;}\nint y(){return 1;}\n}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals(null, methodDelta);
	}

	@Test
	public void testChangedFirstMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 0;}\n}",
				"class Test {int x() {\nreturn 1;}\nint y(){return 0;}\n}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals("x", methodDelta.getMethodName());
	}

	@Test
	public void testChangedSecondMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 0;}\n}",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals("y", methodDelta.getMethodName());
	}

	@Test
	public void testReducedVisibility() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {public int someMethod() {\nreturn 0;}\n}",
				"class Test {int someMethod() {\nreturn 0;}\n}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals("someMethod", methodDelta.getMethodName());
	}

	@Test
	public void testAddedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\n}",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals(null, methodDelta);
	}

	@Test
	public void testRemovedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}}",
				"class Test {int x() {\nreturn 0;}\n");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals(null, methodDelta);
	}

	@Test
	public void testChangedClassVisiblityToo() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}}}",
				"public class Test {int x() {\nreturn 1;}\n");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals(null, methodDelta);
	}

	@Test
	public void testChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test {long x;");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		FieldDelta delta = (FieldDelta) view.getDelta();
		assertEquals(EnumSet.of(BodyDeclarationChangeType.FIELD_TYPE),
				delta.getChangeTypes());
		assertEquals(null, view.getMethodDelta());
	}

	@Test
	public void testAddedAndChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\n}",
				"class Test {int x() {\nreturn 1;}\nint y(){return 1;}\n}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals(null, methodDelta);
	}

	@Test
	public void testRemovedAndChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}\n}",
				"class Test {int x() {\nreturn 1;}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals(null, methodDelta);
	}

	@Test
	public void testOverloadedMovedAndChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/Test.java",
				"class Test {int x(char c) {\nreturn 0;}\nint x(int i){return 1;}\n}\n}",
				"class Test {int x(int i) {\nreturn 1;}\nint x(char c) {\nreturn 1;}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals("x", methodDelta.getMethodName());
		assertEquals("char", methodDelta.getParameterTypes());
	}

	@Test
	public void testOverloadedAndChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/Test.java",
				"class Test {int x(int j) {\nreturn 1;}\nint x(char c){return 1;}\n}\n}",
				"class Test {int x(int i) {\nreturn 1;}\nint x(char c) {\nreturn 1;}");

		SingleChangedInnerBodyDeclarationView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();
		assertEquals("x", methodDelta.getMethodName());
		assertEquals("int", methodDelta.getParameterTypes());
	}

}
