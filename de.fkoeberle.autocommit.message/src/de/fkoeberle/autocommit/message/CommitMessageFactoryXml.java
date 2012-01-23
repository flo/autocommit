/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
