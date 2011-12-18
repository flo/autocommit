package de.fkoeberle.autocommit.message.tex;

import java.util.List;

import de.fkoeberle.autocommit.message.ChangedRange;
import de.fkoeberle.autocommit.message.ChangedTextFile;

public class OutlineNodeDelta {
	private final ChangedTextFile changedTextFile;
	private final OutlineNode oldOutlineNode;
	private final OutlineNode newOutlineNode;
	private ChangedRange changedChildIndices;
	private ChangedRange smartChangedCharacterRange;

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
			setChangedChildIndicesAndSmartChangedCharacterRange();
		}
		return changedChildIndices;
	}

	public ChangedRange getSmartChangedCharacterRange() {
		if (smartChangedCharacterRange == null) {
			setChangedChildIndicesAndSmartChangedCharacterRange();
		}
		return smartChangedCharacterRange;
	}

	private void setChangedChildIndicesAndSmartChangedCharacterRange() {

		ChangedRange earliestCharRange = changedTextFile
				.getEarliestChangedRange();
		ChangedRange latestCharRange = changedTextFile.getLatestChangedRange();

		List<OutlineNode> oldChilds = getOldOutlineNode().getChildNodes();
		List<OutlineNode> newChilds = getNewOutlineNode().getChildNodes();
		int earliestFirstIndex = earliestCharRange.getFirstIndex();
		int start = determineStartIndex(earliestFirstIndex, newChilds);

		/*
		 * The range which got modified is adjustable to a degree. e.g. a change
		 * "ab" ->"aab" can mean that an a got inserted at index 0 or 1. Ideally
		 * the detected modification gets chosen in such a way that it effects
		 * as little sections as possible. Thus the following code tries to move
		 * that point of first modification after the first section that was
		 * first detected to got modified. In such a case this first section
		 * will no longer counted as modified.
		 */
		int latestFirstIndex = latestCharRange.getFirstIndex();
		int newFirstIndex = latestFirstIndex;
		if ((start < newChilds.size()) || (start < oldChilds.size())) {
			OutlineNode firstChangedChild;
			if (start < newChilds.size()) {
				firstChangedChild = newChilds.get(start);
			} else {
				firstChangedChild = oldChilds.get(start);
			}
			int childExlusiveEnd = firstChangedChild.getExlusiveEndIndex();
			/*
			 * Try to exclude the first child completely from change if
			 * possible, to minimize the number of detected changed childs.
			 */
			if (childExlusiveEnd >= earliestFirstIndex
					&& childExlusiveEnd <= latestFirstIndex) {
				newFirstIndex = childExlusiveEnd;
				start++;
			} else {
				/*
				 * Try to include the first child completely, if a part of the
				 * headline is in the modified area. That way the change is that
				 * it got added. An example is given below:
				 */
				// \section{A}\section{C}\section{D}
				// ->
				// \section{A}\section{X}\section{C}x was so cool\section{D}
				// should not get detected as replacement:
				// C} -> X}\section{C}x was so cool
				// but as:
				// \section{C} -> \section{X}\section{C}x was so cool
				int childContentStart = firstChangedChild
						.getContentStartIndex();
				int childFirstIndex = firstChangedChild.getFirstIndex();
				if (childContentStart > latestFirstIndex
						&& (childFirstIndex <= latestFirstIndex)) {
					newFirstIndex = childFirstIndex;
				}
			}
		}
		int indexOffset = Math.max(0, newFirstIndex - earliestFirstIndex);

		int lastModifiedCharForNew = earliestCharRange.getExlusiveEndOfNew()
				+ indexOffset;
		int lastModifiedCharForOld = earliestCharRange.getExlusiveEndOfOld()
				+ indexOffset;
		assert lastModifiedCharForNew <= latestCharRange.getExlusiveEndOfNew();
		assert lastModifiedCharForOld <= latestCharRange.getExlusiveEndOfOld();

		int exclusiveEndOfNew = determineEndIndex(newChilds, start,
				lastModifiedCharForNew);
		int exclusiveEndOfOld = determineEndIndex(oldChilds, start,
				lastModifiedCharForOld);

		this.smartChangedCharacterRange = new ChangedRange(newFirstIndex,
				lastModifiedCharForOld, lastModifiedCharForNew);
		this.changedChildIndices = new ChangedRange(start, exclusiveEndOfOld,
				exclusiveEndOfNew);
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

	public ChangedTextFile getChangedTextFile() {
		return changedTextFile;
	}
}
