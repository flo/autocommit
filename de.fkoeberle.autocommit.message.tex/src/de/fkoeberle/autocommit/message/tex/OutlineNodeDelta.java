package de.fkoeberle.autocommit.message.tex;

import java.util.List;

public class OutlineNodeDelta {
	private final OutlineNode oldOutlineNode;
	private final OutlineNode newOutlineNode;

	public OutlineNodeDelta(OutlineNode oldOutlineNode,
			OutlineNode newOutlineNode) {
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
					foundDelta = new OutlineNodeDelta(oldChild, newChild);
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
}
