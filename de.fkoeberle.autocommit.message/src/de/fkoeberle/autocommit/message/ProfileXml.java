/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "commit-message-factories")
public class ProfileXml {

	private List<CommitMessageFactoryXml> factories;

	public ProfileXml() {
		// used by JAXB to create an instance of this class
	}

	public ProfileXml(ProfileDescription profileDescription) {
		List<CommitMessageFactoryDescription> factoryDescriptions = profileDescription
				.getFactoryDescriptions();
		factories = new ArrayList<CommitMessageFactoryXml>();
		for (CommitMessageFactoryDescription factoryDescription : factoryDescriptions) {
			CommitMessageFactoryXml factoryXml = new CommitMessageFactoryXml();
			factoryXml.setId(factoryDescription.getId());
			List<CommitMessageDescription> commitMessageDescriptions = factoryDescription
					.getCommitMessageDescriptions();
			List<CommitMessageXml> messageXmlList = new ArrayList<CommitMessageXml>(
					commitMessageDescriptions.size());
			for (CommitMessageDescription messageDescription : commitMessageDescriptions) {
				if (messageDescription.isResetPossible()) {
					CommitMessageXml messageXml = new CommitMessageXml();
					messageXml.setFieldName(messageDescription.getField()
							.getName());
					messageXml.setValue(messageDescription.getCurrentValue());
					messageXmlList.add(messageXml);
				}
			}
			factoryXml.setTemplates(messageXmlList);
		}
	}

	@XmlElement(name = "factory")
	public List<CommitMessageFactoryXml> getFactories() {
		if (factories == null) {
			factories = new ArrayList<CommitMessageFactoryXml>();
		}
		return factories;
	}

	public ProfileDescription createProfileDescription(
			ICMFDescriptionFactory cmfFactory, String defaultProfileId)
			throws IOException {
		List<CommitMessageFactoryDescription> createdFactories = new ArrayList<CommitMessageFactoryDescription>();
		for (CommitMessageFactoryXml factoryXml : factories) {
			CommitMessageFactoryDescription factory;
			factory = cmfFactory.createFactoryDescription(factoryXml.getId());
			Map<String, CommitMessageDescription> fieldNameToMessageDescriptionMap = new HashMap<String, CommitMessageDescription>();
			for (CommitMessageDescription messageDescription : factory
					.getCommitMessageDescriptions()) {
				fieldNameToMessageDescriptionMap.put(messageDescription
						.getField().getName(), messageDescription);
			}
			for (CommitMessageXml templateXml : factoryXml
					.getMessages()) {
				String fieldName = templateXml.getFieldName();
				CommitMessageDescription commitMessageDescription = fieldNameToMessageDescriptionMap
						.get(fieldName);
				if (commitMessageDescription == null) {
					throw new IOException(String.format("%s has no field %s",
							factory.getFactoryClass().getName(), fieldName));
				}
				commitMessageDescription
						.setCurrentValue(templateXml.getValue());
			}
			createdFactories.add(factory);
		}
		return new ProfileDescription(createdFactories, defaultProfileId);
	}

	/**
	 * 
	 * @param resource
	 *            the resource to load the {@link ProfileXml} or
	 *            {@link ProfileReferenceXml} from.
	 * @return the loaded {@link ProfileXml} or {@link ProfileReferenceXml}
	 *         instance.
	 */
	public static Object loadProfileFile(URL resource) throws IOException {
		InputStream inputStream = resource.openStream();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ProfileXml.class,
					ProfileReferenceXml.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setEventHandler(new ValidationEventHandler() {

				@Override
				public boolean handleEvent(ValidationEvent event) {
					return false;
				}
			});
			return unmarshaller.unmarshal(inputStream);
		} catch (JAXBException e) {
			throw new IOException(e);
		} finally {
			inputStream.close();
		}
	}

	public void setFactories(List<CommitMessageFactoryXml> factories) {
		this.factories = factories;
	}
}
