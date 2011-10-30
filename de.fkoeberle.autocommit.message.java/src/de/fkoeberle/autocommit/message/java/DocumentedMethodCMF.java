package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.EnumSet;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class DocumentedMethodCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate documentedMethodMessage = new CommitMessageTemplate(
			Translations.DocumentedMethodCMF_documentedMethod);
	@InjectedBySession
	SingleChangedMethodView singleChangedMethodView;

	@Override
	public String createMessage() throws IOException {
		MethodDelta methodDelta = singleChangedMethodView.getMethodDelta();
		if (methodDelta == null) {
			return null;
		}
		if (!methodDelta.getChangeTypes().equals(
				EnumSet.of(BodyDeclarationChangeType.JAVADOC))) {
			return null;
		}

		String fullTypeName = methodDelta.getFullTypeName();
		String methodName = methodDelta.getMethodName();
		String parameterTypes = methodDelta.getParameterTypes();
		String typeName = methodDelta.getSimpleTypeName();
		return documentedMethodMessage.createMessageWithArgs(fullTypeName,
				methodName, parameterTypes, typeName);
	}
}
