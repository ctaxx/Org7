package w_ave.org7;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.gson.JsonObject;

import w_ave.org7.NewItemDialog.NewItemDialogListener;
import w_ave.org7.item.FileItem;
import w_ave.org7.item.Item;
import w_ave.org7.reading.FactoryBuilder;
import w_ave.org7.utils.DateTimeUtils;
import w_ave.org7.utils.FileUtils;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
//import android.util.Log;
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

public class MainActivity extends Activity implements NewItemDialogListener {

	private static final int CM_CREATE_ID = 1;
	private static final int CM_CREATE_EXT_ID = 2;
	private static final int CM_DELETE_ID = 3;
	private static final int CM_CHECK_ID = 4;
	private static final int CM_CANCEL_ID = 5;

	private static final int STATE_CURRENT = 0;
	private static final int STATE_FILE_DLG = 1;

	private static final String resultPath = "/Org7result.txt";

	ParserXML parser;
	ArrayListEx items, fileItems;
	ListAdapter2 adapter, fileAdapter;
	TextView textView1;
	Button upButton, startButton, stopButton, runButton, openButton,
			cancelButton;
	ListView mList;
	FactoryBuilder factoryBuilder = new FactoryBuilder();
	// TODO too many entities show selection
	// private Item selectedItem = null;
	private int state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);

		textView1 = (TextView) findViewById(R.id.textView1);

		parser = new ParserXML();
		items = parser.parse();

		FileItem sdcard = new FileItem(
				Environment.getExternalStorageDirectory());
		fileItems = sdcard.getChilds();

		adapter = new ListAdapter2(this, items);
		fileAdapter = new ListAdapter2(this, fileItems);

		// adapter.setContext(this);
		upButton = (Button) findViewById(R.id.upButton);
		upButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Item item = adapter.getSelectedItem();
				Item ancestor = item.getAncestor();
				ArrayListEx childArray = ancestor.getChilds();
				int index = childArray.indexOf(item);

				if (index > 0) {
					ancestor.getNode().insertBefore(item.getNode(),
							childArray.get(index - 1).getNode());
					// TODO it can be unnecessary
					childArray.set(index, childArray.set(index - 1, item));
				}
				adapter.refreshList();
				parser.saveListToXML();
			}
		});

		runButton = (Button) findViewById(R.id.runButton);
		runButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Item item = adapter.getSelectedItem();
				Element element = (Element) item.getNode();

				if (element.hasAttribute("path")
						&& element.hasAttribute("type")) {

					Intent intent = factoryBuilder.getIntent(
							element.getAttribute("type"),
							element.getAttribute("path"));

					// Verify it resolves
					PackageManager packageManager = getPackageManager();
					List<ResolveInfo> activities = packageManager
							.queryIntentActivities(intent, 0);
					boolean isIntentSafe = activities.size() > 0;

					// Start an activity if it's safe
					if (isIntentSafe) {
						startActivityForResult(intent, 1);
						// TODO this code is the same as in startButton
						// TODO if you cancel opening the document you have to
						// delete timestamp and revert button to initial state
						Date date = new Date();
						element.setAttribute("timestamp",
								DateTimeUtils.dateTimeToShortForm(date));
						parser.saveListToXML();

						showBottomInformation();
					}
				}
			}
		});

		startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View wiew) {
				Item item = adapter.getSelectedItem();

				Date date = new Date();
				Element element = (Element) item.getNode();

				element.setAttribute("timestamp",
						DateTimeUtils.dateTimeToShortForm(date));
				parser.saveListToXML();

				showBottomInformation();
			}
		});

		stopButton = (Button) findViewById(R.id.stopButton);
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// todo: move to special method
				Item item = adapter.getSelectedItem();

				Element element = (Element) item.getNode();
				if (element.hasAttribute("timestamp")) {

					JsonObject json = new JsonObject();

					json.addProperty("item", item.getTitle());

					json.addProperty("begin", element.getAttribute("timestamp"));
					element.removeAttribute("timestamp");

					Date date = new Date();
					json.addProperty("end",
							DateTimeUtils.dateTimeToShortForm(date));

					json.addProperty("path", getChainOfAncestors(item));

					File resultFile = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ resultPath);
					FileUtils.saveJson(resultFile, json.toString());

					parser.saveListToXML();

					showBottomInformation();
				}
			}
		});

		openButton = (Button) findViewById(R.id.openButton);
		openButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				FragmentManager fm = getFragmentManager();
				NewItemDialog newItemDialog = new NewItemDialog(fileAdapter
						.getSelectedItem().getTitle());
				newItemDialog.show(fm, "fragment_new_item");
			}
		});

		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				setState(STATE_CURRENT);
				showBottomInformation();
			}
		});

		mList = (ListView) findViewById(R.id.listView);
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (state == STATE_CURRENT) {
					adapter.clickOnItem(position);
					// selectedItem = (Item) adapter.getItem(position);
					adapter.getItem(position).setSelected(true);
				}
				if (state == STATE_FILE_DLG) {
					fileAdapter.clickOnItem(position);
					// selectedItem = (Item) fileAdapter.getItem(position);
					fileAdapter.getItem(position).setSelected(true);
				}
				showBottomInformation();
			}
		});
		setState(STATE_CURRENT);
		showBottomInformation();
		registerForContextMenu(mList);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_CREATE_ID, 0, "Новая запись");
		menu.add(0, CM_CREATE_EXT_ID, 0, "Open media");
		menu.add(0, CM_DELETE_ID, 0, "Удалить запись");
		menu.add(0, CM_CHECK_ID, 0, "check");
		menu.add(0, CM_CANCEL_ID, 0, "Отменить");
	}

	// TODO context menu has not to work with files
	@Override
	public boolean onContextItemSelected(MenuItem mItem) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) mItem
				.getMenuInfo();

		if (mItem.getItemId() == CM_CREATE_ID) {
			adapter.setSelectedItem(acmi.position);

			showBottomInformation();

			FragmentManager fm = getFragmentManager();
			NewItemDialog newItemDialog = new NewItemDialog();
			newItemDialog.show(fm, "fragment_new_item");
		}
		if (mItem.getItemId() == CM_CREATE_EXT_ID) {
			adapter.setSelectedItem(acmi.position);

			fileAdapter.refreshList();
			setState(STATE_FILE_DLG);

			showBottomInformation();
		}
		if (mItem.getItemId() == CM_DELETE_ID) {

			items.removeItem((Item) adapter.getItem(acmi.position));
			parser.saveListToXML();

			adapter.refreshList();
			// selectedItem = null;
			showBottomInformation();
		}
		if (mItem.getItemId() == CM_CHECK_ID) {
			setChecked();
		}
		if (mItem.getItemId() == CM_CANCEL_ID) {
			return true;
		}
		return super.onContextItemSelected(mItem);
	}

	@Override
	public void onFinishNewItemDialog(String inputText) {
		Toast.makeText(this, inputText, Toast.LENGTH_LONG).show();

		if (state == STATE_CURRENT) {
			items.addItem(inputText, parser.createNode(inputText),
					adapter.getSelectedItem());
		}
		if (state == STATE_FILE_DLG) {
			FileItem fileItem = (FileItem) fileAdapter.getSelectedItem();
			// Toast.makeText(this, "path= "+ fileItem.getPath(),
			// Toast.LENGTH_LONG).show();
			// TODO too complicated
			items.addItem(inputText,
					parser.createNode(inputText, fileItem.getPath()),
					adapter.getSelectedItem());
			setState(STATE_CURRENT);
		}
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
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 1) {
			// if (resultCode == RESULT_OK){
			Toast.makeText(this, "we have received the data " + resultCode,
					Toast.LENGTH_LONG).show();
			// }
		}
	}

	public void showBottomInformation() {
		upButton.setEnabled(false);
		runButton.setEnabled(false);
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
		openButton.setEnabled(false);
		cancelButton.setEnabled(false);
		textView1.setText("");
		if (state == STATE_CURRENT) {
			Item item = adapter.getSelectedItem();
			if (item != null) {
				textView1.setText(item.getTitle());
				Node node = item.getNode();
				Element element = (Element) node;
				upButton.setEnabled(true);
				if (element.hasAttribute("timestamp")) {
					startButton.setEnabled(false);
					runButton.setEnabled(false);
					stopButton.setEnabled(true);
				} else {
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					if (element.hasAttribute("type")
							&& element.hasAttribute("path")) {
						runButton.setEnabled(true);
					} else {
						runButton.setEnabled(false);
					}
				}
			}
		}
		if (state == STATE_FILE_DLG) {
			Item item = fileAdapter.getSelectedItem();
			if (item != null) {
				textView1.setText(item.getTitle());
				openButton.setEnabled(true);
				cancelButton.setEnabled(true);
			}
		}
	}

	private void setState(int state) {
		this.state = state;
		// selectedItem = null;
		if (state == STATE_CURRENT) {
			mList.setAdapter(adapter);
			openButton.setVisibility(View.INVISIBLE);
			cancelButton.setVisibility(View.INVISIBLE);
			upButton.setVisibility(View.VISIBLE);
			runButton.setVisibility(View.VISIBLE);
			startButton.setVisibility(View.VISIBLE);
			stopButton.setVisibility(View.VISIBLE);
		}
		if (state == STATE_FILE_DLG) {
			mList.setAdapter(fileAdapter);
			openButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.VISIBLE);
			upButton.setVisibility(View.INVISIBLE);
			runButton.setVisibility(View.INVISIBLE);
			startButton.setVisibility(View.INVISIBLE);
			stopButton.setVisibility(View.INVISIBLE);
		}
	}

	private void setChecked() {
		// todo: move to special method
		Item item = adapter.getSelectedItem();

		JsonObject json = new JsonObject();

		json.addProperty("item", item.getTitle());

		Date date = new Date();
		json.addProperty("checktime", DateTimeUtils.dateTimeToShortForm(date));

		json.addProperty("path", getChainOfAncestors(item));

		File resultFile = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + resultPath);
		FileUtils.saveJson(resultFile, json.toString());
	}

	public String getChainOfAncestors(Item item) {
		String resultString = new String();
		item = item.getAncestor();
		while (item.getAncestor() != null) {
			resultString = item.getTitle() + " --> " + resultString;
			item = item.getAncestor();
		}
		return resultString;
	}
}
