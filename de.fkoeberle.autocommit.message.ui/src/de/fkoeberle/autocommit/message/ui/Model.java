package de.fkoeberle.autocommit.message.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

import de.fkoeberle.autocommit.message.CommitMessageBuilderPluginActivator;
import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryXml;
import de.fkoeberle.autocommit.message.CommitMessageTemplateXml;
import de.fkoeberle.autocommit.message.ProfileDescription;
import de.fkoeberle.autocommit.message.ProfileXml;

public class Model {
	private final WritableList usedFactories;;
	private final WritableList unusedFactories;;
	private IEditorInput editorInput;
	private final IUndoContext undoContext;
	private IUndoableOperation undoableOperationAtSave;
	private boolean dirty;
	private final List<IDirtyPropertyListener> dirtyPropertyListenerList = new ArrayList<Model.IDirtyPropertyListener>();
	private final IOperationHistoryListener operationHistoryListener;

	public Model() {
		this.usedFactories = new WritableList(Realm.getDefault(),
				Collections.emptySet(), CommitMessageFactoryDescription.class);
		this.unusedFactories = new WritableList(Realm.getDefault(),
				Collections.emptySet(), CommitMessageFactoryDescription.class);
		this.undoContext = new ObjectUndoContext(this);
		undoableOperationAtSave = null;

		operationHistoryListener = new IOperationHistoryListener() {

			@Override
			public void historyNotification(OperationHistoryEvent event) {
				switch (event.getEventType()) {
				case OperationHistoryEvent.DONE:
				case OperationHistoryEvent.REDONE:
				case OperationHistoryEvent.UNDONE:
				case OperationHistoryEvent.OPERATION_ADDED:
				case OperationHistoryEvent.OPERATION_REMOVED:
					IUndoableOperation undoableOperation = getUndoableOperation();
					setDirty(undoableOperation != undoableOperationAtSave);
				}

			}
		};
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		operationHistory.addOperationHistoryListener(operationHistoryListener);
	}

	public WritableList getFactoryDescriptions() {
		return usedFactories;
	}

	public void load(IEditorInput editorInput) throws IOException {
		if (!(editorInput instanceof IURIEditorInput)) {
			throw new IOException("Input type not supported");
		}
		URL url;
		try {
			url = ((IURIEditorInput) editorInput).getURI().toURL();
		} catch (MalformedURLException e) {
			throw new IOException("Unable to handle input as an url", e);
		}
		ProfileDescription profileDescription = CommitMessageBuilderPluginActivator
				.createProfileDescription(url);
		usedFactories.clear();
		usedFactories.addAll(profileDescription.getFactoryDescriptions());
		unusedFactories.clear();
		unusedFactories.addAll(CommitMessageBuilderPluginActivator
				.findMissingFactories(profileDescription));

		this.editorInput = editorInput;

	}

	public void save(IProgressMonitor monitor) throws IOException {
		if (editorInput instanceof IFileEditorInput) {
			try {
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				monitor.beginTask("Generate data structures to save", 10);
				ProfileXml profileXml = createProfileXml();
				monitor.beginTask("Generate XML to write", 10);
				JAXBContext context = JAXBContext.newInstance(ProfileXml.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				monitor.worked(5);
				marshaller.marshal(profileXml, byteArrayOutputStream);
				monitor.beginTask("Writing data", 90);
				byte[] data = byteArrayOutputStream.toByteArray();
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
						data);
				boolean keepHistory = true;
				boolean force = true;
				fileEditorInput.getFile().setContents(byteArrayInputStream,
						force, keepHistory, monitor);
			} catch (JAXBException e) {
				throw new IOException(e);
			} catch (CoreException e) {
				throw new IOException(e);
			}

			undoableOperationAtSave = getUndoableOperation();
			setDirty(false);
		} else {
			throw new RuntimeException(
					"Saving is not supported for this input type. Editing should not have been possible!");
		}
	}

	private ProfileXml createProfileXml() {
		ProfileXml profileXml = new ProfileXml();

		List<CommitMessageFactoryXml> factories = new ArrayList<CommitMessageFactoryXml>();
		for (Object factoryDescriptionObject : usedFactories) {
			CommitMessageFactoryDescription factoryDescription = (CommitMessageFactoryDescription) factoryDescriptionObject;
			CommitMessageFactoryXml factoryXml = createFactoryXmlFor(factoryDescription);
			factories.add(factoryXml);
		}
		profileXml.setFactories(factories);
		return profileXml;
	}

	private CommitMessageFactoryXml createFactoryXmlFor(
			CommitMessageFactoryDescription factoryDescription) {
		CommitMessageFactoryXml factoryXml = new CommitMessageFactoryXml();
		factoryXml.setId(factoryDescription.getId());
		factoryXml
				.setTemplates(createAnMessageTemplateXmlListFor(factoryDescription));
		return factoryXml;
	}

	private List<CommitMessageTemplateXml> createAnMessageTemplateXmlListFor(
			CommitMessageFactoryDescription factoryDescription) {
		List<CommitMessageDescription> commitMessageDescriptions = factoryDescription
				.getCommitMessageDescriptions();
		List<CommitMessageTemplateXml> messageXmlList = new ArrayList<CommitMessageTemplateXml>(
				commitMessageDescriptions.size());
		for (CommitMessageDescription messageDescription : commitMessageDescriptions) {
			if (messageDescription.isResetPossible()) {
				CommitMessageTemplateXml messageXml = new CommitMessageTemplateXml();
				messageXml
						.setFieldName(messageDescription.getField().getName());
				messageXml.setValue(messageDescription.getCurrentValue());
				messageXmlList.add(messageXml);
			}
		}
		return messageXmlList;
	}

	public boolean isReadOnly() {
		return (editorInput instanceof IFileEditorInput);
	}

	public IUndoContext getUndoContext() {
		return undoContext;
	}

	public void resetMessage(Composite requestSource,
			CommitMessageDescription messageDescription) {
		ResetCommitMessageOperation operation = new ResetCommitMessageOperation(
				messageDescription);
		runOperation(requestSource, operation);
	}

	public void setDirty(boolean newValue) {
		if (this.dirty != newValue) {
			this.dirty = newValue;
			fireDirtyPropertyHasChanged();
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	private void fireDirtyPropertyHasChanged() {
		for (IDirtyPropertyListener listener : dirtyPropertyListenerList) {
			listener.handleDirtyPropertyChange();
		}
	}

	public void addDirtyPropertyListener(IDirtyPropertyListener listener) {
		dirtyPropertyListenerList.add(listener);
	}

	public void removeDirtyPropertyListener(IDirtyPropertyListener listener) {
		dirtyPropertyListenerList.add(listener);
	}

	public void setMessage(Control requestSource,
			CommitMessageDescription messageDescription, String value) {
		SetCommitMessageOperation operation = new SetCommitMessageOperation(
				messageDescription, value);
		runOperation(requestSource, operation);
	}

	private void runOperation(Control requestSource,
			IUndoableOperation operation) {
		operation.addContext(undoContext);
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			MessageDialog.openError(requestSource.getShell(),
					"Failed to Reset", e.getLocalizedMessage());
		}
	}

	private IUndoableOperation getUndoableOperation() {
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		IUndoableOperation undoableOperation = operationHistory
				.getUndoOperation(undoContext);
		return undoableOperation;
	}

	public interface IDirtyPropertyListener {
		void handleDirtyPropertyChange();
	}

	public void dispose() {
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		operationHistory
				.removeOperationHistoryListener(operationHistoryListener);
	}

	public WritableList getUnusedFactoryDescriptions() {
		return unusedFactories;
	}
}
