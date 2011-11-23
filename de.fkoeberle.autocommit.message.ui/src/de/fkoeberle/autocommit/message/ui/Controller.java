package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
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

	public void handleLeftFactorySelection(int[] indices) {
		view.setRightFactorySelection(indices);
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

	public void setMessage(CommitMessageComposite requestSource,
			CommitMessageDescription messageDescription, String value) {
		model.setMessage(requestSource, messageDescription, value);
	}

	public void resetMessage(CommitMessageComposite requestSource,
			CommitMessageDescription messageDescription) {
		model.resetMessage(requestSource, messageDescription);
	}

	public void dispose() {
		model.dispose();
	}
}
