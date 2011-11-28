package de.fkoeberle.autocommit.message;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "commit-message-factories-reference")
public class ProfileReferenceXml {
	private String id;

	@XmlValue
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
