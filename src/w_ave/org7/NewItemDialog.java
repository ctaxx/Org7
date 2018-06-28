package w_ave.org7;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewItemDialog extends DialogFragment implements OnEditorActionListener{
	
	private String primaryText;
	
	public interface NewItemDialogListener{
		void onFinishNewItemDialog(String inputText);
	}
	
	EditText editText;
	
	public NewItemDialog(){
		
	}
	
	public NewItemDialog(String primaryText){
		this.primaryText = primaryText;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		
		View view = inflater.inflate(R.layout.fragment_new_item, container);
		editText = (EditText) view.findViewById(R.id.editText);
		editText.setText(primaryText);
		getDialog().setTitle("Hello!");
		
		editText.setOnEditorActionListener(this);
		
		return view;
		
	}

	@Override
	public boolean onEditorAction(TextView v, int actionID, KeyEvent event) {
		if(EditorInfo.IME_ACTION_DONE == actionID){
			NewItemDialogListener activity = (NewItemDialogListener) getActivity();
			activity.onFinishNewItemDialog(editText.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}

}
