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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

import de.fkoeberle.autocommit.message.CommitMessageBuilderPluginActivator;
import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryXml;
import de.fkoeberle.autocommit.message.CommitMessageTemplateXml;
import de.fkoeberle.autocommit.message.ProfileDescription;
import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;
import de.fkoeberle.autocommit.message.ProfileReferenceXml;
import de.fkoeberle.autocommit.message.ProfileXml;

public class Model {
	public static final ProfileIdResourceAndName CUSTOM_PROFILE = new ProfileIdResourceAndName(
			null, null, "Custom commit messages");
	private final WritableList usedFactories;;
	private final WritableList unusedFactories;;
	private IEditorInput editorInput;
	private final IUndoContext undoContext;
	private IUndoableOperation undoableOperationAtSave;
	private boolean dirty;
	private final List<IDirtyPropertyListener> dirtyPropertyListenerList = new ArrayList<Model.IDirtyPropertyListener>();
	private final List<ICurrentProfileListener> currentProfileListenerList = new ArrayList<Model.ICurrentProfileListener>();
	private final IOperationHistoryListener operationHistoryListener;
	private final WritableList profiles;;
	private ProfileIdResourceAndName currentProfile;
	private final JAXBContext jaxbContext;

	public Model() {
		this.usedFactories = new WritableList(Realm.getDefault(),
				Collections.emptySet(), CommitMessageFactoryDescription.class);
		this.unusedFactories = new WritableList(Realm.getDefault(),
				Collections.emptySet(), CommitMessageFactoryDescription.class);
		this.profiles = new WritableList(Realm.getDefault(),
				Collections.emptySet(), ProfileDescription.class);
		profiles.addAll(CommitMessageBuilderPluginActivator
				.getDefaultProfiles());
		profiles.add(CUSTOM_PROFILE);
		this.currentProfile = CUSTOM_PROFILE;

		this.undoContext = new ObjectUndoContext(this);
		try {
			this.jaxbContext = JAXBContext.newInstance(ProfileXml.class,
					ProfileReferenceXml.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

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
		loadFactoriesFor(profileDescription);
		String profileId = profileDescription.getDefaultProfileId();
		if (profileId == null) {
			setCurrentProfileForOperations(CUSTOM_PROFILE);
		} else {
			for (Object profileObject : profiles) {
				ProfileIdResourceAndName profile = (ProfileIdResourceAndName) profileObject;
				if (profileId.equals(profile.getId())) {
					setCurrentProfileForOperations(profile);
				}
			}
		}
		this.editorInput = editorInput;
	}

	/**
	 * Should NOT be called from UI classes. It's accessible for operations
	 * 
	 * @param profileDescription
	 */
	void loadFactoriesFor(ProfileDescription profileDescription) {
		usedFactories.clear();
		usedFactories.addAll(profileDescription.getFactoryDescriptions());
		unusedFactories.clear();
		unusedFactories.addAll(CommitMessageBuilderPluginActivator
				.findMissingFactories(profileDescription));
	}

	public void save(IProgressMonitor monitor) throws IOException {
		if (editorInput instanceof IFileEditorInput) {
			try {
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				monitor.beginTask("Generating data structures to save", 10);

				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				Object objectToMarshall;
				if (currentProfile == CUSTOM_PROFILE) {
					objectToMarshall = createProfileXml();
				} else {
					objectToMarshall = createProfileReferenceXml();
				}
				marshaller.marshal(objectToMarshall, byteArrayOutputStream);
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

	private ProfileReferenceXml createProfileReferenceXml() {
		ProfileReferenceXml profileReferenceXml = new ProfileReferenceXml();
		profileReferenceXml.setId(currentProfile.getId());
		return profileReferenceXml;
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

	public void resetMessage(CommitMessageDescription messageDescription)
			throws ExecutionException {
		ResetCommitMessageOperation operation = new ResetCommitMessageOperation(
				this, messageDescription);
		runOperation(operation);
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

	public void setMessage(CommitMessageDescription messageDescription,
			String value) throws ExecutionException {
		SetCommitMessageOperation operation = new SetCommitMessageOperation(
				this, messageDescription, value);
		runOperation(operation);
	}

	private void runOperation(IUndoableOperation operation)
			throws ExecutionException {
		operation.addContext(undoContext);
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		operationHistory.execute(operation, null, null);
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

	public enum CMFList {
		UNUSED, USED;
	}

	public WritableList getList(CMFList targetListType) {
		switch (targetListType) {
		case USED:
			return usedFactories;
		case UNUSED:
			return unusedFactories;
		}
		throw new IllegalArgumentException();
	}

	public void moveFactories(CMFList sourceListType, CMFList targetListType,
			int[] selectionIndices, int insertIndex) throws ExecutionException {
		MoveFactoriesOperation operation = new MoveFactoriesOperation(this,
				getList(sourceListType), getList(targetListType),
				selectionIndices, insertIndex);
		runOperation(operation);
	}

	public WritableList getProfiles() {
		return profiles;
	}

	public ProfileIdResourceAndName getCurrentProfile() {
		return currentProfile;
	}

	interface ICurrentProfileListener {
		void currentProfileChanged();
	}

	public void switchToProfile(ProfileIdResourceAndName profile)
			throws ExecutionException {
		/*
		 * Avoid unnecessary undo entries and a set dirty flag at start.
		 */
		if (profile.equals(currentProfile)) {
			return;
		}
		runOperation(new SwitchProfileOperation(this, profile));
	}

	void setCurrentProfileForOperations(ProfileIdResourceAndName profile) {
		if (this.currentProfile != profile) {
			this.currentProfile = profile;
			for (ICurrentProfileListener listener : currentProfileListenerList) {
				listener.currentProfileChanged();
			}
		}
	}

	public void addCurrentProfileListener(ICurrentProfileListener listener) {
		currentProfileListenerList.add(listener);
	}

}
