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
/** массив, который будет содержать видимые элементы иерархии и который
 *  будет показан в списке
 */
	private ArrayListEx hierarchyArray;
/** массив элементов верхнего уровн€ иерархии. Ётот список передаетс€ конструктору
 *  адаптера, он нам нужен, чтобы обновл€ть массив hierarchyArray при 
 *  открытии-закрытии какого-то элемента	
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
/** getView возвращает View дл€ каждой строки нашего списка. ¬ нашем случае
 * мы создаем его из файла item.xml (с помощью LayoutInflater)
 * convertView старый View дл€ повторного использовани€
 * parent - родитель, к которому присоедин€етс€ созданна€ строка.	
 * ¬ методе мы провер€ем, не была ли строка списка уже создана и если нет,
 * то создаем ее из файла item.xml и задаем здачени€ дл€ двух текстовый полей	
 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = mLayoutInflater.inflate(R.layout.row, null);
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		Item item = hierarchyArray.get(position);
		
		title.setText(item.getTitle());
	/** устанавливает изображение в TextView	
	 */
		title.setCompoundDrawablesWithIntrinsicBounds(item.getIconResource(), 0, 0, 0);
		title.setPadding(item.getLevel()*15, 0, 0, 0);
		return convertView;
	}
	
	private void generateHierarchy(){
		hierarchyArray.clear();
// вызываем рекурсивную функцию, передаем ей список элементов верхнего уровн€		
		generateList(originalItems, 0);
	}
	
/** функци€ добавл€ет каждый элемент переданного массива в hierarchyArray. ≈сли данный
 * элемент открыт, то она вызывает саму себ€
 * передава€ в качестве параметра массив нижесто€щих элементов	
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
