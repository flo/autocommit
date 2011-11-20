package de.fkoeberle.autocommit.message.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.WorkedOnPathCMF;

public class CommitMessageFactoryComposite extends Composite {
	CommitMessageFactoryDescription factoryDescription = new CommitMessageFactoryDescription(
			new WorkedOnPathCMF());
	private final Composite argumentsComposite;
	private final Composite messagesComposite;
	private final Label descriptionLabel;
	private final Group grpFactory;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CommitMessageFactoryComposite(Composite parent, int style) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		grpFactory = new Group(this, SWT.NONE);
		grpFactory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		grpFactory.setText("DocumentatedJavaTypeCMF");
		grpFactory.setLayout(new GridLayout(1, false));

		descriptionLabel = new Label(grpFactory, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true,
				false, 1, 1));
		descriptionLabel.setBounds(0, 0, 100, 100);
		descriptionLabel
				.setText("This is a very long description placeholder, which should show that very long descriptions are possible. Even multiple lines should be possible");

		Label argumentsLabel = new Label(grpFactory, SWT.NONE);
		argumentsLabel.setText("Available Placeholders:");

		argumentsComposite = new Composite(grpFactory, SWT.NONE);
		argumentsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));
		argumentsComposite.setLayout(new GridLayout(2, false));

		Label argumentNameLabel = new Label(argumentsComposite, SWT.NONE);
		argumentNameLabel.setText("{0}");

		Label argumentDescriptionLabel = new Label(argumentsComposite, SWT.WRAP);
		argumentDescriptionLabel
				.setText("This is a very long description placeholder, which should show that very long descriptions are possible. Even multiple lines should be possible");
		argumentDescriptionLabel.setBounds(0, 0, 474, 51);

		Label messagesLabel = new Label(grpFactory, SWT.NONE);
		messagesLabel.setText("Generated Messages:");

		messagesComposite = new Composite(grpFactory, SWT.NONE);
		messagesComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		messagesComposite.setLayout(new GridLayout(1, false));

	}

	public CommitMessageFactoryDescription getFactoryDescription() {
		return factoryDescription;
	}

	public void setFactoryDescription(
			CommitMessageFactoryDescription factoryDescription) {
		this.factoryDescription = factoryDescription;
		grpFactory.setText(factoryDescription.getTitle());
		descriptionLabel.setText(factoryDescription.getDescription());
		updateArgumentDescriptions();
		updateMessageDescriptions();
		/*
		 * all==true argument at layout call is needed, otherwise the argument
		 * list update does not work if the new factory has the same arguments
		 */
		getParent().layout(true, true);

	}

	void updateArgumentDescriptions() {
		for (Control control : argumentsComposite.getChildren()) {
			control.dispose();
		}
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

	void updateMessageDescriptions() {
		for (Control control : messagesComposite.getChildren()) {
			control.dispose();
		}
		List<CommitMessageDescription> messageDescriptions = factoryDescription
				.getCommitMessageDescriptions();
		for (int i = 0; i < messageDescriptions.size(); i++) {
			CommitMessageDescription messageDescription = messageDescriptions
					.get(i);
			CommitMessageComposite messageComposite = new CommitMessageComposite(
					messagesComposite, SWT.NONE, messageDescription);

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

}
