package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class AddedGettersAndSettersCMF implements ICommitMessageFactory {
	public final CommitMessageTemplate addedGettersMessage = new CommitMessageTemplate(
			Translations.AddedGettersAndSettersCMF_addedGetters);

	public final CommitMessageTemplate addedSettersMessage = new CommitMessageTemplate(
			Translations.AddedGettersAndSettersCMF_addedSetters);

	public final CommitMessageTemplate addedGettersAndSettersMessage = new CommitMessageTemplate(
			Translations.AddedGettersAndSettersCMF_addedGettersAndSetters);

	public final CommitMessageTemplate addedAGetterAndSettersMessage = new CommitMessageTemplate(
			Translations.AddedGettersAndSettersCMF_addedAGetterAndSetters);

	public final CommitMessageTemplate addedGettersAndASetterMessage = new CommitMessageTemplate(
			Translations.AddedGettersAndSettersCMF_addedGettersAndASetter);

	public final CommitMessageTemplate addedAGetterAndSetterMessage = new CommitMessageTemplate(
			Translations.AddedGettersAndSettersCMF_addedAGettersAndSetter);

	@InjectedBySession
	private SingleChangedTypeView singleChangedTypeView;

	@Override
	public String createMessage() throws IOException {
		TypeDelta typeDelta = singleChangedTypeView.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		if (!typeDelta.isDeclarationListOnlyChange()) {
			return null;
		}
		if (typeDelta.getType() != TypeDeltaType.CLASS) {
			return null;
		}
		DeclarationListDelta declarationList = typeDelta
				.getDeclarationListDelta();
		if (declarationList.getRemovedDeclarations().size() != 0) {
			return null;
		}
		if (declarationList.getChangedDeclarations().size() != 0) {
			return null;
		}
		int setterCounter = 0;
		int getterCounter = 0;
		if (declarationList.getAddedDeclarations().size() < 2) {
			return null;
		}
		for (BodyDeclaration declaration : declarationList
				.getAddedDeclarations()) {
			if (!(declaration instanceof MethodDeclaration)) {
				return null;
			}
			MethodDeclaration method = (MethodDeclaration) declaration;
			String methodName = method.getName().getIdentifier();
			Type returnType = method.getReturnType2();
			if (returnType == null) {
				return null;
			}
			if (methodName.startsWith("get")) {
				if (isVoid(returnType)) {
					return null;
				}
				if (method.parameters().size() != 0) {
					return null;
				}
				getterCounter++;
			} else if (methodName.startsWith("set")) {
				if (!isVoid(returnType)) {
					return null;
				}
				if (method.parameters().size() != 1) {
					return null;
				}
				setterCounter++;
			} else {
				return null;
			}
		}
		final String typeName = typeDelta.getFullTypeName();
		if (getterCounter >= 2 && setterCounter >= 2) {
			return addedGettersAndSettersMessage
					.createMessageWithArgs(typeName);
		} else if (getterCounter == 0 && setterCounter >= 2) {
			return addedSettersMessage.createMessageWithArgs(typeName);
		} else if (getterCounter == 2 && setterCounter == 0) {
			return addedGettersMessage.createMessageWithArgs(typeName);
		} else if (getterCounter >= 2 && setterCounter == 1) {
			return addedGettersAndASetterMessage
					.createMessageWithArgs(typeName);
		} else if (getterCounter == 1 && setterCounter >= 2) {
			return addedAGetterAndSettersMessage
					.createMessageWithArgs(typeName);
		} else if (getterCounter == 1 && setterCounter == 1) {
			return addedAGetterAndSetterMessage
					.createMessageWithArgs(typeName);
		} else {
			return null;
		}
	}

	boolean isVoid(Type type) {
		if (type instanceof PrimitiveType) {
			PrimitiveType primitiveType = (PrimitiveType) type;
			if (primitiveType.getPrimitiveTypeCode() == PrimitiveType.VOID) {
				return true;
			}
		}
		return false;
	}

}
