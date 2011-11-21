package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.ProfileDescription;

public class Controller {
	private final ProfileDescription model;
	private final CommitMessagesEditorPart view;

	public Controller(ProfileDescription model, CommitMessagesEditorPart view) {
		this.model = model;
		this.view = view;
	}

	public void resetMessage(Composite requestSource, int factoryIndex,
			int messageIndex) {
		ResetCommitMessageOperation operation = new ResetCommitMessageOperation(
				model, view, factoryIndex, messageIndex);
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			MessageDialog.openError(requestSource.getShell(),
					"Failed to Reset", e.getLocalizedMessage());
		}
	}

	// TODO remove this method:
	@Deprecated
	public CommitMessageDescription getMessageDescription(int factoryIndex,
			int messageIndex) {
		return model.getMessageDescription(factoryIndex, messageIndex);
	}
}
