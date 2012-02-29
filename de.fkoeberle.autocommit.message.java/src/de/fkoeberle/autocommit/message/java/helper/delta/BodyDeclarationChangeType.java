/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java.helper.delta;

public enum BodyDeclarationChangeType {
	/**
	 * The method {@link DeclarationDelta#getChangeTypes()} of classes of type
	 * {@link DeclarationDelta} can return this class to indicate that their
	 * modifiers (like public, private or annotations) changed.
	 */
	MODIFIERS,
	/**
	 * Gets used to describe {@link IDelta} instances with modified, added or
	 * removed javadoc tags.
	 */
	JAVADOC,
	/**
	 * Instances of {@link JavaFileDelta} and {@link TypeDelta} use this
	 * constant to indicate when their declaration list changed.
	 */
	DECLARATION_LIST,
	/**
	 * Instances of {@link TypeDelta} use this constant to indicate that the
	 * super class declaration changed.
	 */
	SUPER_CLASS,
	/**
	 * Instances of {@link TypeDelta} use this constant to indicate that the
	 * interface list changed.
	 */
	SUPER_INTERFACE_LIST,
	/**
	 * Instances of {@link TypeDelta} use this constant to indicate that the
	 * user changed the type of a type declaration. e.g. when the user changes a
	 * enum declaration into an class declaration.
	 */
	TYPE_OF_TYPE,
	/**
	 * Instances of {@link MethodDelta} use this constant to indicate that the
	 * return type changed.
	 */
	RETURN_TYPE,
	/**
	 * Instances of {@link MethodDelta} use this constant to indicate that the
	 * method body changed.
	 */
	METHOD_BODY,
	/**
	 * Instances of {@link MethodDelta} use this constant to indicate that the
	 * methods extra dimensions changed.
	 */
	METHOD_EXTRA_DIMENSIONS,
	/**
	 * Instances of {@link FieldDelta} use this constant to indicate that the
	 * type of the declared field changed.
	 */
	FIELD_TYPE,
	/**
	 * Instances of {@link AnnotationTypeMemberDelta} use this constant to
	 * indicate that the default value changed.
	 */
	ANNOTATION_MEMBER_DEFAULT,
	/**
	 * Instances of {@link AnnotationTypeMemberDelta} use this constant to
	 * indicate that the type of an annotation member changed.
	 */
	ANNOTATION_MEMBER_TYPE,
	/**
	 * Instances of {@link TypeDelta} use this constant to indicate that the
	 * list of declared enum constants changed.
	 */
	ENUM_CONSTANTS,
	/**
	 * Instances of {@link JavaFileDelta} use this constant to indicate that the
	 * list of imports changed.
	 */
	IMPORTS,
	/**
	 * Instances of {@link JavaFileDelta} use this constant to indicate that the
	 * package declaration got added, removed or changed.
	 */
	PACKAGE,
	/**
	 * Instances of {@link PackageDeclationDelta} use this constant to indicate
	 * that the package name changed.
	 */
	PACKAGE_NAME,
	/**
	 * Instances of {@link PackageDeclationDelta} use this constant to indicate
	 * that the package got added or removed and not just changed.
	 */
	PACKAGE_EXISTANCE,
	/**
	 * Instances of {@link PackageDeclationDelta} use this constant to indicate
	 * that the annotation of the package declaration changed.
	 */
	PACKAGE_ANNOTATIONS,
	/**
	 * Instances of {@link EnumConstantDelta} use this constant to indicate that
	 * the arguments of a declared enum constant changed.
	 */
	ENUM_CONSTANTS_ARGUMENTS;
}
