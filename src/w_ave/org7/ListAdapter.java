package w_ave.org7;

import java.util.ArrayList;
import java.util.LinkedList;

import w_ave.org7.item.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
	
	private LayoutInflater mLayoutInflater;
// ������, ������� ����� ��������� ������� �������� �������� � �������
// �� � ����� ������������	
	private ArrayList<Item> hierarchyArray;
// ������ ��������� �������� ������ ��������. ���� ������ ���������� ������������ 
// ��������, �� ��� �����, ����� ��������� ������ hierarchyArray ��� 
// ��������/�������� ������-�� ��������	
	private ArrayList<Item> originalItems;
// ��� ����� ���-�� ��������� ����� �������� �� �������. ���������� �� � �������
	private LinkedList <Item> openItems;
	
	public ListAdapter (Context ctx, ArrayList<Item>items){
		mLayoutInflater = LayoutInflater.from(ctx);
		originalItems = items;
		
		hierarchyArray = new ArrayList<Item>();
		openItems = new LinkedList<Item>();
		
		generateHierarchy();
	}
	
	@Override
	public int getCount() {
		return hierarchyArray.size();
	}

	@Override
	public Object getItem(int position) {
		return hierarchyArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
// getView ���������� View ��� ������ ������ ������ ������. � ����� ������
// �� ������� ��� �� ����� item.xml (� ������� LayoutInflater)
// convertView ������ View ��� ���������� �������������
// parent - ��������, � �������� �������������� ��������� ������	
// � ������ �� ���������, �� ���� �� ������ ������ ��� ������� � ���� ���,
// �� ������� �� �� ����� item.xml � ������ �������� ��� ���� ��������� �����	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = mLayoutInflater.inflate(R.layout.row, null);
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		Item item = hierarchyArray.get(position);
		
		title.setText(item.getTitle());
	// ������������� ����������� � TextView	
		title.setCompoundDrawablesWithIntrinsicBounds(item.getIconResource(), 0, 0, 0);
		return convertView;
	}
	
	private void generateHierarchy(){
		hierarchyArray.clear();
// �������� ����������� �������, �������� �� ������ ��������� �������� ������		
		generateList(originalItems);
	}
	
// ������� ��������� ������ ������� ����������� ������� � hierarchyArray. ���� ������
// ������� ������ (�� ���� �� ���� � ������ openItems) �� ��� �������� ���� ����
// ��������� � �������� ��������� ������ ����������� ���������	
	private void generateList(ArrayList<Item> items) {
		for(Item i: items){
			hierarchyArray.add(i);
			if (openItems.contains(i)){
				generateList(i.getChilds());
			}
		}
	}
// ����� ���� ������� ������ �������� �� ��� �����, ��� ����� remove() 
//	� ArrayList ���������� true, ���� ����� ������� � ������� ��� �
//	�������������� ��� ������ ��� false � ���� ������ �������� �� ����. 
//	����� �� ������ �� ��������, ��� ��� ����� �������, ���� �� ��� ������
//	� �������� ������� ���� �� ��� ������. ����� �� ������ ������ ��������, 
//	�� �������� ��� ������� � ���� remove() ������ false (�� ���� � ������ 
//	�������� �������� �� ����), �� �� ��������� ��� ����. ����� ����� 
//	������������� ������ �� �������� � �������� notifyDataSetChanged() 
//	����� ������������ ������.	
	public void clickOnItem(int position){
		Item i = hierarchyArray.get(position);
		if (!openItems.remove(i))
			openItems.add(i);
		
		generateHierarchy();
		notifyDataSetChanged();
	}

}
