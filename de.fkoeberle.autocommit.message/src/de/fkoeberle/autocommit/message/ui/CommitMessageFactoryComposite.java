/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;

public class CommitMessageFactoryComposite extends Composite {
	private final Composite argumentsComposite;
	private final Composite messagesComposite;
	private final Label descriptionLabel;
	private final Group grpFactory;
	private final Model model;
	private final FormToolkit toolkit;

	/**
	 * Constructor only used for preview
	 */
	public CommitMessageFactoryComposite(Composite parent, int style) {
		this(parent, style, null, null, new FormToolkit(Display.getCurrent()));
	}

	/**
	 * Create the composite.
	 * 
	 */
	public CommitMessageFactoryComposite(Composite parent, int style,
			Model model, CommitMessageFactoryDescription factoryDescription,
			FormToolkit toolkit) {
		super(parent, SWT.NONE);
		toolkit.adapt(this);
		this.model = model;
		this.toolkit = toolkit;
		setLayout(new GridLayout(1, false));
		grpFactory = new Group(this, SWT.NONE);
		grpFactory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		grpFactory.setText(factoryDescription.getTitle());
		grpFactory.setLayout(new GridLayout(1, false));
		toolkit.adapt(grpFactory);

		String description = factoryDescription.getDescription();
		if (description == null) {
			description = "placeholder description";
		}
		descriptionLabel = toolkit.createLabel(grpFactory, description,
				SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true,
				false, 1, 1));
		descriptionLabel.setBounds(0, 0, 100, 100);

		toolkit.createLabel(grpFactory, "Available Placeholders:");

		argumentsComposite = toolkit.createComposite(grpFactory, SWT.NONE);
		argumentsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));
		argumentsComposite.setLayout(new GridLayout(2, false));

		toolkit.createLabel(grpFactory, "Generated Messages:");
		createArgumentDescriptions(factoryDescription);

		messagesComposite = toolkit.createComposite(grpFactory);
		messagesComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		messagesComposite.setLayout(new GridLayout(1, false));
		createMessageDescriptions(factoryDescription);

	}

	public void setFactoryDescription(
			CommitMessageFactoryDescription factoryDescription) {

		/*
		 * all==true argument at layout call is needed, otherwise the argument
		 * list update does not work if the new factory has the same arguments
		 */
		getParent().layout(true, true);

	}

	void createArgumentDescriptions(
			CommitMessageFactoryDescription factoryDescription) {
		List<String> argumentDescriptions = factoryDescription
				.getArgumentDescriptions();
		for (int i = 0; i < argumentDescriptions.size(); i++) {
			String argumentDescription = argumentDescriptions.get(i);
			String numberString = String.format("{%d}", i);
			Label numberLabel = toolkit.createLabel(argumentsComposite,
					numberString);
			numberLabel.setLayoutData(new GridData(SWT.TOP, SWT.TOP, false,
					false, 1, 1));

			Label descriptionLabel = toolkit.createLabel(argumentsComposite,
					argumentDescription, SWT.WRAP);
			descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false, 1, 1));
		}
	}

	void createMessageDescriptions(
			CommitMessageFactoryDescription factoryDescription) {
		List<CommitMessageDescription> messageDescriptions = factoryDescription
				.getCommitMessageDescriptions();
		for (CommitMessageDescription messageDescription : messageDescriptions) {
			CommitMessageComposite messageComposite = new CommitMessageComposite(
					messagesComposite, SWT.NONE, messageDescription, model,
					toolkit);

			messageComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false, 1, 1));
		}
	}

	public String getFactoryTitle() {
		return grpFactory.getText();
	}

	public void setFactoryTitle(String text_1) {
		System.out.println("title got set");
		grpFactory.setText(text_1);
	}

	public CommitMessageComposite getCommitMessageComposite(int messageIndex) {
		return (CommitMessageComposite) (messagesComposite.getChildren()[messageIndex]);
	}

}
