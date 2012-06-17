package org.mobul.utils;

import java.io.File;
import android.os.Environment;

public class FileUtils {

	//public static final String pathBase = "/sdcard/mobul/";
	public static final String PATHBASE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/org.mobul/";

	public static void makeDir(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
}
