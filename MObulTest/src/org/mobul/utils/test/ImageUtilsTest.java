package org.mobul.utils.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.mobul.utils.ImageUtils;

import android.graphics.Bitmap;
import junit.framework.TestCase;

public class ImageUtilsTest extends TestCase {

	public void testResizeBitmap() {
		
		// test.jpg - 2816x2112
		
		File f = new File("/sdcard/DCIM/test.jpg");
		File copy = new File("/sdcard/DCIM/copy.jpg");
		
		//copyOriginalFile(f, copy);
		
		Bitmap bmp = ImageUtils.get1mpBitmapFromFile(f);
		
		assertEquals(1408, bmp.getWidth());
		assertEquals(1056, bmp.getHeight());
		
		ImageUtils.saveJPG(bmp, copy, 90);
		
		Bitmap bmp2 = ImageUtils.getBitmapFromFile(copy, 3456);
		assertEquals(1408, bmp2.getWidth());
		assertEquals(1056, bmp2.getHeight());
		
	}

	@SuppressWarnings("unused")
	private void copyOriginalFile(File f, File copy) {
		
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(copy));
			
			byte[] buf = new byte[1024*1024];
			int readBytes = 0;
			while (true) {
				readBytes = bis.read(buf);
				if (readBytes == -1) {
					break;
				} else {
					bos.write(buf, 0, readBytes);
				}
			}
			bis.close();
			bos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
