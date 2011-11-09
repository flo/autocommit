package de.fkoeberle.autocommit.message.tex;

import java.util.ArrayList;
import java.util.List;

public class OutlineNode {
	private final OutlineNodeType type;
	private final String caption;
	private List<OutlineNode> childNodes;

	public OutlineNode(OutlineNodeType type, String caption) {
		this.caption = caption;
		this.type = type;
		this.childNodes = new ArrayList<OutlineNode>();
	}

	public OutlineNodeType getType() {
		return type;
	}

	public List<OutlineNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<OutlineNode> childNodes) {
		this.childNodes = childNodes;
	}

	public String getCaption() {
		return caption;
	}

	public void addChild(OutlineNode node) {
		childNodes.add(node);
	}

}
