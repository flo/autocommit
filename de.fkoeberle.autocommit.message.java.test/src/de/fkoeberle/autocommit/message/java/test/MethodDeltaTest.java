package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.EnumSet;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;
import de.fkoeberle.autocommit.message.java.BodyDeclarationChangeType;
import de.fkoeberle.autocommit.message.java.MethodDelta;
import de.fkoeberle.autocommit.message.java.SingleChangedMethodView;

public class MethodDeltaTest {
	private SingleChangedMethodView createView(FileSetDelta delta)
			throws IOException {
		Session session = new Session(delta);
		SingleChangedMethodView view = session
				.getInstanceOf(SingleChangedMethodView.class);
		return view;
	}

	@Test
	public void testGetChangeTypesWithChangedBody() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int m() {return 0;};}",
				"class Test {int m() {return 1;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.METHOD_BODY),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithAddedBody() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"abstract class Test {abstract int m();}",
				"abstract class Test {int m() {return 1;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.METHOD_BODY,
				BodyDeclarationChangeType.MODIFIERS),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithRemovedBody() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"abstract class Test {int m() {return 1;};}",
				"abstract class Test {abstract int m();}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.METHOD_BODY,
				BodyDeclarationChangeType.MODIFIERS),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedConstructors() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"abstract class Test {Test() {};}",
				"abstract class Test {Test() {int x;x++;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.METHOD_BODY),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedExtraDimensions()
			throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"abstract class Test {int m() {return null;};}",
				"abstract class Test {int m()[] {return null;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.METHOD_EXTRA_DIMENSIONS),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedReturnType() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"abstract class Test {int m() { return null;};}",
				"abstract class Test {String m() {return null;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.RETURN_TYPE),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithAddedJavaDoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {int m() {return null;};}",
				"class Test {/** new */ int m() {return null;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithRemovedJavaDoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {/** old */ int m() {return null;};}",
				"class Test {int m() {return null;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				methodDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedJavaDoc() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/Test.java",
				"class Test {/** old */ int m() {return null;};}",
				"class Test {/** new */ int m() {return null;};}");

		SingleChangedMethodView view = createView(builder.build());
		MethodDelta methodDelta = view.getMethodDelta();

		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				methodDelta.getChangeTypes());
	}
}
