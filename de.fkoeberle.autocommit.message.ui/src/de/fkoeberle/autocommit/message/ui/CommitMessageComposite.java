package de.fkoeberle.autocommit.message.ui;

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
	 */
	public CommitMessageComposite(Composite parent, int style,
			final Controller controller,
			CommitMessageDescription messageDescription) {
		super(parent, style);
		this.messageDescription = messageDescription;
		setLayout(new GridLayout(1, false));
		Composite headerComposite = new Composite(this, SWT.NONE);
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		headerComposite.setBounds(0, 0, 259, 17);
		GridLayout gl_headerComposite = new GridLayout(1, false);
		gl_headerComposite.marginHeight = 0;
		gl_headerComposite.horizontalSpacing = 0;
		headerComposite.setLayout(gl_headerComposite);

		captionLabel = new Label(headerComposite, SWT.NONE);

		Composite bodyComposite = new Composite(this, SWT.NONE);
		bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		bodyComposite.setBounds(0, 0, 454, 26);
		GridLayout gl_bodyComposite = new GridLayout(2, false);
		gl_bodyComposite.marginHeight = 0;
		bodyComposite.setLayout(gl_bodyComposite);

		field = new Text(bodyComposite, SWT.BORDER);
		field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		ModifyListener modifyListener = new TextModificationListener(controller);
		field.addModifyListener(modifyListener);

		resetButton = new Button(bodyComposite, SWT.NONE);
		resetButton.addSelectionListener(new ResetButtonClickListener(
				controller));
		resetButton
				.setToolTipText("Resets the replacement to the default value");
		resetButton.setText("Reset");

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
		private final Controller controller;

		private TextModificationListener(Controller controller) {
			this.controller = controller;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			if (!field.getText().equals(messageDescription.getCurrentValue())) {
				controller.setMessage(CommitMessageComposite.this,
						messageDescription, field.getText());
			}
		}
	}

	private final class ResetButtonClickListener extends SelectionAdapter {
		private final Controller controller;

		private ResetButtonClickListener(Controller controller) {
			this.controller = controller;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (controller != null) {
				controller.resetMessage(CommitMessageComposite.this,
						messageDescription);
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
		captionLabel.setText(NLS.bind("Replacement of \"{0}\":", value));
	}

}
