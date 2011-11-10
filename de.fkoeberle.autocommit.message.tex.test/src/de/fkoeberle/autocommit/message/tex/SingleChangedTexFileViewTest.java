package de.fkoeberle.autocommit.message.tex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileDeltaBuilder;
import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.Session;

public class SingleChangedTexFileViewTest {

	private SingleChangedTexFileView create(FileSetDelta delta) {
		Session session = new Session();
		session.add(delta);
		return session.getInstanceOf(SingleChangedTexFileView.class);
	}

	@Test
	public void testChangedTextFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile("/hello.tex", "\\chapter{One}",
				"\\chapter{One} \\chapter{Two}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedTexFileView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals(1, delta.getOldOutlineNode().getChildNodes().size());
		assertEquals(2, delta.getNewOutlineNode().getChildNodes().size());
	}

	@Test
	public void testChangingJavaFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addChangedFile(
				"/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {class Inner{int myMethod(String s, int i) {return 0;}}}",
				"package org.example;\n\nclass Test {class Inner{/** New Docu*/ int myMethod(String s, int i) {return 0;}}}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedTexFileView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals(null, delta);
	}

	@Test
	public void testAddedTextFile() throws IOException {
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/hello.tex", "\\chapter{One}");

		FileSetDelta fileSetDelta = builder.build();
		SingleChangedTexFileView view = create(fileSetDelta);
		OutlineNodeDelta delta = view.getDelta();
		assertEquals(null, delta);
	}
}
