package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class WorkedOnTypeCMF implements ICommitMessageFactory {

	@CommitMessage
	public final CommitMessageTemplate workedOnClass = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnClass);

	@CommitMessage
	public final CommitMessageTemplate workedOnInterface = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnInterface);

	@CommitMessage
	public final CommitMessageTemplate workedOnEnum = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnEnum);

	@CommitMessage
	public final CommitMessageTemplate workedOnAnnotation = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnAnnotation);

	@CommitMessage
	public final CommitMessageTemplate workedOnInnerClass = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnInnerClass);

	@CommitMessage
	public final CommitMessageTemplate workedOnInnerInterface = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnInnerInterface);

	@CommitMessage
	public final CommitMessageTemplate workedOnInnerEnum = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnInnerEnum);

	@CommitMessage
	public final CommitMessageTemplate workedOnInnerAnnotation = new CommitMessageTemplate(
			Translations.WorkedOnTypeCMF_workedOnInnerAnnotation);

	@InjectedBySession
	private SingleChangedTypeView view;

	@Override
	public String createMessage() throws IOException {
		TypeDelta typeDelta = view.getTypeDelta();
		if (typeDelta == null) {
			return null;
		}
		AbstractTypeDeclaration oldType = typeDelta.getOldDeclaration();

		String typeName = typeDelta.getSimpleTypeName();
		String fullOuterTypeName = typeDelta.getOuterTypeName();
		if (oldType instanceof TypeDeclaration) {
			TypeDeclaration typeDeclation = (TypeDeclaration) oldType;
			if (typeDeclation.isInterface()) {
				if (fullOuterTypeName != null) {
					return workedOnInnerInterface.createMessageWithArgs(
							typeName, fullOuterTypeName);
				} else {
					return workedOnInterface.createMessageWithArgs(typeName);
				}
			} else {
				if (fullOuterTypeName != null) {
					return workedOnInnerClass.createMessageWithArgs(typeName,
							fullOuterTypeName);
				} else {
					return workedOnClass.createMessageWithArgs(typeName);
				}
			}

		} else if (oldType instanceof EnumDeclaration) {
			if (fullOuterTypeName != null) {
				return workedOnInnerEnum.createMessageWithArgs(typeName,
						fullOuterTypeName);
			} else {
				return workedOnEnum.createMessageWithArgs(typeName);
			}
		} else if (oldType instanceof AnnotationTypeDeclaration) {
			if (fullOuterTypeName != null) {
				return workedOnInnerAnnotation.createMessageWithArgs(typeName,
						fullOuterTypeName);
			} else {
				return workedOnAnnotation.createMessageWithArgs(typeName);
			}

		} else {
			return null;
		}

	}

}
