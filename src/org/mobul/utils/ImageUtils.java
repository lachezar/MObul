package org.mobul.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {
	
	public static Bitmap getBitmapFromFile(File f, int imageMaxSize) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int scale = 1;
			if (o.outHeight > imageMaxSize || o.outWidth > imageMaxSize) {
				scale = (int) Math.pow(2, (int) Math.round(Math.log(imageMaxSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			b = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public static Bitmap get1mpBitmapFromFile(File f) {
		return getBitmapFromFile(f, 1280);
	}

	public static void saveJPG(Bitmap pic, File imageFile) {
		saveJPG(pic, imageFile, 100);
	}

	public static void saveJPG(Bitmap pic, File imageFile, int quality) {
		try {
			FileOutputStream out = new FileOutputStream(imageFile);
			pic.compress(Bitmap.CompressFormat.JPEG, quality, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File generateImageFile() {
		FileUtils.makeDir(FileUtils.PATHBASE);
		String photoPath = FileUtils.PATHBASE + new Date().getTime() + ".jpg";
		return new File(photoPath);
	}

}
