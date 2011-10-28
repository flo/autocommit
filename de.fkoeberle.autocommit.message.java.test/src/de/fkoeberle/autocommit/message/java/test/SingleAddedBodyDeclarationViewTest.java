package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.SingleAddedBodyDeclarationView;
import de.fkoeberle.autocommit.message.java.TypeUtil;

public class SingleAddedBodyDeclarationViewTest {
	private SingleAddedBodyDeclarationView createView(FileSetDelta delta)
			throws IOException {
		Session session = new Session(delta);
		SingleAddedBodyDeclarationView view = session
				.getInstanceOf(SingleAddedBodyDeclarationView.class);
		return view;
	}

	@Test
	public void testAddedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\n}",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertTrue(addedDeclaration instanceof MethodDeclaration);
		MethodDeclaration methodDeclaration = (MethodDeclaration) addedDeclaration;
		assertEquals("y", TypeUtil.nameOfMethod(methodDeclaration));
	}

	@Test
	public void testAddedFieldAndJavadoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {}",
				"/** Use me! */class Test {int x;}");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testAddedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {\n}",
				"class Test {int x;\n}");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertTrue(addedDeclaration instanceof FieldDeclaration);
		FieldDeclaration fieldDeclaration = (FieldDeclaration) addedDeclaration;
		assertEquals("int",
				TypeUtil.typeRefAsString(fieldDeclaration.getType()));
	}

	@Test
	public void testAddedFieldAndMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {\n}",
				"class Test {int x; int getX(){return x;};\n}");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testAddedTwoFields() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java", "class Test {}",
				"class Test {int x; int y;\n}");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testRemovedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}}",
				"class Test {int x() {\nreturn 0;}\n");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testChangedMethod() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y(){return 0;}\n}}",
				"class Test {int x() {\nreturn 0;}\nint y(){return 1;}\n}}");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}

	@Test
	public void testChangedField() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int x() {\nreturn 0;}\nint y;}\n}}",
				"class Test {int x() {\nreturn 0;}\n@Changed int y;\n}}");

		SingleAddedBodyDeclarationView view = createView(builder.build());
		BodyDeclaration addedDeclaration = view.getAddedDeclaration();
		assertEquals(null, addedDeclaration);
	}
}
