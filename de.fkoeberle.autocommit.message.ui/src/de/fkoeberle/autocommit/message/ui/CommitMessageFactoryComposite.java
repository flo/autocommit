package de.fkoeberle.autocommit.message.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.ProfileDescription;

public class CommitMessageFactoryComposite extends Composite {
	private final ProfileDescription model;
	private final Controller controller;
	private int factoryIndex;
	private final Composite argumentsComposite;
	private final Composite messagesComposite;
	private final Label descriptionLabel;
	private final Group grpFactory;

	/**
	 * Constructor only used for preview
	 */
	public CommitMessageFactoryComposite(Composite parent, int style) {
		this(parent, style, null, null, 0);
	}

	/**
	 * Create the composite.
	 * 
	 */
	public CommitMessageFactoryComposite(Composite parent, int style,
			ProfileDescription model, Controller controller, int factoryIndex) {
		super(parent, SWT.NONE);
		this.model = model;
		this.controller = controller;
		this.factoryIndex = factoryIndex;
		CommitMessageFactoryDescription factoryDescription = model
				.getFactoryDescriptions().get(factoryIndex);
		setLayout(new GridLayout(1, false));
		grpFactory = new Group(this, SWT.NONE);
		grpFactory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		grpFactory.setText(factoryDescription.getTitle());
		grpFactory.setLayout(new GridLayout(1, false));

		descriptionLabel = new Label(grpFactory, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true,
				false, 1, 1));
		descriptionLabel.setBounds(0, 0, 100, 100);
		descriptionLabel.setText(factoryDescription.getDescription());

		Label argumentsLabel = new Label(grpFactory, SWT.NONE);
		argumentsLabel.setText("Available Placeholders:");

		argumentsComposite = new Composite(grpFactory, SWT.NONE);
		argumentsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));
		argumentsComposite.setLayout(new GridLayout(2, false));

		Label messagesLabel = new Label(grpFactory, SWT.NONE);
		messagesLabel.setText("Generated Messages:");
		createArgumentDescriptions(factoryDescription);

		messagesComposite = new Composite(grpFactory, SWT.NONE);
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
			Label numberLabel = new Label(argumentsComposite, SWT.NONE);
			numberLabel.setText(String.format("{%d}", i));
			numberLabel.setLayoutData(new GridData(SWT.TOP, SWT.TOP, false,
					false, 1, 1));
			Label descriptionLabel = new Label(argumentsComposite, SWT.NONE);
			descriptionLabel.setText(argumentDescription);
			descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false, 1, 1));
		}
	}

	void createMessageDescriptions(
			CommitMessageFactoryDescription factoryDescription) {
		List<CommitMessageDescription> messageDescriptions = factoryDescription
				.getCommitMessageDescriptions();
		for (int i = 0; i < messageDescriptions.size(); i++) {
			CommitMessageComposite messageComposite = new CommitMessageComposite(
					messagesComposite, SWT.NONE, model, controller,
					factoryIndex, i);

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

	public void setFactoryIndex(int factoryIndex) {
		this.factoryIndex = factoryIndex;
	}

	public int getFactoryIndex() {
		return factoryIndex;
	}

}
