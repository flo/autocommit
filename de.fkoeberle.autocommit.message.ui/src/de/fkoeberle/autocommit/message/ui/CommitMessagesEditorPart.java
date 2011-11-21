package de.fkoeberle.autocommit.message.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import de.fkoeberle.autocommit.message.CommitMessageBuilderPluginActivator;
import de.fkoeberle.autocommit.message.CommitMessageDescription;
import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.Profile;
import de.fkoeberle.autocommit.message.ProfileDescription;
import de.fkoeberle.autocommit.message.WorkedOnPathCMF;

public class CommitMessagesEditorPart extends EditorPart {
	private DataBindingContext m_bindingContext;

	public static final String ID = "de.fkoeberle.autocommit.message.ui.CommitMessagesEditorPart"; //$NON-NLS-1$
	private ProfileDescription model;
	private Controller controller;
	private Table table;
	private TableViewer tableViewer;
	private CommitMessageFactoryComposite factoryComposite;

	public CommitMessagesEditorPart() {
		ArrayList<ICommitMessageFactory> factories = new ArrayList<ICommitMessageFactory>();
		factories.add(new WorkedOnPathCMF());
		model = new ProfileDescription(new Profile(factories));
	}

	/**
	 * Create contents of the editor part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(container, SWT.NONE);

		Composite leftComposite = new Composite(sashForm, SWT.BORDER);
		leftComposite.setLayout(new GridLayout(1, false));

		Composite leftHeader = new Composite(leftComposite, SWT.NONE);
		leftHeader.setLayout(new GridLayout(2, false));

		Label lblCommitMessageFactories = new Label(leftHeader, SWT.NONE);
		lblCommitMessageFactories.setText("Used Commit Message Factories:");

		Button btnAdd = new Button(leftHeader, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(factoryComposite.factoryDescription
						.getTitle());
				System.out.println(tableViewer.getSelection().isEmpty());
				factoryComposite.setFactoryTitle("Hello");
			}
		});
		btnAdd.setText("Add..");

		tableViewer = new TableViewer(leftComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite rightComposite = new Composite(sashForm, SWT.BORDER);
		rightComposite.setLayout(new GridLayout(1, false));
		factoryComposite = new CommitMessageFactoryComposite(controller,
				rightComposite, SWT.NONE);
		factoryComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false, 1, 1));

		sashForm.setWeights(new int[] { 318, 502 });
		m_bindingContext = initDataBindings();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Do the Save operation
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public void dispose() {
		try {
			m_bindingContext.dispose();
		} finally {
			super.dispose();
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		if (!(input instanceof IURIEditorInput)) {
			throw new PartInitException("Input type not supported");

		}
		URL url;
		try {
			url = ((IURIEditorInput) input).getURI().toURL();
		} catch (MalformedURLException e) {
			throw new PartInitException("Unble to handle input as an url", e);
		}
		try {
			Profile p = CommitMessageBuilderPluginActivator.getProfile(url);
			this.model = new ProfileDescription(p);
			this.controller = new Controller(model, this);
		} catch (IOException e) {
			throw new PartInitException("An IOException occured while loading",
					e);
		}
		// Initialize the editor part
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableMap observeMap = PojoObservables.observeMap(
				listContentProvider.getKnownElements(),
				CommitMessageFactoryDescription.class, "title");
		tableViewer
				.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		//
		IObservableList profileFactoryDescriptionsObserveList = PojoObservables
				.observeList(Realm.getDefault(), model, "factoryDescriptions");
		tableViewer.setInput(profileFactoryDescriptionsObserveList);
		//
		IObservableValue factoryFactoryDescriptionObserveValue = PojoObservables
				.observeValue(factoryComposite, "factoryDescription");
		IObservableValue tableViewerObserveSingleSelection_1 = ViewersObservables
				.observeSingleSelection(tableViewer);
		bindingContext.bindValue(factoryFactoryDescriptionObserveValue,
				tableViewerObserveSingleSelection_1, null, null);
		//
		return bindingContext;
	}

	public ProfileDescription getProfile() {
		return model;
	}

	public void setCommitMessageValue(int factoryIndex, int messageIndex,
			String oldMessage) {
		if (tableViewer.getTable().getSelectionIndex() == factoryIndex) {
			CommitMessageComposite commitMessageComposite = factoryComposite
					.getCommitMessageComposite(messageIndex);
			CommitMessageDescription commitMessageDescription = model
					.getFactoryDescriptions().get(factoryIndex)
					.getCommitMessageDescriptions().get(messageIndex);
			commitMessageComposite.setCurrentMessage(commitMessageDescription
					.getCurrentValue());
		}
	}

}
