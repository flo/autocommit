package de.fkoeberle.autocommit.message.tex;

import java.util.List;

import de.fkoeberle.autocommit.message.ChangedRange;
import de.fkoeberle.autocommit.message.ChangedTextFile;

public class OutlineNodeDelta {
	private final ChangedTextFile changedTextFile;
	private final OutlineNode oldOutlineNode;
	private final OutlineNode newOutlineNode;
	private ChangedRange changedChildIndices;

	public OutlineNodeDelta(ChangedTextFile changedTextFile,
			OutlineNode oldOutlineNode, OutlineNode newOutlineNode) {
		this.changedTextFile = changedTextFile;
		this.oldOutlineNode = oldOutlineNode;
		this.newOutlineNode = newOutlineNode;
	}

	public OutlineNode getOldOutlineNode() {
		return oldOutlineNode;
	}

	public OutlineNode getNewOutlineNode() {
		return newOutlineNode;
	}

	/**
	 * 
	 * @return either this or a more specific delta if it finds one in it's
	 *         child trees. Returns never null.
	 */
	public OutlineNodeDelta findMostSpecificDelta() {
		List<OutlineNode> oldChilds = oldOutlineNode.getChildNodes();
		List<OutlineNode> newChilds = newOutlineNode.getChildNodes();
		if (oldChilds.size() != newChilds.size()) {
			return this;
		}
		int numberOfChilds = oldChilds.size();
		OutlineNodeDelta foundDelta = null;
		for (int i = 0; i < numberOfChilds; i++) {
			OutlineNode oldChild = oldChilds.get(i);
			OutlineNode newChild = newChilds.get(i);
			if (!oldChild.getText().equals(newChild.getText())) {
				if (foundDelta == null) {
					foundDelta = new OutlineNodeDelta(changedTextFile,
							oldChild, newChild);
				} else {
					return this;
				}
			}
		}
		if (foundDelta == null) {
			/*
			 * This happens if for example just the introduction changed.
			 */
			return this;
		}
		foundDelta = foundDelta.findMostSpecificDelta();
		return foundDelta;
	}

	public ChangedRange getChangedChildIndices() {
		if (changedChildIndices == null) {
			changedChildIndices = determineChangedChildIndices();
		}
		return changedChildIndices;
	}

	private ChangedRange determineChangedChildIndices() {

		ChangedRange earliestCharRange = changedTextFile
				.getEarliestChangedRange();
		ChangedRange latestCharRange = changedTextFile.getLatestChangedRange();

		List<OutlineNode> oldChilds = getOldOutlineNode().getChildNodes();
		List<OutlineNode> newChilds = getNewOutlineNode().getChildNodes();
		int firstIndex = earliestCharRange.getFirstIndex();
		int start = determineStartIndex(firstIndex, newChilds);

		int lastModifiedCharForNew = latestCharRange.getExlusiveEndOfNew();
		int exclusiveEndOfNew = determineEndIndex(newChilds, start,
				lastModifiedCharForNew);

		int lastModifiedCharForOld = latestCharRange.getExlusiveEndOfOld();
		int exclusiveEndOfOld = determineEndIndex(oldChilds, start,
				lastModifiedCharForOld);

		return new ChangedRange(start, exclusiveEndOfNew, exclusiveEndOfOld);
	}

	private int determineEndIndex(List<OutlineNode> newChilds,
			int startChildIndex, int lastModifiedChar) {
		int exlusiveChildEndIndexForNew = newChilds.size();
		while (exlusiveChildEndIndexForNew > startChildIndex) {
			OutlineNode precursorNewChild = newChilds
					.get(exlusiveChildEndIndexForNew - 1);

			boolean precursorNewChildStartsAfterModification = (precursorNewChild
					.getFirstIndex() >= lastModifiedChar);

			if (precursorNewChildStartsAfterModification) {
				exlusiveChildEndIndexForNew--;
			} else {
				break;
			}
		}
		return exlusiveChildEndIndexForNew;
	}

	private int determineStartIndex(int firstIndex, List<OutlineNode> newChilds) {
		int startChildIndex = 0;
		while (startChildIndex < newChilds.size()) {
			OutlineNode newChild = newChilds.get(startChildIndex);
			boolean noChange = newChild.getExlusiveEndIndex() <= firstIndex;
			if (!noChange) {
				break;
			} else {
				startChildIndex++;
			}
		}
		return startChildIndex;
	}
}
