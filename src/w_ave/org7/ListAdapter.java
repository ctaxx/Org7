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
// массив, который будет содержать видимые элементы иерархии и который
// мы и будем отрисовывать	
	private ArrayList<Item> hierarchyArray;
// массив элементов верхнего уровн€ иерархии. Ётот список передаетс€ конструктору 
// адаптера, он нам нужен, чтобы обновл€ть массив hierarchyArray при 
// открытии/закрытии какого-то элемента	
	private ArrayList<Item> originalItems;
// нам нужно как-то сохран€ть какие элементы мы открыли. «апоминаем их в массиве
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
// getView возвращает View дл€ каждой строки нашего списка. ¬ нашем случае
// мы создаем его из файла item.xml (с помощью LayoutInflater)
// convertView старый View дл€ повторного использовани€
// parent - родитель, к которому присоедин€етс€ созданна€ строка	
// в методе мы провер€ем, не была ли строка списка уже создана и если нет,
// то создаем ее из файла item.xml и задаем здачени€ дл€ двух текстовый полей	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = mLayoutInflater.inflate(R.layout.row, null);
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		Item item = hierarchyArray.get(position);
		
		title.setText(item.getTitle());
	// устанавливает изображение в TextView	
		title.setCompoundDrawablesWithIntrinsicBounds(item.getIconResource(), 0, 0, 0);
		return convertView;
	}
	
	private void generateHierarchy(){
		hierarchyArray.clear();
// вызываем рекурсивную функцию, передаем ей список элементов верхнего уровн€		
		generateList(originalItems);
	}
	
// функци€ добавл€ет каждый элемент переданного массива в hierarchyArray. ≈сли данный
// элемент открыт (то есть он есть в списке openItems) то она вызывает саму себ€
// передава€ в качестве параметра массив нижесто€щих элементов	
	private void generateList(ArrayList<Item> items) {
		for(Item i: items){
			hierarchyArray.add(i);
			if (openItems.contains(i)){
				generateList(i.getChilds());
			}
		}
	}
// «десь весь принцип работы построен на том факте, что метод remove() 
//	у ArrayList возвращает true, если такой элемент в массиве был и
//	соответственно был удален или false Ч если такого элемента не было. 
//	 огда мы тыкаем по элементу, нам его нужно закрыть, если он был открыт
//	и наоборот открыть если он был закрыт. „тобы не делать лишних проверок, 
//	мы пытаемс€ его удалить и если remove() вернул false (то есть в списке 
//	открытых элемента не было), то мы добавл€ем его туда. ѕосле этого 
//	перестраиваем массив из иерархии и вызываем notifyDataSetChanged() 
//	чтобы перерисовать список.	
	public void clickOnItem(int position){
		Item i = hierarchyArray.get(position);
		if (!openItems.remove(i))
			openItems.add(i);
		
		generateHierarchy();
		notifyDataSetChanged();
	}

}
