package w_ave.org7.reading;

import android.content.Intent;
import android.net.Uri;

public class WebIntentFactory extends AbstractIntentFactory{

	public Intent getIntent(String path){
		Uri webpage = Uri.parse(path);
		intent = new Intent(Intent.ACTION_VIEW, webpage);
		return intent;
	}
}
