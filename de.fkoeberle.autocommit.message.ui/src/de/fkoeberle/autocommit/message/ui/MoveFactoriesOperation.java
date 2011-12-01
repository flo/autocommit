package de.fkoeberle.autocommit.message.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class MoveFactoriesOperation extends AbstractProfileCustomizingOperation {
	private final WritableList sourceList;
	private final WritableList targetList;
	private final int[] indicesOfObjectsToMove;
	private final int insertIndex;
	private int insertIndexAfterElementRemoval;

	public MoveFactoriesOperation(Model model, WritableList sourceList,
			WritableList targetList, int[] indicesOfObjectsToMove,
			int insertIndex) {
		super("Move Commit Message Factories", model);
		this.sourceList = sourceList;
		this.targetList = targetList;
		this.indicesOfObjectsToMove = indicesOfObjectsToMove;
		this.insertIndex = insertIndex;
	}

	@Override
	public IStatus executeHook(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (insertIndex > targetList.size()) {
			throw new ExecutionException(
					"Insert location is far after list end");
		}
		if (sourceList == targetList) {
			int endOfListIndex = targetList.size()
					- indicesOfObjectsToMove.length;
			insertIndexAfterElementRemoval = Math.min(insertIndex,
					endOfListIndex);
		} else {
			insertIndexAfterElementRemoval = insertIndex;
		}
		Arrays.sort(indicesOfObjectsToMove);
		ArrayList<Object> reverseListOfObjectsToMove = new ArrayList<Object>(
				indicesOfObjectsToMove.length);
		int lastIndexIndex = indicesOfObjectsToMove.length - 1;
		for (int i = lastIndexIndex; i >= 0; i--) {
			int indexOfObjectToMove = indicesOfObjectsToMove[i];
			Object removedObject = sourceList.remove(indexOfObjectToMove);
			reverseListOfObjectsToMove.add(removedObject);
		}
		for (Object objectToMove : reverseListOfObjectsToMove) {
			targetList.add(insertIndexAfterElementRemoval, objectToMove);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undoHook(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		List<Object> objectsToMove = new ArrayList<Object>();
		int numberOfObjectsToMove = indicesOfObjectsToMove.length;
		for (int i = 0; i < numberOfObjectsToMove; i++) {
			Object object = targetList.remove(insertIndexAfterElementRemoval);
			objectsToMove.add(object);
		}
		for (int i = 0; i < numberOfObjectsToMove; i++) {
			Object object = objectsToMove.get(i);
			int insertIndex = indicesOfObjectsToMove[i];
			sourceList.add(insertIndex, object);
		}
		return Status.OK_STATUS;
	}

}
