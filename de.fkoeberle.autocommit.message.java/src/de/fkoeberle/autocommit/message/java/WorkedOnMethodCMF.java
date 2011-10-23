package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnMethodCMF implements ICommitMessageFactory {

	public final CommitMessageTemplate workedOnMethodWithArgsMessage = new CommitMessageTemplate(
			Translations.WorkedOnMethodCMF_workedOnMethodWithArgs);
	@InjectedBySession
	SingleChangedMethodView singleChangedMethodView;

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
		return workedOnMethodWithArgsMessage.createMessageWithArgs(
				fullTypeName, methodName, parameterTypes, typeName);
	}


}
