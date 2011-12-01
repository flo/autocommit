package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;

public class SetCommitMessageOperation extends AbstractOperation {
	private final Model model;
	private final CommitMessageDescription messageDescription;
	private final String newMessage;
	private String oldMessage;
	private ProfileIdResourceAndName oldProfileId;

	public SetCommitMessageOperation(Model model,
			CommitMessageDescription messageDescription, String value) {
		super("Set Commit Message");
		this.model = model;
		this.messageDescription = messageDescription;
		this.newMessage = value;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.oldMessage = messageDescription.getCurrentValue();
		messageDescription.setCurrentValue(newMessage);
		oldProfileId = model.getCurrentProfile();
		model.setCurrentProfileForOperations(Model.CUSTOM_PROFILE);
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
		model.setCurrentProfileForOperations(oldProfileId);
		messageDescription.setCurrentValue(oldMessage);
		return Status.OK_STATUS;
	}

}
