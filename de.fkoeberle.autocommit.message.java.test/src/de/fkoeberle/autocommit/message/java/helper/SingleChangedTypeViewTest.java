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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.helper.SingleChangedTypeView;
import de.fkoeberle.autocommit.message.java.helper.delta.TypeDelta;

public class SingleChangedTypeViewTest {
	private SingleChangedTypeView createView(FileSetDelta delta)
			throws IOException {
		Session session = new Session();
		session.add(delta);
		SingleChangedTypeView view = session
				.getInstanceOf(SingleChangedTypeView.class);
		return view;
	}

	@Test
	public void testChangedOnlyOneDeclarationOfTopLevelClass()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertTrue(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
		assertEquals(null, typeDelta.getOuterTypeName());
	}

	@Test
	public void testAddedSuperClassAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test extends Other {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testRemovedSuperClassAndChangedField()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test  extends Other {int x;}", "class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testChangedSuperClassAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test extends OldClass {int x;}",
				"class Test extends NewClass {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testAddedImplementedInterfaceAndChangedField()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test implements Other {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testRemovedImplementedInterfaceAndChangedField()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test implements Other {int x;}", "class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testChangedImplementedInterfaceAndChangedField()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test implements OldInterface {int x;}",
				"class Test implements NewInterface {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testChangedSecondImplementedInterfaceAndChangedField()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test implements First, OldInterface {int x;}",
				"class Test implements First, NewInterface {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testMadeClassPublicAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"public class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testAddedAnnotationAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"@NewClass class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testRemovedAnnotationAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "@OldClass class Test {int x;}",
				"class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testChangedAnnotationAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "@OldClass class Test {int x;}",
				"@NewClass class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testMadeClassFinalAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"final class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testMadeClassWithAnnotationFinalAndChangedField()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "@First class Test {int x;}",
				"@First final class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testChangedSecondAnnotationAndChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"@First @OldClass class Test {int x;}",
				"@First @NewClass class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertFalse(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testClassWithStuffButOnlyOneChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"@First @Second public final abstract class Test extends One implements IOne, ITwo {int x;}",
				"@First @Second public final abstract class Test extends One implements IOne, ITwo {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertTrue(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Test", typeDelta.getSimpleTypeName());
	}

	@Test
	public void testInnerClasses() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {class It {class Now{int x;}}}",
				"class Test {class It {class Now{int y;}}}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertTrue(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Now", typeDelta.getSimpleTypeName());
		assertEquals("Test.It", typeDelta.getOuterTypeName());
	}

	@Test
	public void testInnerEnum() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {static class It {enum Now{int x;}}}",
				"class Test {static class It {enum Now{int y;}}}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertTrue(typeDelta.isDeclarationListOnlyChange());
		assertEquals("Now", typeDelta.getSimpleTypeName());
		assertEquals("Test.It", typeDelta.getOuterTypeName());
	}

}
