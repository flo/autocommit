/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.fkoeberle.autocommit.message.ProfileIdResourceAndName;
import de.fkoeberle.autocommit.message.ui.Model.ICurrentProfileListener;

public class OverviewPage extends FormPage {
	private static final String ID = FormPage.class.getCanonicalName();
	private static final String TITLE = "Overview";
	private final Model model;

	public OverviewPage(FormEditor editor, Model model) {
		super(editor, ID, TITLE);
		this.model = model;
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Overview");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		managedForm.getForm().getBody().setLayout(new GridLayout(1, false));

		Section sctnSelectHowCommit = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(),
				Section.TWISTIE | Section.TITLE_BAR);
		GridData gd_sctnSelectHowCommit = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		sctnSelectHowCommit.setLayoutData(gd_sctnSelectHowCommit);
		managedForm.getToolkit().paintBordersFor(sctnSelectHowCommit);
		sctnSelectHowCommit
				.setText("Select which commit messages should be generated:");

		Composite profileRadioButtonsComposite = toolkit.createComposite(
				sctnSelectHowCommit, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(profileRadioButtonsComposite);
		sctnSelectHowCommit.setClient(profileRadioButtonsComposite);
		profileRadioButtonsComposite.setLayout(new GridLayout(1, false));
		createRadioButtons(toolkit, profileRadioButtonsComposite);

		Section helpSection = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(),
				Section.TWISTIE | Section.TITLE_BAR);
		toolkit.paintBordersFor(helpSection);
		helpSection.setText("How to customize the commit message generation:");
		Composite helpComposite = toolkit
				.createComposite(helpSection, SWT.NONE);
		toolkit.paintBordersFor(helpComposite);
		helpSection.setClient(helpComposite);
		helpComposite.setLayout(new GridLayout(1, false));

		String helpStringPart1 = "For what commmit messages are generated and how they are formulated"
				+ " gets determined by a list of so called \"Commit Message Factories\".";
		createHelpLabel(toolkit, helpComposite, helpStringPart1);
		Hyperlink helpAdvancedHyperlink = toolkit.createHyperlink(
				helpComposite, "This list can be edited in the advanced tab.",
				SWT.WRAP);
		helpAdvancedHyperlink.addHyperlinkListener(new CustomizeProfile(
				helpAdvancedHyperlink.getShell(), null));
		layoutHelpItem(helpAdvancedHyperlink);

		String helpStringPart2 = "The message algorithm will search for the first factory that can successfully"
				+ " create a message for a given change. It starts with the search at the top of the list."
				+ " Thus more specific \"Commit Message Factories\" should be put to the top and a factory"
				+ " that is always able to generate a message at the bottom, as factories below it would not get used."
				+ " The list of available commit message factories can be extended via the factory commit message extension point.";
		createHelpLabel(toolkit, helpComposite, helpStringPart2);

		/*
		 * setExpanded needs to be called after creating the content. Since
		 * eclipse 3.7 seems to not create the section content properly
		 * otherwise.
		 */
		sctnSelectHowCommit.setExpanded(true);
		helpSection.setExpanded(true);

	}

	private void createHelpLabel(FormToolkit toolkit, Composite helpComposite,
			String helpString) {
		Label label = toolkit.createLabel(helpComposite, helpString, SWT.WRAP);
		layoutHelpItem(label);
	}

	private void layoutHelpItem(Control control) {
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_label.widthHint = 500;
		control.setLayoutData(gd_label);
	}

	private void createRadioButtons(FormToolkit toolkit,
			Composite profileRadioButtonsComposite) {
		for (Object object : model.getProfiles()) {
			final ProfileIdResourceAndName profile = (ProfileIdResourceAndName) object;
			Composite rowComposite = toolkit
					.createComposite(profileRadioButtonsComposite);
			GridData rowGridData = new GridData();
			rowGridData.widthHint = 500;
			rowComposite.setLayoutData(rowGridData);
			GridLayout rowLayout = new GridLayout(2, false);
			rowLayout.marginHeight = 0;
			rowComposite.setLayout(rowLayout);

			final Button radioButton = toolkit.createButton(rowComposite,
					profile.getName(), SWT.RADIO);
			toolkit.adapt(radioButton, true, true);
			ICurrentProfileListener listener = new RadioButtonCurrentProfileListener(
					model, radioButton, profile);
			model.addCurrentProfileListener(listener);
			radioButton.addSelectionListener(new RadioButtonSelectionListener(
					model, radioButton, profile));
			if (profile != Model.CUSTOM_PROFILE) {
				Hyperlink hyperlink = toolkit.createHyperlink(rowComposite,
						"(Customize...)", SWT.NONE);
				hyperlink.addHyperlinkListener(new CustomizeProfile(radioButton
						.getShell(), profile));
			}
			listener.currentProfileChanged();
		}
	}

	private final class CustomizeProfile extends HyperlinkAdapter {
		private final Shell shell;
		private final ProfileIdResourceAndName profile;

		private CustomizeProfile(Shell shell, ProfileIdResourceAndName profile) {
			this.shell = shell;
			this.profile = profile;
		}

		@Override
		public void linkActivated(HyperlinkEvent event) {
			getEditor().setActivePage(AdvancedPage.ID);
			if (profile != null) {
				try {
					model.setCurrentProfile(profile);
				} catch (ExecutionException e) {
					AdvancedPage.reportError(shell,
							"Failed to switch profile for customization", e);
				}
			}
		}
	}

	private final class RadioButtonSelectionListener implements
			SelectionListener {
		private final Model model;
		private final Button radioButton;
		private final ProfileIdResourceAndName profile;

		private RadioButtonSelectionListener(Model model, Button radioButton,
				ProfileIdResourceAndName profile) {
			this.model = model;
			this.radioButton = radioButton;
			this.profile = profile;
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			try {
				model.setCurrentProfile(profile);
			} catch (ExecutionException e) {
				AdvancedPage.reportError(radioButton.getShell(),
						"Failed to switch profile", e);
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent event) {
			try {
				model.setCurrentProfile(profile);
			} catch (ExecutionException e) {
				AdvancedPage.reportError(radioButton.getShell(),
						"Failed to switch profile", e);
			}
		}
	}

	private final class RadioButtonCurrentProfileListener implements
			ICurrentProfileListener {
		private final Model model;
		private final Button radioButton;
		private final ProfileIdResourceAndName profile;

		private RadioButtonCurrentProfileListener(Model model,
				Button radioButton, ProfileIdResourceAndName profile) {
			this.model = model;
			this.radioButton = radioButton;
			this.profile = profile;
		}

		@Override
		public void currentProfileChanged() {
			boolean selected = (model.getCurrentProfile() == profile);
			if (selected != radioButton.getSelection()) {
				radioButton.setSelection(selected);
			}
			if (profile == Model.CUSTOM_PROFILE) {
				radioButton.setEnabled(selected);
			}
		}
	}
}
