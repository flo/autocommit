package de.fkoeberle.autocommit.message;

import javax.xml.bind.annotation.XmlElement;

public class CommitMessageTemplateXml {
	private String fieldName;
	private String value;

	@XmlElement(name = "field")
	public String getFieldName() {
		return fieldName;
	}

	@XmlElement(name = "value")
	public String getValue() {
		return value;
	}

}
