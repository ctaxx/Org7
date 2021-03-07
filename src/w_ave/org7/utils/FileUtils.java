package w_ave.org7.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class FileUtils {
//	private static final String resultPath = "/Org7result.txt";
	
	public static void saveJson(File file, String string){
		try {	
			Writer writer = new FileWriter(file, true);
			writer.write(string);
			writer.write('\n');
			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
