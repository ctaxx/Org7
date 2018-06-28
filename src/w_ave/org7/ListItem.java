package w_ave.org7;

import org.w3c.dom.Node;

public class ListItem implements Item {
	private String title;
	public ArrayListEx childs;
	private boolean isOpened;
	/**
	 * указатель на предка, чтобы сделать возможным удаление элемента
	 */
	private Item ancestor;
	private int level;
	/**
	 * для привязки DOM к списку ArrayList
	 */
	private Node node;
	/**
	 * для изменения иконки, для отображения меню запуска отсчета времени
	 */
	private boolean isSelected;
	
	public ListItem(String title){
		this.title = title;
		childs = new ArrayListEx();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int getIconResource() {
		if (childs.size() > 0){
			if (isSelected)
				return R.drawable.redfolder;
			return R.drawable.folder;
		}
		if (isSelected)
			return R.drawable.redfile;
		return R.drawable.file;
	}

	@Override
	public ArrayListEx getChilds() {
		return childs;
	}
	
	public void addChild(Item item){
		childs.add(item);
	}

	@Override
	public boolean getOpened() {
		return this.isOpened;
	}

	@Override
	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}

	@Override
	public void setAncestor(Item ancestor) {
		this.ancestor = ancestor;
		
	}

	@Override
	public Item getAncestor() {
		return this.ancestor;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
		
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		
	}

	@Override
	public boolean getSelected() {
		return isSelected;
	}

}
