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

import java.util.EnumSet;

import org.junit.Test;

import de.fkoeberle.autocommit.message.java.helper.delta.BodyDeclarationChangeType;
import de.fkoeberle.autocommit.message.java.helper.delta.DeclarationListDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.FieldDelta;
import de.fkoeberle.autocommit.message.java.helper.delta.TypeDelta;

public class FieldDeltaTest {

	public FieldDelta createFieldDelta(String oldBodyContent,
			String newBodyContent) {
		String oldSource = String.format(
				"package org.example;\n\nclass MainClass {\n%s\n}",
				oldBodyContent);
		String newSource = String.format(
				"package org.example;\n\nclass MainClass {\n%s\n}",
				newBodyContent);
		DeclarationListDelta fileDelta = DeclarationListUtil.createDelta(
				oldSource, newSource);
		assertEquals(0, fileDelta.getAddedDeclarations().size());
		assertEquals(0, fileDelta.getRemovedDeclarations().size());
		assertEquals(1, fileDelta.getChangedDeclarations().size());
		TypeDelta typeDelta = (TypeDelta) (fileDelta.getChangedDeclarations()
				.get(0));
		DeclarationListDelta typeBodyDelta = typeDelta
				.getDeclarationListDelta();
		assertEquals(0, typeBodyDelta.getAddedDeclarations().size());
		assertEquals(0, typeBodyDelta.getRemovedDeclarations().size());
		assertEquals(1, typeBodyDelta.getChangedDeclarations().size());
		FieldDelta fieldDelta = (FieldDelta) (typeBodyDelta
				.getChangedDeclarations().get(0));
		return fieldDelta;
	}

	@Test
	public void testGetChangeTypesWithChangedFieldType() {
		FieldDelta fieldDelta = createFieldDelta("int x;", "String x;");
		assertEquals(EnumSet.of(BodyDeclarationChangeType.FIELD_TYPE),
				fieldDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedModifiers() {
		FieldDelta fieldDelta = createFieldDelta("private int x;",
				"public int x;");
		assertEquals(EnumSet.of(BodyDeclarationChangeType.MODIFIERS),
				fieldDelta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedJavaDoc() {
		FieldDelta fieldDelta = createFieldDelta("/** old*/ int x;",
				"/** new */ int x;");
		assertEquals(EnumSet.of(BodyDeclarationChangeType.JAVADOC),
				fieldDelta.getChangeTypes());
	}

}
