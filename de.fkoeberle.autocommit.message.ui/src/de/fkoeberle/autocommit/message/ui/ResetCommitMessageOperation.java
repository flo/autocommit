package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;

public class ResetCommitMessageOperation extends AbstractOperation {
	private final Model model;
	private final CommitMessageDescription messageDescription;
	private String oldMessage;
	private ProfileIdResourceAndName oldProfileId;

	public ResetCommitMessageOperation(Model model,
			CommitMessageDescription messageDescription) {
		super("Reset Commit Message");
		this.model = model;
		this.messageDescription = messageDescription;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		oldProfileId = model.getCurrentProfile();
		model.setCurrentProfileForOperations(Model.CUSTOM_PROFILE);
		this.oldMessage = messageDescription.getCurrentValue();
		messageDescription.reset();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		messageDescription.setCurrentValue(oldMessage);
		model.setCurrentProfileForOperations(oldProfileId);
		return Status.OK_STATUS;
	}
}
