package w_ave.org7;

import java.io.File;
import org.w3c.dom.Node;

public class FileItem implements Item{
	File file; 
	ArrayListEx childs;
	private boolean isOpened;
	
	private Item ancestor;
	private int level;
	
	private Node node;
	
	private boolean isSelected;
		
	public FileItem (File f) {
		file = f;
	}
		
	@Override
	public String getTitle() {
	  return file.getName();
	}

	@Override
	public int getIconResource() {
	  if (file.isDirectory()) {
//	    if (getCountChilds() > 0)
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

	private int getCountChilds() {
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
		return this.node;
	}

	@Override
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		
	}

	@Override
	public boolean getSelected() {
		return this.isSelected;
	}
}
