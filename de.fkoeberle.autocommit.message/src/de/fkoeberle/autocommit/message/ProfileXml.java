package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

	public Profile createProfile(ICommitMessageFactoryFactory cmfFactory)
			throws IOException {
		List<ICommitMessageFactory> createdFactories = new ArrayList<ICommitMessageFactory>();
		for (CommitMessageFactoryXml factoryXml : factories) {
			ICommitMessageFactory factory;
			try {
				factory = cmfFactory.createFactory(factoryXml.getId());
				Class<?> factoryClass = factory.getClass();
				for (CommitMessageTemplateXml templateXml : factoryXml
						.getTemplates()) {
					String fieldName = templateXml.getFieldName();
					Field field = factoryClass.getField(fieldName);
					Object fieldValue = field.get(factory);
					CommitMessageTemplate template = (CommitMessageTemplate) fieldValue;
					template.setValue(templateXml.getValue());
				}
			} catch (Exception e) {
				throw new IOException("Failed to load CMF", e);
			}
			createdFactories.add(factory);
		}
		return new Profile(createdFactories);
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
