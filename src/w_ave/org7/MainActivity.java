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
import w_ave.org7.reading.AbstractIntentFactory;
import w_ave.org7.reading.PdfIntentFactory;
import w_ave.org7.reading.WebIntentFactory;
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
	AbstractIntentFactory intentFactory;

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
				
//					String type = "pdf";
					String type = element.getAttribute("type");
//					String path = "/Memory.pdf";
					String path = element.getAttribute("path");
					Intent intent = null;
				
					if (type.equals("pdf")){
						intentFactory = new PdfIntentFactory();
						intent = intentFactory.getIntent(path);
					}
				
					if (type.equals("html")){
						intentFactory = new WebIntentFactory();
						intent = intentFactory.getIntent(path);
					}
				
					// Verify it resolves
					PackageManager packageManager = getPackageManager();
					List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
					boolean isIntentSafe = activities.size() > 0;

					// Start an activity if it's safe
					if (isIntentSafe) {
						startActivity(intent);
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
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
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
					startButton.setEnabled(true);
					runButton.setEnabled(true);
					stopButton.setEnabled(false);
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
				showBottomInformation((Item) adapter.getItem(position));
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
	
	private String dateTimeToShortForm(Date date){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR)+"-"+
		(calendar.get(Calendar.MONTH)+1)+"-"+
		calendar.get(Calendar.DAY_OF_MONTH)+" "+
		calendar.get(Calendar.HOUR_OF_DAY)+":"+
		calendar.get(Calendar.MINUTE);
	}
	
	public void showBottomInformation(Item item){
		textView1.setText(item.getTitle());
		Node node = item.getNode();
		Element element = (Element) node;
		if (element.hasAttribute("timestamp")){
			startButton.setEnabled(false);
			runButton.setEnabled(false);
			stopButton.setEnabled(true);
		}else{
			startButton.setEnabled(true);
			runButton.setEnabled(true);
			stopButton.setEnabled(false);
		}
	}
}
