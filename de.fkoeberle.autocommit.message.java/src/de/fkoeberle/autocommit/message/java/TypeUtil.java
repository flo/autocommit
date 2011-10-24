package de.fkoeberle.autocommit.message.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;

public class TypeUtil {

	public static String parameterTypesOf(MethodDeclaration methodDeclaration) {
		StringBuilder builder = new StringBuilder();

		boolean addComma = false;
		for (Object parameterObject : methodDeclaration.parameters()) {
			if (addComma) {
				builder.append(", ");
			} else {
				addComma = true;
			}
			SingleVariableDeclaration parameter = (SingleVariableDeclaration) parameterObject;
			Type type = parameter.getType();
			appendTypeTo(type, builder);
			if (parameter.isVarargs()) {
				builder.append("...");
			}
		}
		return builder.toString();
	}

	public static String nameOfMethod(MethodDeclaration methodDeclaration) {
		return methodDeclaration.getName().getIdentifier();
	}

	/**
	 * @return a string version of the specified type or another string
	 *         indicating an error.
	 */
	public static void appendTypeTo(Type type, StringBuilder builder) {
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
			boolean addComma = false;
			for (Type typeArg : typeArguments) {
				if (addComma) {
					builder.append(", ");
				} else {
					addComma = true;
				}
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

	public static String outerTypeNameOf(AbstractTypeDeclaration type) {
		ASTNode parent = type.getParent();
		if (!(parent instanceof AbstractTypeDeclaration)) {
			return null;
		}
		return fullTypeNameOf((AbstractTypeDeclaration) parent);
	}

	public static String fullTypeNameOf(AbstractTypeDeclaration type) {
		ASTNode currentType = type;
		List<String> reversedTypeParts = new ArrayList<String>();
		while (currentType instanceof AbstractTypeDeclaration) {
			reversedTypeParts
					.add(nameOf((AbstractTypeDeclaration) currentType));
			currentType = currentType.getParent();
		}
		int length = 0;
		for (String part : reversedTypeParts) {
			length += part.length();
		}
		length += reversedTypeParts.size() - 1;
		StringBuilder stringBuilder = new StringBuilder(length);
		for (int i = reversedTypeParts.size() - 1; i >= 0; i--) {
			stringBuilder.append(reversedTypeParts.get(i));
			if (i != 0) {
				stringBuilder.append('.');
			}
		}
		String result = stringBuilder.toString();
		assert (result.length() == length);
		return result;
	}

	public static String nameOf(AbstractTypeDeclaration typeDeclation) {
		return typeDeclation.getName().getIdentifier();
	}
}
