package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;

import de.fkoeberle.autocommit.message.CommitMessageDescription;

public class Controller {
	private final CommitMessagesEditorPart view;
	private final Model model;

	public Controller(CommitMessagesEditorPart view, Model model) {
		this.view = view;
		this.model = model;
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
		operation.addContext(model.getUndoContext());
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			MessageDialog.openError(requestSource.getShell(),
					"Failed to Reset", e.getLocalizedMessage());
		}
	}

	public void initEditor(IEditorSite site, IEditorInput input)
			throws PartInitException {
		try {
			model.load(input);
		} catch (IOException e) {
			reportError("Loading failed. See error log for details", e);
		}
		createUndoAndRedoActionHandlers(site);
	}

	private void createUndoAndRedoActionHandlers(IEditorSite site) {
		IActionBars actionBars = site.getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				new UndoActionHandler(site, model.getUndoContext()));
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				new RedoActionHandler(site, model.getUndoContext()));
	}

	public void save(IProgressMonitor monitor) {
		try {
			model.save(monitor);
		} catch (IOException e) {
			reportError("Saving failed", e);
		}
	}

	private void reportError(String message, Exception e) {
		MessageDialog.openError(view.getEditorSite().getShell(), message,
				NLS.bind("{0}: See error log for details", message));
		Activator
				.getDefault()
				.getLog()
				.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK,
						message, e));
	}
}
