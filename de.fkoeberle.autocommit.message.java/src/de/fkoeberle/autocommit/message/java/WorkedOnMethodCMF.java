package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnMethodCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate workedOnMethodWithArgsMessage = new CommitMessageTemplate(
			Translations.WorkedOnMethodCMF_workedOnMethodWithArgs);
	@InjectedBySession
	SingleChangedTypeView singleChangedTypeView;

	@Override
	public String createMessage() throws IOException {
		TypeDelta typeDelta = singleChangedTypeView.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		if (!typeDelta.isDeclarationListOnlyChange()) {
			return null;
		}
		DeclarationListDelta declarationListDelta = typeDelta
				.getDeclarationListDelta();
		if (declarationListDelta.getAddedDeclarations().size() != 0) {
			return null;
		}
		if (declarationListDelta.getRemovedDeclarations().size() != 0) {
			return null;
		}
		if (declarationListDelta.getChangedDeclarations().size() != 1) {
			return null;
		}
		DeclarationDelta declarationDelta = declarationListDelta
				.getChangedDeclarations().get(0);
		BodyDeclaration oldDeclaration = declarationDelta.getOldDeclaration();
		BodyDeclaration newDeclaration = declarationDelta.getNewDeclaration();
		if (!(oldDeclaration instanceof MethodDeclaration)) {
			return null;
		}
		assert newDeclaration instanceof MethodDeclaration : "since it has same type as oldDeclaration based on how delta lists get created"; //$NON-NLS-1$
		MethodDeclaration oldMethodDeclaration = (MethodDeclaration) oldDeclaration;
		MethodDeclaration newMethodDeclaration = (MethodDeclaration) newDeclaration;

		String fullTypeName = typeDelta.getFullTypeName();
		String methodName = nameOf(newMethodDeclaration);
		String parameterTypes = parameterTypesOf(newMethodDeclaration);
		String typeName = typeDelta.getSimpleTypeName();
		return workedOnMethodWithArgsMessage.createMessageWithArgs(
				fullTypeName, methodName, parameterTypes, typeName);
	}

	private static String nameOf(MethodDeclaration methodDeclaration) {
		return methodDeclaration.getName().getIdentifier();
	}

	private static String parameterTypesOf(MethodDeclaration methodDeclaration) {
		StringBuilder builder = new StringBuilder();

		boolean addComma = false;
		for (Object parameterObject : methodDeclaration.parameters()) {
			if (addComma) {
				builder.append(',');
			} else {
				addComma = true;
			}
			SingleVariableDeclaration parameter = (SingleVariableDeclaration) parameterObject;
			Type type = parameter.getType();
			appendTypeTo(type, builder);
		}
		return builder.toString();
	}

	/**
	 * @return a string version of the specified type or another string
	 *         indicating an error.
	 */
	private static void appendTypeTo(Type type, StringBuilder builder) {
		if (type instanceof SimpleType) {
			SimpleType simpleType = (SimpleType) type;
			builder.append(simpleType.getName().getFullyQualifiedName());
		} else if (type instanceof PrimitiveType) {
			PrimitiveType primitiveType = (PrimitiveType) type;
			Code code = primitiveType.getPrimitiveTypeCode();
			builder.append(code.toString());
		} else if (type instanceof ArrayType) {
			ArrayType arrayType = (ArrayType) type;
			appendTypeTo(arrayType.getComponentType(), builder);
			builder.append("[]"); //$NON-NLS-1$
		} else if (type instanceof QualifiedType) {
			QualifiedType qualifiedType = (QualifiedType) type;
			builder.append(qualifiedType.getQualifier());
			builder.append('.');
			builder.append(qualifiedType.getName().getIdentifier());
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			builder.append(parameterizedType.getType());
			builder.append("<"); //$NON-NLS-1$
			@SuppressWarnings("unchecked")
			List<Type> typeArguments = parameterizedType.typeArguments();
			for (Type typeArg : typeArguments) {
				appendTypeTo(typeArg, builder);
			}
			builder.append(">"); //$NON-NLS-1$
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type bound = wildcardType.getBound();
			if (bound != null) {
				if (wildcardType.isUpperBound()) {
					builder.append("? extends "); //$NON-NLS-1$
				} else {
					builder.append("? super "); //$NON-NLS-1$
				}
				appendTypeTo(bound, builder);
			} else {
				builder.append("?"); //$NON-NLS-1$
			}
		} else {
			assert false : "Expected no other types"; //$NON-NLS-1$
		}
	}
}
