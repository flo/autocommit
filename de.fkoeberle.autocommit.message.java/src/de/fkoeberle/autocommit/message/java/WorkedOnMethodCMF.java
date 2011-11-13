package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnMethodCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate workedOnMethodMessage = new CommitMessageTemplate(
			Translations.WorkedOnMethodCMF_workedOnMethod);

	public final CommitMessageTemplate workedOnConstructorMessage = new CommitMessageTemplate(
			Translations.WorkedOnMethodCMF_workedOnConstructor);

	@InjectedBySession
	SingleChangedBodyDeclarationView singleChangedMethodView;

	@Override
	public String createMessage() throws IOException {
		MethodDelta methodDelta = singleChangedMethodView.getMethodDelta();
		if (methodDelta == null) {
			return null;
		}

		String fullTypeName = methodDelta.getFullTypeName();
		String methodName = methodDelta.getMethodName();
		String parameterTypes = methodDelta.getParameterTypes();
		String typeName = methodDelta.getSimpleTypeName();
		CommitMessageTemplate messageTemplate;
		if (methodDelta.getOldDeclaration().isConstructor()) {
			messageTemplate = workedOnConstructorMessage;
		} else {
			messageTemplate = workedOnMethodMessage;
		}
		return messageTemplate.createMessageWithArgs(
				fullTypeName, methodName, parameterTypes, typeName);
	}


}
