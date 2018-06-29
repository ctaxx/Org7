package w_ave.org7.reading;

import android.content.Intent;

public class FactoryBuilder {

	AbstractIntentFactory intentFactory;
	
	
	public Intent getIntent(String type, String path){
		
		if (type.equals("pdf")){
			intentFactory = new PdfIntentFactory();
			return intentFactory.getIntent(path);
		}

		if (type.equals("html")){
			intentFactory = new WebIntentFactory();
			return intentFactory.getIntent(path);
		}
	
		if (type.equals("fb2")){
			intentFactory = new FB2IntentFactory();
			return intentFactory.getIntent(path);
		}
	
		if (type.equals("epub")){
			intentFactory = new EpubIntentFactory();
			return intentFactory.getIntent(path);
		}
		
		if (type.equals("mp4")||type.equals("avi")){
			intentFactory = new VideoIntentFactory();
			return intentFactory.getIntent(path);
		}
		return null;	
	}
	
}
