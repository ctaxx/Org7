package w_ave.org7;

import java.io.File;

import org.w3c.dom.Node;

public class FileItem implements Item{
	File file; // 1
	ArrayListEx childs; // 2
	private boolean isOpened;
	
	private Item ancestor;
	private int level;
	
	private Node node;
		
	public FileItem (File f) {
		file = f; // 3
	}
		
	@Override
	public String getTitle() { // 4
	  return file.getName();
	}

	@Override
	public int getIconResource() { // 5
	  if (file.isDirectory()) {
	    if (getCountChilds() > 0)
	      return R.drawable.folder;
	    return R.drawable.folder;
	  }
	  return R.drawable.file;
	}

	@Override
	public ArrayListEx getChilds() { // 6
	  if (childs != null)
	    return childs;
		
	  childs = new ArrayListEx();
		
	  File[] files = file.listFiles();

	  if (files != null) {
	    for (File f : files) 
	      childs.add(new FileItem(f));
	  }
		    
	  return childs;
	}

	private int getCountChilds() { // 7
	  if (childs != null)
	    return childs.size();
			
	  File[] files = file.listFiles();
	  if (files == null)
	    return 0;
	  return files.length;
	}
	
	@Override
	public boolean getOpened() {
		return this.isOpened;
	}

	@Override
	public void setOpened(boolean b) {
		this.isOpened = b;
		
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
		return this.node;
	}

	@Override
	public void setSelected(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
