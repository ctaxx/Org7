package w_ave.org7;

import w_ave.org7.item.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter2 extends BaseAdapter {
	
	//TODO add lazy loading
	
	private LayoutInflater mLayoutInflater;
/** ������, ������� ����� ��������� ������� �������� �������� � �������
 *  ����� ������� � ������
 */
	private ArrayListEx hierarchyArray;
/** ������ ��������� �������� ������ ��������. ���� ������ ���������� ������������
 *  ��������, �� ��� �����, ����� ��������� ������ hierarchyArray ��� 
 *  ��������-�������� ������-�� ��������	
 */
	private ArrayListEx originalItems;
	
	public ListAdapter2 (Context ctx, ArrayListEx items){
		mLayoutInflater = LayoutInflater.from(ctx);
		originalItems = items;
		
		hierarchyArray = new ArrayListEx();
		
		generateHierarchy();
	}
	
	@Override
	public int getCount() {
		return hierarchyArray.size();
	}

	@Override
	public Item getItem(int position) {
		return hierarchyArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
/** getView ���������� View ��� ������ ������ ������ ������. � ����� ������
 * �� ������� ��� �� ����� item.xml (� ������� LayoutInflater)
 * convertView ������ View ��� ���������� �������������
 * parent - ��������, � �������� �������������� ��������� ������.	
 * � ������ �� ���������, �� ���� �� ������ ������ ��� ������� � ���� ���,
 * �� ������� �� �� ����� item.xml � ������ �������� ��� ���� ��������� �����	
 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = mLayoutInflater.inflate(R.layout.row, null);
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		Item item = hierarchyArray.get(position);
		
		title.setText(item.getTitle());
	/** ������������� ����������� � TextView	
	 */
		title.setCompoundDrawablesWithIntrinsicBounds(item.getIconResource(), 0, 0, 0);
		title.setPadding(item.getLevel()*15, 0, 0, 0);
		return convertView;
	}
	
	private void generateHierarchy(){
		hierarchyArray.clear();
// �������� ����������� �������, �������� �� ������ ��������� �������� ������		
		generateList(originalItems, 0);
	}
	
/** ������� ��������� ������ ������� ����������� ������� � hierarchyArray. ���� ������
 * ������� ������, �� ��� �������� ���� ����
 * ��������� � �������� ��������� ������ ����������� ���������	
 */
	private void generateList(ArrayListEx items, int level) {
		for(Item i: items){
			i.setLevel(level);
			hierarchyArray.add(i);
			if (i.getOpened()){
				generateList(i.getChilds(), level+1);
			}
		}
	}
 
	public void clickOnItem(int position){
		for (Item i: hierarchyArray)
			i.setSelected(false);
		
		Item i = hierarchyArray.get(position);
		i.setSelected(true);
		
		if (i.getOpened()){
			i.setOpened(false);
		}else{
			i.setOpened(true);
		}
		generateHierarchy();
		notifyDataSetChanged();
	}
	
	public void setSelectedItem(int selectedPosition) {
		for(Item i:hierarchyArray) 
			i.setSelected(false);
		hierarchyArray.get(selectedPosition).setSelected(true);
		generateHierarchy();
		notifyDataSetChanged();
	}
	
	public Item getSelectedItem(){
		for(Item i: hierarchyArray)
			if (i.getSelected())
				return i;
		return hierarchyArray.get(0);
	}
	
	public void refreshList(){
		generateHierarchy();
		notifyDataSetChanged();
	}
}
