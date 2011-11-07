package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.EnumSet;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.BodyDeclarationChangeType;
import de.fkoeberle.autocommit.message.java.SingleChangedTypeView;
import de.fkoeberle.autocommit.message.java.TypeDelta;

public class TypeDeltaTest {
	private SingleChangedTypeView createView(FileSetDelta delta)
			throws IOException {
		Session session = new Session();
		session.add(delta);
		SingleChangedTypeView view = session
				.getInstanceOf(SingleChangedTypeView.class);
		return view;
	}

	@Test
	public void testGetChangeTypesWithOnlyOneChangedBodyDeclaration()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.DECLARATION_LIST),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithOnlyOneChangedBodyDeclarationAndSuperClass()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test extends OldSuperClass {int x;}",
				"class Test extends NewSuperClass {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.DECLARATION_LIST,
				BodyDeclarationChangeType.SUPER_CLASS),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithAddedSuperClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test extends NewSuperClass {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.SUPER_CLASS),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithRemovedSuperClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test  extends Old {int x;}", "class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.SUPER_CLASS),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedInterfaceList() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test implements MyInterface, IOld {int x;}",
				"class Test implements MyInterface, INew {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.SUPER_INTERFACE_LIST),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithAddedInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test implements INew {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.SUPER_INTERFACE_LIST),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithRemovedInterface() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test implements IOld {int x;}", "class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.SUPER_INTERFACE_LIST),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithAddedModifier() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"public class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.MODIFIERS),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithRemovedModifier() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "public class Test {int x;}",
				"class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.MODIFIERS),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedModifier() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "public class Test {int x;}",
				"final class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.MODIFIERS),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithAddedJavaDoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"/** new */ class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				typeDelta.getChangeTypes());
	}
	

	@Test
	public void testGetChangeTypesWithRemovedJavaDoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "/** old */  class Test {int x;}",
				"class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedJavaDoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "/** old */  class Test {int x;}",
				"/** new */ class Test {int x;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				typeDelta.getChangeTypes());
	}

	@Test
	public void testGetFullTypeName() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Outer.java",
				"class Outer { class Inner {int x;}}",
				"class Outer {class Inner {int y;}}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals("Outer.Inner", typeDelta.getFullTypeName());
	}

	@Test
	public void testGetOuterTypeName() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Outer.java",
				"class Outer { class Middle {class Inner {int x;}}}",
				"class Outer {class Middle {class Inner {int y;}}}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals("Outer.Middle", typeDelta.getOuterTypeName());
	}

	@Test
	public void testGetOuterTypeNameWithoutOuterClass() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {int x;}",
				"class Test {int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(null, typeDelta.getOuterTypeName());
	}

	@Test
	public void testGetDeclarationListDelta() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {}",
				"class Test {int x; int y;}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();

		assertEquals(2, typeDelta.getDeclarationListDelta()
				.getAddedDeclarations().size());
		assertEquals(0, typeDelta.getDeclarationListDelta()
				.getRemovedDeclarations().size());
		assertEquals(0, typeDelta.getDeclarationListDelta()
				.getChangedDeclarations().size());

		assertEquals(null, typeDelta.getOuterTypeName());
	}

	@Test
	public void testGetOldDeclaration() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "@One @Two @Three class Test {}",
				"@One @Two class Test {}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertEquals(3, typeDelta.getOldDeclaration().modifiers().size());
	}

	@Test
	public void testGetNewDeclaration() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "@One @Two @Three class Test {}",
				"@One @Two class Test {}");

		SingleChangedTypeView view = createView(builder.build());
		TypeDelta typeDelta = view.getTypeDelta();
		assertEquals(2, typeDelta.getNewDeclaration().modifiers().size());
	}
}
