package de.fkoeberle.autocommit.message.java.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fkoeberle.autocommit.message.FileSetDelta;
import de.fkoeberle.autocommit.message.java.WorkedOnPackageCMF;

public class WorkedOnPackageCMFTest {

	@Test
	public void testBuild() {
		WorkedOnPackageCMF factory = new WorkedOnPackageCMF();
		FileDeltaBuilder builder = new FileDeltaBuilder();
		builder.addAddedFile("/project1/org/example/Test.java",
				"package org.example;\n\nclass Test {}");

		FileSetDelta delta = builder.build();
		String message = factory.build(delta);
		assertEquals("Worked on package org.example", message);
	}

}
