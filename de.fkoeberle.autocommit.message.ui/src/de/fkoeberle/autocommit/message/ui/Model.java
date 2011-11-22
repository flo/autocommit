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

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
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
import de.fkoeberle.autocommit.message.ProfileXml;

public class Model {
	private final WritableList usedFactories;;
	private IEditorInput editorInput;
	private final IUndoContext undoContext;

	public Model() {
		this.usedFactories = new WritableList(Realm.getDefault(),
				Collections.emptySet(), CommitMessageFactoryDescription.class);
		this.undoContext = new ObjectUndoContext(this);
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
}
