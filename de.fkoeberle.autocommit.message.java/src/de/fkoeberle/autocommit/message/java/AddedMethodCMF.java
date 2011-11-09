package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class AddedMethodCMF implements ICommitMessageFactory {
	public final CommitMessageTemplate addedMethodMessage = new CommitMessageTemplate(
			Translations.AddedMethodCMF_addedMethod);

	public final CommitMessageTemplate addedConstructorMessage = new CommitMessageTemplate(
			Translations.AddedMethodCMF_addedConstructor);

	@InjectedBySession
	private SingleAddedBodyDeclarationView singleAddedBodyDeclarationView;

	@Override
	public String createMessage() throws IOException {
		BodyDeclaration addedDeclaration = singleAddedBodyDeclarationView
				.getAddedDeclaration();
		if (!(addedDeclaration instanceof MethodDeclaration)) {
			return null;
		}
		MethodDeclaration addedMethod = (MethodDeclaration) addedDeclaration;

		AbstractTypeDeclaration type = (AbstractTypeDeclaration) (addedMethod
				.getParent());

		String fullTypeName = TypeUtil.fullTypeNameOf(type);
		String methodName = TypeUtil.nameOfMethod(addedMethod);
		String parameterTypes = TypeUtil.parameterTypesOf(addedMethod);
		String typeName = TypeUtil.nameOf(type);
		CommitMessageTemplate message;
		if (addedMethod.isConstructor()) {
			message = addedConstructorMessage;
		} else {

			message = addedMethodMessage;
		}

		return message.createMessageWithArgs(fullTypeName,
				methodName, parameterTypes, typeName);
	}

}
