package de.fkoeberle.autocommit.message.ui;

import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import de.fkoeberle.autocommit.message.ui.Model.CMFList;

public class CMFDropAdapter extends ViewerDropAdapter {
	private final Model model;
	private final Controller controller;
	private final TableViewer targetTable;
	private final CMFList targetListType;
	private final Map<Long, CMFList> listIdToTypeMap;
	private final Map<CMFList, TableViewer> listTypeToTableViewerMap;

	public CMFDropAdapter(CMFList targetListType, Model model,
			Controller controller, Map<Long, CMFList> listIdToTypeMap,
			Map<CMFList, TableViewer> listTypeToTableViewerMap) {
		super(listTypeToTableViewerMap.get(targetListType));
		this.model = model;
		this.targetTable = listTypeToTableViewerMap.get(targetListType);
		this.targetListType = targetListType;
		this.controller = controller;
		this.listIdToTypeMap = listIdToTypeMap;
		this.listTypeToTableViewerMap = listTypeToTableViewerMap;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		// TODO Auto-generated method stub
		return true;
	}

	private int determineInsertIndex() {
		int insertIndex;
		if (getCurrentLocation() == LOCATION_NONE) {
			insertIndex = model.getList(targetListType).size();
		} else {
			int targetIndex = model.getList(targetListType).indexOf(
					getCurrentTarget());
			if (getCurrentLocation() == LOCATION_BEFORE
					|| (getCurrentLocation() == LOCATION_ON)) {
				insertIndex = targetIndex;
			} else {
				assert ((getCurrentLocation() == LOCATION_AFTER));
				insertIndex = targetIndex + 1;
			}
		}
		return insertIndex;
	}

	@Override
	public boolean performDrop(Object data) {
		CMFList sourceListType = listIdToTypeMap.get(data);
		if (sourceListType == null) {
			return false;
		}
		TableViewer sourceTable = listTypeToTableViewerMap.get(sourceListType);
		int insertIndex = determineInsertIndex();
		controller.moveFactories(targetTable.getTable(), sourceListType,
				targetListType, sourceTable.getTable().getSelectionIndices(),
				insertIndex);

		return true;
	}
}
