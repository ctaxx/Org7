package w_ave.org7;

import org.w3c.dom.Node;

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
