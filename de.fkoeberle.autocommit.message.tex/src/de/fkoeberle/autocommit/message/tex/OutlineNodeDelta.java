package de.fkoeberle.autocommit.message.tex;

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

}
