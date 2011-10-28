package de.fkoeberle.autocommit.message.java;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.fkoeberle.autocommit.message.CommitMessage;
import de.fkoeberle.autocommit.message.CommitMessageTemplate;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.InjectedBySession;

public class AddedConstructorCMF implements ICommitMessageFactory {

	@CommitMessage
	public final CommitMessageTemplate addedConstructorMessage = new CommitMessageTemplate(
			Translations.AddedConstructorCMF_addedConstructor);

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
		if (!addedMethod.isConstructor()) {
			return null;
		}
		AbstractTypeDeclaration type = (AbstractTypeDeclaration) (addedMethod
				.getParent());

		String fullTypeName = TypeUtil.fullTypeNameOf(type);
		return addedConstructorMessage.createMessageWithArgs(fullTypeName);
	}

}
