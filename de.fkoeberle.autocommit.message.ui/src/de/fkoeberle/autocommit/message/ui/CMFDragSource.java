package de.fkoeberle.autocommit.message.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

public class CMFDragSource extends DragSourceAdapter {
	private final TableViewer sourceTable;
	private final Long sourceListId;

	public CMFDragSource(TableViewer sourceTable, Long sourceListId) {
		this.sourceTable = sourceTable;
		this.sourceListId = sourceListId;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !sourceTable.getSelection().isEmpty();
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (UniqueIdTransfer.INSTANCE.isSupportedType(event.dataType)) {
			event.data = sourceListId;
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
	}

}
