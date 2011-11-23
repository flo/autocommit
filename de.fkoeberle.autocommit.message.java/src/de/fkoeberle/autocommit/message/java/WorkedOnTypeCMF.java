package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnTypeCMF implements ICommitMessageFactory {

	@CommitMessage
	public final CommitMessageTemplate workedOnClassMessage = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnClass);

	@CommitMessage
	public final CommitMessageTemplate workedOnInterfaceMessage = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnInterface);

	@CommitMessage
	public final CommitMessageTemplate workedOnEnumMessage = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnEnum);

	@CommitMessage
	public final CommitMessageTemplate workedOnAnnotationMessage = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnAnnotation);

	@InjectedBySession
	private SingleChangedTypeView view;

	@Override
	public String createMessage() throws IOException {
		TypeDelta typeDelta = view.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		CommitMessageTemplate messageTemplate = getMessageTemplateFor(typeDelta);
		String simpleName = typeDelta.getSimpleTypeName();
		String fullName = typeDelta.getSimpleTypeName();
		return messageTemplate.createMessageWithArgs(simpleName, fullName);
	}

	CommitMessageTemplate getMessageTemplateFor(TypeDelta typeDelta) {
		switch (typeDelta.getType()) {
		case CLASS:
			return workedOnClassMessage;
		case INTERFACE:
			return workedOnInterfaceMessage;
		case ENUM:
			return workedOnEnumMessage;
		case ANNOTATION:
			return workedOnAnnotationMessage;
		default:
			throw new RuntimeException("Unhandled type");
		}
	}
}
