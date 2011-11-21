package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.ProfileDescription;

public class ResetCommitMessageOperation extends AbstractOperation {
	private final ProfileDescription model;
	private final CommitMessagesEditorPart view;
	private final int factoryIndex;
	private final int messageIndex;
	private String oldMessage;

	public ResetCommitMessageOperation(ProfileDescription model,
			CommitMessagesEditorPart view, int factoryIndex, int messageIndex) {
		super("Reset Commit Message");
		this.model = model;
		this.view = view;
		this.factoryIndex = factoryIndex;
		this.messageIndex = messageIndex;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		CommitMessageDescription messageDescription = getCommitDescription();
		this.oldMessage = messageDescription.getCurrentValue();
		messageDescription.reset();
		view.setCommitMessageValue(factoryIndex, messageIndex,
				messageDescription.getCurrentValue());
		return Status.OK_STATUS;
	}

	private CommitMessageDescription getCommitDescription() {
		return model.getMessageDescription(factoryIndex, messageIndex);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		CommitMessageDescription messageDescription = getCommitDescription();
		messageDescription.setCurrentValue(oldMessage);
		view.setCommitMessageValue(factoryIndex, messageIndex, oldMessage);
		return Status.OK_STATUS;
	}
}
