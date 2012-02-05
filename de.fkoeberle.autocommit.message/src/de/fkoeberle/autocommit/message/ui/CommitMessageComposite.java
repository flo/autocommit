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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.fkoeberle.autocommit.message.CommitMessageDescription;

public class CommitMessageComposite extends Composite {
	private final Text field;
	private final Label captionLabel;
	private final Button resetButton;
	private final CommitMessageDescription messageDescription;
	private final MessageListener messageListener;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param messageDescription
	 * @param toolkit
	 */
	public CommitMessageComposite(Composite parent, int style,
			CommitMessageDescription messageDescription, final Model model,
			FormToolkit toolkit) {
		super(parent, style);
		toolkit.adapt(this);
		this.messageDescription = messageDescription;
		setLayout(new GridLayout(1, false));
		Composite headerComposite = toolkit.createComposite(this, SWT.NONE);
		toolkit.adapt(headerComposite);
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		headerComposite.setBounds(0, 0, 259, 17);
		GridLayout gl_headerComposite = new GridLayout(1, false);
		gl_headerComposite.marginHeight = 0;
		gl_headerComposite.horizontalSpacing = 0;
		headerComposite.setLayout(gl_headerComposite);

		captionLabel = toolkit.createLabel(headerComposite, "", SWT.NONE);

		Composite bodyComposite = new Composite(this, SWT.NONE);
		toolkit.adapt(bodyComposite);
		bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		bodyComposite.setBounds(0, 0, 454, 26);
		GridLayout gl_bodyComposite = new GridLayout(2, false);
		gl_bodyComposite.marginHeight = 0;
		bodyComposite.setLayout(gl_bodyComposite);

		field = toolkit.createText(bodyComposite, "", SWT.BORDER);
		field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		ModifyListener modifyListener = new TextModificationListener(model);
		field.addModifyListener(modifyListener);

		resetButton = toolkit.createButton(bodyComposite, "Reset", SWT.NONE);
		resetButton.addSelectionListener(new ResetButtonClickListener(model));
		resetButton
				.setToolTipText("Resets the replacement to the default value");

		setDefaultMessage(messageDescription.getDefaultValue());
		setCurrentMessage(messageDescription.getCurrentValue());

		messageListener = new MessageListener();
		addDisposeListener(new SelfDisposeListener());
		messageDescription.addListener(messageListener);
		messageListener.handleMessageChanged();
	}

	/**
	 * Overriding dispose() will not work since it does not get called when the
	 * widget gets disposed by parent.
	 */
	private final class SelfDisposeListener implements DisposeListener {
		@Override
		public void widgetDisposed(DisposeEvent e) {
			messageDescription.removeListener(messageListener);
		}
	}

	private final class TextModificationListener implements ModifyListener {
		private final Model model;

		private TextModificationListener(Model model) {
			this.model = model;
		}

		@Override
		public void modifyText(ModifyEvent event) {
			if (!field.getText().equals(messageDescription.getCurrentValue())) {
				try {
					model.setMessage(messageDescription, field.getText());
				} catch (ExecutionException ex) {
					CMFMultiPageEditorPart.reportError(
							CommitMessageComposite.this.getShell(),
							"Failed to set commit message", ex);
				}
			}
		}
	}

	private final class ResetButtonClickListener extends SelectionAdapter {
		private final Model model;

		private ResetButtonClickListener(Model model) {
			this.model = model;
		}

		@Override
		public void widgetSelected(SelectionEvent event) {

			try {
				model.resetMessage(messageDescription);
			} catch (ExecutionException e) {
				CMFMultiPageEditorPart.reportError(
						CommitMessageComposite.this.getShell(),
						"Failed to reset commit message", e);
			}

		}
	}

	private class MessageListener implements CommitMessageDescription.IListener {

		@Override
		public void handleMessageChanged() {
			setCurrentMessage(messageDescription.getCurrentValue());
			setResetEnabled(messageDescription.isResetPossible());
		}
	}

	public String getCurrentMessage() {
		return field.getText();
	}

	public void setCurrentMessage(String text) {
		field.setText(text);
	}

	public void setResetEnabled(boolean value) {
		resetButton.setEnabled(value);
	}

	private void setDefaultMessage(String value) {
		captionLabel.setText(NLS.bind("Replacement for \"{0}\":", value));
	}

}
