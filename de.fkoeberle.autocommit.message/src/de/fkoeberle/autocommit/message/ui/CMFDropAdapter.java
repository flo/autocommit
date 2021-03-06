/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import de.fkoeberle.autocommit.message.ui.Model.CMFList;

public class CMFDropAdapter extends ViewerDropAdapter {
	private final Model model;
	private final TableViewer targetTable;
	private final CMFList targetListType;
	private final Map<Long, CMFList> listIdToTypeMap;
	private final Map<CMFList, TableViewer> listTypeToTableViewerMap;

	public CMFDropAdapter(CMFList targetListType, Model model,
			Map<Long, CMFList> listIdToTypeMap,
			Map<CMFList, TableViewer> listTypeToTableViewerMap) {
		super(listTypeToTableViewerMap.get(targetListType));
		this.model = model;
		this.targetTable = listTypeToTableViewerMap.get(targetListType);
		this.targetListType = targetListType;
		this.listIdToTypeMap = listIdToTypeMap;
		this.listTypeToTableViewerMap = listTypeToTableViewerMap;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
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
		try {
			model.moveFactories(sourceListType, targetListType, sourceTable
					.getTable().getSelectionIndices(), insertIndex);
		} catch (ExecutionException e) {
			CMFMultiPageEditorPart.reportError(targetTable.getTable()
					.getShell(), "Drop failed", e);
			return false;
		}

		return true;
	}
}
