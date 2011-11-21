package de.fkoeberle.autocommit.message.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.ProfileDescription;

public class CommitMessageComposite extends Composite {
	private final DataBindingContext m_bindingContext;
	private final Text field;
	private final Label captionLabel;
	private final Button resetButton;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param messageDescription
	 */
	public CommitMessageComposite(Composite parent, int style,
			final ProfileDescription model, final Controller controller,
			final int factoryIndex, final int messageIndex) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		final CommitMessageDescription messageDescription = model
				.getMessageDescription(factoryIndex, messageIndex);
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

		resetButton = new Button(bodyComposite, SWT.NONE);
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (controller != null) {
					controller.resetMessage(CommitMessageComposite.this,
							factoryIndex, messageIndex);
				}
			}
		});
		resetButton
				.setToolTipText("Resets the replacement to the default value");
		resetButton.setText("Reset");

		setDefaultMessage(messageDescription.getDefaultValue());
		setCurrentMessage(messageDescription.getCurrentValue());
		m_bindingContext = initDataBindings();
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

	@Override
	public void dispose() {
		try {
			m_bindingContext.dispose();
		} finally {
			super.dispose();
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
