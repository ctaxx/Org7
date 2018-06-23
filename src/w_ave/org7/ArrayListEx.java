package w_ave.org7;

import java.util.ArrayList;
import org.w3c.dom.Node;

public class ArrayListEx extends ArrayList<Item> {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	public void removeItem(Item item){
		Node node = item.getNode();
		Item ancestor = item.getAncestor();
		
		ancestor.getNode().removeChild(node);
		ancestor.getChilds().remove(item);
	}
	
	public void addItem(String inputText, Node node, Item ancestor){
		Item item = new ListItem(inputText);
		item.setNode(node);
		item.setAncestor(ancestor);
		ancestor.getChilds().add(item);
		ancestor.getNode().appendChild(node);
	}

}
