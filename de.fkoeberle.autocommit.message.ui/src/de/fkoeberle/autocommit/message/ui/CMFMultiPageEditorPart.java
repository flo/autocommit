package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;

public class CMFMultiPageEditorPart extends FormEditor {
	public static final String ID = "de.fkoeberle.autocommit.message.ui.CommitMessagesEditorPart"; //$NON-NLS-1$
	private final Model model;

	public CMFMultiPageEditorPart() {
		model = new Model();
	}

	@Override
	public void setFocus() {
		// Set the focus

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			model.save(monitor);
		} catch (IOException e) {
			reportError(getEditorSite().getShell(), "Saving failed", e);
		}
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setSite(site);
		setInput(input);
		try {
			model.load(input);
		} catch (IOException e) {
			reportError(this.getSite().getShell(),
					"Loading failed. See error log for details", e);
		}
		createUndoAndRedoActionHandlers(site);
	}

	@Override
	public boolean isDirty() {
		return model.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void createUndoAndRedoActionHandlers(IEditorSite site) {
		IActionBars actionBars = site.getActionBars();
		// null check is a workaround around a WindowBuilder parser bug
		if (model != null) {
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
					new UndoActionHandler(site, model.getUndoContext()));
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
					new RedoActionHandler(site, model.getUndoContext()));
		}
	}

	public static void reportError(Shell shell, String message, Exception e) {
		MessageDialog.openError(shell, message,
				NLS.bind("{0}: See error log for details", message));
		Activator
				.getDefault()
				.getLog()
				.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK,
						message, e));
	}

	@Override
	protected void addPages() {
		try {
			addPage(new AdvancedPage(this, model));
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}
}