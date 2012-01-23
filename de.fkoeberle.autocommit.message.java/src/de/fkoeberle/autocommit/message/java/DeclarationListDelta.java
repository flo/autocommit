/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.java;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class DeclarationListDelta {
	private final List<BodyDeclaration> addedDeclarations;
	private final List<BodyDeclaration> removedDeclarations;
	private final List<DeclarationDelta<?>> changedDeclarations;



	public DeclarationListDelta(AbstractTypeDeclaration oldType,
			AbstractTypeDeclaration newType) {
		this(oldType.bodyDeclarations(), newType.bodyDeclarations());
	}

	public DeclarationListDelta(CompilationUnit oldCompilationUnit,
			CompilationUnit newCompilationUnit) {
		this(oldCompilationUnit.types(), newCompilationUnit.types());
	}

	public DeclarationListDelta(List<?> oldTypes, List<?> newTypes) {
		// Use a linked hash map so that the list of
		// removed declaration is in declaration order.
		Map<DeclarationId, BodyDeclaration> idToOldVersion = new LinkedHashMap<DeclarationId, BodyDeclaration>(
				oldTypes.size());
		this.addedDeclarations = new ArrayList<BodyDeclaration>();
		this.changedDeclarations = new ArrayList<DeclarationDelta<?>>();
		for (Object declarationObject : oldTypes) {
			BodyDeclaration declaration = (BodyDeclaration) declarationObject;
			DeclarationId id = declarationIdOf(declaration);
			idToOldVersion.put(id, declaration);
		}

		for (Object typeObject : newTypes) {
			BodyDeclaration newDeclaration = (BodyDeclaration) typeObject;
			DeclarationId id = declarationIdOf(newDeclaration);
			BodyDeclaration oldDeclaration = idToOldVersion.remove(id);
			if (oldDeclaration == null) {
				addedDeclarations.add(newDeclaration);
			} else {
				if (!oldDeclaration.subtreeMatch(new ASTMatcher(true),
						newDeclaration)) {
					changedDeclarations.add(createDeclarationDelta(
							oldDeclaration, newDeclaration));
				}
			}
		}
		this.removedDeclarations = new ArrayList<BodyDeclaration>(
				idToOldVersion.values());
	}

	private DeclarationId declarationIdOf(BodyDeclaration declaration) {
		DeclarationId id;
		if (declaration instanceof TypeDeclaration) {
			id = new TypeDeclarationId((TypeDeclaration) declaration);
		} else if (declaration instanceof MethodDeclaration) {
			id = new MethodDeclarationId((MethodDeclaration) declaration);
		} else if (declaration instanceof FieldDeclaration) {
			id = new FieldDeclarationId((FieldDeclaration) declaration);
		} else if (declaration instanceof EnumConstantDeclaration) {
			id = new EnumConstantDeclarationId(
					(EnumConstantDeclaration) declaration);
		} else if (declaration instanceof Initializer) {
			id = new InitializerId((Initializer) declaration);
		} else if (declaration instanceof AnnotationTypeMemberDeclaration) {
			id = new AnnotationTypeMemberDeclarationId(
					(AnnotationTypeMemberDeclaration) declaration);
		} else if (declaration instanceof EnumDeclaration) {
			id = new EnumDeclarationId((EnumDeclaration) declaration);
		} else if (declaration instanceof AnnotationTypeDeclaration) {
			id = new AnnotationTypeDeclarationId(
					(AnnotationTypeDeclaration) declaration);
		} else {
			throw new RuntimeException("Unknown BodyDeclaration: "
					+ declaration);
		}
		return id;
	}

	public List<BodyDeclaration> getAddedDeclarations() {
		return addedDeclarations;
	}

	public List<DeclarationDelta<?>> getChangedDeclarations() {
		return changedDeclarations;
	}

	public List<BodyDeclaration> getRemovedDeclarations() {
		return removedDeclarations;
	}

	private static abstract class DeclarationId {

		@Override
		public abstract boolean equals(Object obj);

		@Override
		public abstract int hashCode();
	}

	private static abstract class AbstractNameBasedDeclarationId extends
			DeclarationId {
		private final String name;

		protected AbstractNameBasedDeclarationId(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			AbstractNameBasedDeclarationId other = (AbstractNameBasedDeclarationId) obj;
			return name.equals(other.name);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static abstract class AbstractTypeDeclarationId extends
	AbstractNameBasedDeclarationId {
		protected AbstractTypeDeclarationId(AbstractTypeDeclaration declaration) {
			super(declaration.getName().getIdentifier());
		}

	}

	private static final class TypeDeclarationId extends
			AbstractTypeDeclarationId {

		public TypeDeclarationId(TypeDeclaration typeDeclaration) {
			super(typeDeclaration);
		}

	}

	private static final class EnumDeclarationId extends
			AbstractTypeDeclarationId {

		public EnumDeclarationId(EnumDeclaration typeDeclaration) {
			super(typeDeclaration);
		}
	}

	private static final class AnnotationTypeDeclarationId extends
			AbstractTypeDeclarationId {

		public AnnotationTypeDeclarationId(
				AnnotationTypeDeclaration typeDeclaration) {
			super(typeDeclaration);
		}
	}

	private static final class MethodDeclarationId extends DeclarationId {
		private final String name;
		private final List<Type> parameters;

		public MethodDeclarationId(MethodDeclaration declaration) {
			this.name = declaration.getName().getIdentifier();
			this.parameters = new ArrayList<Type>();
			List<?> parameterObjects = declaration.parameters();
			for (Object parameterObject : parameterObjects) {
				SingleVariableDeclaration parameter = (SingleVariableDeclaration) parameterObject;
				this.parameters.add(parameter.getType());
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + name.hashCode();
			result = prime * result + parameters.size();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			MethodDeclarationId other = (MethodDeclarationId) obj;
			if (!name.equals(other.name)) {
				return false;
			}
			List<Type> otherParameters = other.parameters;
			if (parameters.size() != otherParameters.size()) {
				return false;
			}
			ASTMatcher matcher = new ASTMatcher(true);
			for (int i = 0; i < parameters.size(); i++) {
				if (!(parameters.get(i).subtreeMatch(matcher,
						otherParameters.get(i)))) {
					return false;
				}
			}
			return true;
		}

	}

	private static final class FieldDeclarationId extends DeclarationId {
		private final List<String> names;

		public FieldDeclarationId(FieldDeclaration declaration) {
			List<?> fragmentObjects = declaration.fragments();
			this.names = new ArrayList<String>(fragmentObjects.size());
			for (Object fragmentObject : fragmentObjects) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragmentObject;
				names.add(fragment.getName().getIdentifier());
			}

		}

		@Override
		public int hashCode() {
			return names.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			FieldDeclarationId other = (FieldDeclarationId) obj;
			if (!names.equals(other.names)) {
				return false;
			}
			return true;
		}

	}

	private static final class AnnotationTypeMemberDeclarationId extends
			AbstractNameBasedDeclarationId {

		public AnnotationTypeMemberDeclarationId(
				AnnotationTypeMemberDeclaration declaration) {
			super(declaration.getName().getIdentifier());
		}

	}

	private static final class EnumConstantDeclarationId extends
			AbstractNameBasedDeclarationId {

		public EnumConstantDeclarationId(EnumConstantDeclaration declaration) {
			super(declaration.getName().getIdentifier());
		}


	}

	private static final class InitializerId extends DeclarationId {
		private final Initializer initializer;
		public InitializerId(Initializer declaration) {
			this.initializer = declaration;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			InitializerId other = (InitializerId) obj;
			return initializer.subtreeMatch(new ASTMatcher(true),
					other.initializer);
		}

		@Override
		public int hashCode() {
			return 0;
		}

	}

	private static DeclarationDelta<?> createDeclarationDelta(
			BodyDeclaration oldDeclaration, BodyDeclaration newDeclaration) {

		if (oldDeclaration instanceof MethodDeclaration) {
			if (!(newDeclaration instanceof MethodDeclaration)) {
				throw new IllegalArgumentException(
						"One declaration is of type MethodDeclaration but not the other");
			}
			MethodDeclaration oldMethod = (MethodDeclaration) oldDeclaration;
			MethodDeclaration newMethod = (MethodDeclaration) newDeclaration;

			return new MethodDelta(oldMethod, newMethod);
		}

		if (oldDeclaration instanceof AbstractTypeDeclaration) {
			if (!(newDeclaration instanceof AbstractTypeDeclaration)) {
				throw new IllegalArgumentException(
						"One declaration is of type AbstractTypeDeclaration but not the other");
			}
			AbstractTypeDeclaration oldType = (AbstractTypeDeclaration) oldDeclaration;
			AbstractTypeDeclaration newType = (AbstractTypeDeclaration) newDeclaration;

			return new TypeDelta(oldType, newType);
		}

		if (oldDeclaration instanceof FieldDeclaration) {
			if (!(newDeclaration instanceof FieldDeclaration)) {
				throw new IllegalArgumentException(
						"One declaration is of type FieldDeclaration but not the other");
			}
			FieldDeclaration oldField = (FieldDeclaration) oldDeclaration;
			FieldDeclaration newField = (FieldDeclaration) newDeclaration;

			return new FieldDelta(oldField, newField);
		}

		if (oldDeclaration instanceof Initializer) {
			if (!(newDeclaration instanceof Initializer)) {
				throw new IllegalArgumentException(
						"One declaration is of type Initializer but not the other");
			}
			Initializer oldInitializer = (Initializer) oldDeclaration;
			Initializer newInitializer = (Initializer) newDeclaration;

			return new InitializerDelta(oldInitializer, newInitializer);
		}

		if (oldDeclaration instanceof EnumConstantDeclaration) {
			if (!(newDeclaration instanceof EnumConstantDeclaration)) {
				throw new IllegalArgumentException(
						"One declaration is of type EnumConstantDeclaration but not the other");
			}
			EnumConstantDeclaration oldEnumConstant = (EnumConstantDeclaration) oldDeclaration;
			EnumConstantDeclaration newEnumConstant = (EnumConstantDeclaration) newDeclaration;

			return new EnumConstantDelta(oldEnumConstant, newEnumConstant);
		}

		if (oldDeclaration instanceof AnnotationTypeMemberDeclaration) {
			if (!(newDeclaration instanceof AnnotationTypeMemberDeclaration)) {
				throw new IllegalArgumentException(
						"One declaration is of type AnnotationTypeMemberDeclaration but not the other");
			}
			AnnotationTypeMemberDeclaration oldAnnotationTypeMember = (AnnotationTypeMemberDeclaration) oldDeclaration;
			AnnotationTypeMemberDeclaration newAnnotationTypeMember = (AnnotationTypeMemberDeclaration) newDeclaration;

			return new AnnotationTypeMemberDelta(oldAnnotationTypeMember,
					newAnnotationTypeMember);
		}

		throw new RuntimeException("Unhandled type");
	}
}
