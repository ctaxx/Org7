package w_ave.org7;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import w_ave.org7.NewItemDialog.NewItemDialogListener;
import w_ave.org7.reading.FactoryBuilder;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements NewItemDialogListener{
	
	private static final int CM_CREATE_ID = 1;
	private static final int CM_DELETE_ID = 2;
	private static final int CM_CANCEL_ID = 3;
	ParserXML parser;
	ArrayListEx items;
	ListAdapter2 adapter;
	TextView textView1;
	Button startButton, stopButton, runButton;
	FactoryBuilder factoryBuilder = new FactoryBuilder();
	private Item selectedItem = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);

		textView1 = (TextView) findViewById(R.id.textView1);
		
		parser = new ParserXML();
		items = parser.parse();
		
		adapter = new ListAdapter2(this, items);
	//	adapter.setContext(this);
		
		runButton = (Button) findViewById(R.id.runButton);
		runButton.setEnabled(false);
		runButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Item item = adapter.getSelectedItem();
				Element element = (Element) item.getNode();
				
				if (element.hasAttribute("path")&& element.hasAttribute("type")){
				
					Intent intent = factoryBuilder.getIntent(element.getAttribute("type"), element.getAttribute("path"));
				
					// Verify it resolves
					PackageManager packageManager = getPackageManager();
					List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
					boolean isIntentSafe = activities.size() > 0;

					// Start an activity if it's safe
					if (isIntentSafe) {
						startActivityForResult(intent,1);
	// ToDo: this code is the same as in startButton
	// if you cancel opening the document you have to delete timestamp
	// and revert button to initial state
						Date date = new Date();
						element.setAttribute("timestamp", dateTimeToShortForm(date));
						parser.saveListToXML();
						
						showBottomInformation();
					}
				}
			}
		});
		
		startButton = (Button) findViewById(R.id.startButton);
		startButton.setEnabled(false);
		startButton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View wiew) {
				Item item = adapter.getSelectedItem();
				
				Date date = new Date();
				Element element = (Element) item.getNode();
	
				element.setAttribute("timestamp", dateTimeToShortForm(date));
				parser.saveListToXML();
				
				showBottomInformation();
			}
		});
		
		stopButton = (Button) findViewById(R.id.stopButton);
		stopButton.setEnabled(false);
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Item item = adapter.getSelectedItem();
				
				Element element = (Element) item.getNode();
				if (element.hasAttribute("timestamp")){	
					Date date = new Date();
					String resultString = new String(item.getTitle()+ " " 
						+ element.getAttribute("timestamp")+ " - " 
						+ dateTimeToShortForm(date));
					element.removeAttribute("timestamp");
					item = item.getAncestor();
					while(item.getAncestor()!=null){
						resultString = item.getTitle() + " --> " + resultString;
						item = item.getAncestor();
					};
							
					try {
						Writer writer = new FileWriter("/sdcard/Org7result.txt", true);
						for(int j = 0; j< resultString.length(); j++){
							writer.write(resultString.charAt(j));
						}	
						writer.write('\n');
						writer.flush();
						writer.close();
								
						} catch (Exception e) {
								e.printStackTrace();
							}
					parser.saveListToXML();
					
					showBottomInformation();
				}			
			}
		});
		
		ListView mList = (ListView) findViewById(R.id.listView);
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				adapter.clickOnItem(position);
				selectedItem = (Item) adapter.getItem(position);
				showBottomInformation();
			}
		});
		registerForContextMenu(mList);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_CREATE_ID, 0, "Новая запись");
		menu.add(0, CM_DELETE_ID, 0, "Удалить запись");
		menu.add(0, CM_CANCEL_ID, 0, "Отменить");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem mItem){
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) mItem.getMenuInfo();
		
		if (mItem.getItemId()== CM_CREATE_ID){
			adapter.setSelectedItem(acmi.position);
			
			FragmentManager fm = getFragmentManager();
			NewItemDialog newItemDialog = new NewItemDialog();
			newItemDialog.show(fm, "fragment_new_item");
		}
		if (mItem.getItemId()== CM_DELETE_ID){	
			
			items.removeItem((Item) adapter.getItem(acmi.position));
			parser.saveListToXML();
			
			adapter.refreshList();
			selectedItem = null;
			showBottomInformation();
		}
		if (mItem.getItemId()== CM_CANCEL_ID){
			return true;
		}
		return super.onContextItemSelected(mItem);
	}
	
	@Override
	public void onFinishNewItemDialog(String inputText){
		Toast.makeText(this, inputText, Toast.LENGTH_LONG).show();
		
		items.addItem(inputText, parser.createNode(inputText), adapter.getSelectedItem());
		adapter.refreshList();
		parser.saveListToXML();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		if (requestCode == 1){
//			if (resultCode == RESULT_OK){
				Toast.makeText(this, "we have received the data "+ resultCode, Toast.LENGTH_LONG).show();
//			}
		}
	}
	
	private String dateTimeToShortForm(Date date){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR)+"-"+
		(calendar.get(Calendar.MONTH)+1)+"-"+
		calendar.get(Calendar.DAY_OF_MONTH)+" "+
		calendar.get(Calendar.HOUR_OF_DAY)+":"+
		calendar.get(Calendar.MINUTE);
	}
	
	public void showBottomInformation(){
		runButton.setEnabled(false);
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
		textView1.setText("");
		if (selectedItem != null){
			textView1.setText(selectedItem.getTitle());
			Node node = selectedItem.getNode();
			Element element = (Element) node;
			if (element.hasAttribute("timestamp")){
				startButton.setEnabled(false);
				runButton.setEnabled(false);
				stopButton.setEnabled(true);
			}else{
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				if (element.hasAttribute("type")&& element.hasAttribute("path")){
					runButton.setEnabled(true);
				}else{
					runButton.setEnabled(false);
				}
			}
		}
	}
}
