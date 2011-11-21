package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.fkoeberle.autocommit.message.CommitMessageDescription;

public class Controller {
	private final CommitMessagesEditorPart view;

	public Controller(CommitMessagesEditorPart view) {
		this.view = view;
	}

	public void resetMessage(Composite requestSource,
			CommitMessageDescription messageDescription) {
		ResetCommitMessageOperation operation = new ResetCommitMessageOperation(
				messageDescription);
		runOperation(requestSource, operation);
	}

	public void handleLeftFactorySelection(int[] indices) {
		view.setRightFactorySelection(indices);
	}

	public void setMessage(Control requestSource,
			CommitMessageDescription messageDescription, String value) {
		SetCommitMessageOperation operation = new SetCommitMessageOperation(
				messageDescription, value);
		runOperation(requestSource, operation);
	}

	private void runOperation(Control requestSource,
			IUndoableOperation operation) {
		operation.addContext(view.getUndoContext());
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			MessageDialog.openError(requestSource.getShell(),
					"Failed to Reset", e.getLocalizedMessage());
		}
	}
}
