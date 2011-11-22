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

	@XmlElement(name = "factory")
	public List<CommitMessageFactoryXml> getFactories() {
		if (factories == null) {
			factories = new ArrayList<CommitMessageFactoryXml>();
		}
		return factories;
	}

	public ProfileDescription createProfileDescription(
			ICMFDescriptionFactory cmfFactory) throws IOException {
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
			for (CommitMessageTemplateXml templateXml : factoryXml
					.getTemplates()) {
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
		return new ProfileDescription(createdFactories);
	}

	public static ProfileXml createFrom(URL resource) throws IOException {
		InputStream inputStream = resource.openStream();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ProfileXml.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			ProfileXml profileXml = (ProfileXml) unmarshaller
					.unmarshal(inputStream);
			unmarshaller.setEventHandler(new ValidationEventHandler() {

				@Override
				public boolean handleEvent(ValidationEvent event) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			return profileXml;

		} catch (JAXBException e) {
			throw new IOException(e);
		} finally {
			inputStream.close();
		}
	}
}
