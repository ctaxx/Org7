package w_ave.org7.reading;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class PdfIntentFactory extends AbstractIntentFactory{

	public Intent getIntent(String path){
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+path);
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		return intent;
	}
}
