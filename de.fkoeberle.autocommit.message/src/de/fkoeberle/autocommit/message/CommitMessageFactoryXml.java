package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class CommitMessageFactoryXml {
	private String id;
	private List<CommitMessageTemplateXml> templates;

	@XmlElement(name = "id", required = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElementWrapper(name = "commit-message-templates")
	@XmlElement(name = "template")
	public List<CommitMessageTemplateXml> getTemplates() {
		if (templates == null) {
			this.templates = new ArrayList<CommitMessageTemplateXml>(0);
		}
		return templates;
	}

	public void setTemplates(List<CommitMessageTemplateXml> templates) {
		this.templates = templates;
	}

}
