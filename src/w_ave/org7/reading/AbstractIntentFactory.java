package w_ave.org7.reading;

import android.content.Intent;

public abstract class AbstractIntentFactory {
	
	protected Intent intent;
	
	// Build the intent
	public abstract Intent getIntent(String path);
}
