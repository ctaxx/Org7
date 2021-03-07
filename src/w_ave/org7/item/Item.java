package w_ave.org7.item;

import org.w3c.dom.Node;

import w_ave.org7.ArrayListEx;

public interface Item {
	public boolean getOpened();
	public void setOpened(boolean isOpened);
	public void setAncestor(Item ancestor);
	public Item getAncestor();
	public String getTitle();
	public int getIconResource();
	public ArrayListEx getChilds();
	public void setLevel(int level);
	public int getLevel();
	public void setNode(Node node);
	public Node getNode();
	public void setSelected(boolean isSelected);
	public boolean getSelected();
	public String getPath();
}
