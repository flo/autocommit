/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.util.List;

public class CommitMessageFactoryDescription {
	private final Class<? extends ICommitMessageFactory> factoryClass;
	private final String description;
	private final List<String> argumentDescriptions;
	private final List<CommitMessageDescription> commitMessageDescriptions;

	public CommitMessageFactoryDescription(
			Class<? extends ICommitMessageFactory> factoryClass,
			String description, List<String> argumentDescriptions,
			List<CommitMessageDescription> messageDescriptions) {
		this.description = description;
		this.factoryClass = factoryClass;
		this.argumentDescriptions = argumentDescriptions;
		this.commitMessageDescriptions = messageDescriptions;
	}

	public String getTitle() {
		return factoryClass.getSimpleName();
	}

	public String getDescription() {
		return description;
	}

	public List<String> getArgumentDescriptions() {
		return argumentDescriptions;
	}

	public List<CommitMessageDescription> getCommitMessageDescriptions() {
		return commitMessageDescriptions;
	}

	public ICommitMessageFactory createFactory() {
		ICommitMessageFactory factory;
		try {
			factory = factoryClass.newInstance();
			for (CommitMessageDescription messageDescription : commitMessageDescriptions) {
				messageDescription.injectCurrentValueTo(factory);
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return factory;
	}

	public Class<? extends ICommitMessageFactory> getFactoryClass() {
		return factoryClass;
	}

	public String getId() {
		return factoryClass.getCanonicalName();
	}

	@Override
	public String toString() {
		return getId();
	}
}
