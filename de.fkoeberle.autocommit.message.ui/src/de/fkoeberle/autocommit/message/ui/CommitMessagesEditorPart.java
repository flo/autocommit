package de.fkoeberle.autocommit.message.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import de.fkoeberle.autocommit.message.CommitMessageFactoryDescription;
import de.fkoeberle.autocommit.message.ICommitMessageFactory;
import de.fkoeberle.autocommit.message.WorkedOnPathCMF;
import de.fkoeberle.autocommit.message.ui.Model.IDirtyPropertyListener;

public class CommitMessagesEditorPart extends EditorPart {

	private final class FactoryLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			CommitMessageFactoryDescription factoryDescription = (CommitMessageFactoryDescription) element;
			return factoryDescription.getTitle();
		}
	}

	public static final String ID = "de.fkoeberle.autocommit.message.ui.CommitMessagesEditorPart"; //$NON-NLS-1$
	private final Model model;
	private final Controller controller;
	private Table usedFactoriesTable;
	private TableViewer usedFactoriesTableViewer;
	private Composite factoriesComposite;
	private ScrolledComposite rightComposite;
	private Table unusedFactoriesTable;

	public CommitMessagesEditorPart() {
		ArrayList<ICommitMessageFactory> factories = new ArrayList<ICommitMessageFactory>();
		factories.add(new WorkedOnPathCMF());
		model = new Model();
		controller = new Controller(this, model);
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
		leftHeader.setLayout(new GridLayout(1, false));

		Label lblCommitMessageFactories = new Label(leftHeader, SWT.NONE);
		lblCommitMessageFactories.setText("Used:");

		usedFactoriesTableViewer = new TableViewer(leftComposite, SWT.BORDER
				| SWT.MULTI);
		usedFactoriesTable = usedFactoriesTableViewer.getTable();
		usedFactoriesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		usedFactoriesTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						int[] indices = usedFactoriesTableViewer.getTable()
								.getSelectionIndices();
						controller.handleLeftFactorySelection(indices);
					}
				});
		usedFactoriesTableViewer
				.setContentProvider(new ObservableListContentProvider());
		usedFactoriesTableViewer.setLabelProvider(new FactoryLabelProvider());
		usedFactoriesTableViewer.setInput(model.getFactoryDescriptions());

		Composite middleComposite = new Composite(sashForm, SWT.BORDER);
		middleComposite.setLayout(new GridLayout(1, false));

		Composite middleHeader = new Composite(middleComposite, SWT.NONE);
		middleHeader.setLayout(new GridLayout(1, false));

		Label lblUnused = new Label(middleHeader, SWT.NONE);
		lblUnused.setText("Unused:");

		TableViewer unusedFactoriesTableViewer = new TableViewer(
				middleComposite, SWT.BORDER | SWT.MULTI);
		unusedFactoriesTable = unusedFactoriesTableViewer.getTable();
		unusedFactoriesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1));
		unusedFactoriesTableViewer
				.setContentProvider(new ObservableListContentProvider());
		unusedFactoriesTableViewer.setLabelProvider(new FactoryLabelProvider());
		unusedFactoriesTableViewer.setInput(model
				.getUnusedFactoryDescriptions());

		rightComposite = new ScrolledComposite(sashForm, SWT.V_SCROLL
				| SWT.BORDER);
		factoriesComposite = new Composite(rightComposite, SWT.NONE);
		rightComposite.setContent(factoriesComposite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		factoriesComposite.setLayout(layout);
		rightComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle r = rightComposite.getClientArea();
				factoriesComposite.setSize(factoriesComposite.computeSize(
						r.width, SWT.DEFAULT));
				factoriesComposite.layout();
			}
		});
		rightComposite.setAlwaysShowScrollBars(true);
		sashForm.setWeights(new int[] { 200, 200, 350 });

		model.addDirtyPropertyListener(new IDirtyPropertyListener() {

			@Override
			public void handleDirtyPropertyChange() {
				firePropertyChange(PROP_DIRTY);
			}
		});
		container.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				controller.dispose();
			}
		});
	}

	public void setRightFactorySelection(int[] indices) {
		for (Control child : factoriesComposite.getChildren()) {
			child.dispose();
		}
		for (int factoryIndex : indices) {
			CommitMessageFactoryDescription factory = (CommitMessageFactoryDescription) model
					.getFactoryDescriptions().get(factoryIndex);
			CommitMessageFactoryComposite factoryComposite = new CommitMessageFactoryComposite(
					factoriesComposite, SWT.NONE, controller, factory);
			factoryComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
					true, false, 1, 1));
		}

		factoriesComposite.layout(true, true);
		factoriesComposite.setSize(factoriesComposite.computeSize(
				factoriesComposite.getSize().x, SWT.DEFAULT));
	}

	@Override
	public void setFocus() {
		// Set the focus

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		controller.save(monitor);
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		controller.initEditor(site, input);
	}

	@Override
	public boolean isDirty() {
		return model.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
