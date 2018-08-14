package w_ave.org7.reading;

import java.io.File;

import android.content.Intent;
import android.net.Uri;

public class DjvuIntentFactory extends AbstractIntentFactory{

	@Override
	public Intent getIntent(String path) {
//		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+path);
		File file = new File(path);
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/djvu");
		intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		return intent;
	}
}
