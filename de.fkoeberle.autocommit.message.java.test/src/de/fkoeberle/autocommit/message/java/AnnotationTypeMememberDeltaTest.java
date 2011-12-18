package de.fkoeberle.autocommit.message.java;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;

import org.junit.Test;

import de.fkoeberle.autocommit.message.java.AnnotationTypeMemberDelta;
import de.fkoeberle.autocommit.message.java.BodyDeclarationChangeType;
import de.fkoeberle.autocommit.message.java.DeclarationListDelta;
import de.fkoeberle.autocommit.message.java.TypeDelta;

public class AnnotationTypeMememberDeltaTest {

	public AnnotationTypeMemberDelta createFieldDelta(String oldBodyContent,
			String newBodyContent) {
		String oldSource = String.format(
				"package org.example;\n\n@interface MyAnnotation {\n%s\n}",
				oldBodyContent);
		String newSource = String.format(
				"package org.example;\n\n@interface MyAnnotation {\n%s\n}",
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
		AnnotationTypeMemberDelta fieldDelta = (AnnotationTypeMemberDelta) (typeBodyDelta
				.getChangedDeclarations().get(0));
		return fieldDelta;
	}

	@Test
	public void testGetChangeTypesWithChangedType() {
		AnnotationTypeMemberDelta delta = createFieldDelta("int id();",
				"String id();");
		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.ANNOTATION_MEMBER_TYPE),
				delta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithChangedDefault() {
		AnnotationTypeMemberDelta delta = createFieldDelta(
				"String name() default \"old\";",
				"String name() default \"new\";");
		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.ANNOTATION_MEMBER_DEFAULT),
				delta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithAddedDefault() {
		AnnotationTypeMemberDelta delta = createFieldDelta("String name();",
				"String name() default \"new\";");
		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.ANNOTATION_MEMBER_DEFAULT),
				delta.getChangeTypes());
	}

	@Test
	public void testGetChangeTypesWithRemovedDefault() {
		AnnotationTypeMemberDelta delta = createFieldDelta(
				"String name() default \"old\";", "String name();");
		assertEquals(
				EnumSet.of(BodyDeclarationChangeType.ANNOTATION_MEMBER_DEFAULT),
				delta.getChangeTypes());
	}
}
