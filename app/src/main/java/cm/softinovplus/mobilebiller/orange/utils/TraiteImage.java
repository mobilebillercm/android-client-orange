package cm.softinovplus.mobilebiller.orange.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
/////////////////////////////////////////////////////////////////////////////
///
///		Liens utils:
///     https://stackoverflow.com/questions/14530058/how-can-i-print-an-image-on-a-bluetooth-printer-in-android
///
///
///
///
///
////////////////////////////////////////////////////////////////////////////
import cm.softinovplus.mobilebiller.orange.BluetoothPrinterActivity;

public class TraiteImage {
	private BitSet dots;
	private int mWidth;
	private int mHeight;
	private String mStatus;
	private Context context;

	public TraiteImage(Context context){
		this.context = context;
	}
	public JSONArray processImage(String path) {
		if (path != null && path.length() > 0) {
				List<byte[]> listebyte = get_print_image(path);
				byte[] byteImage = getImageToPrint(listebyte);
                int leng = byteImage.length;
				JSONArray jsonarray = new JSONArray();
				for(int k = 0; k<leng; k++) jsonarray.put(byteImage[k]);
			return jsonarray;
		} else {
			return null;
		}

	}
	
	
	public String convertBitmap(Bitmap inputBitmap) {

		    mWidth = inputBitmap.getWidth();
		    mHeight = inputBitmap.getHeight();

		    convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
		    mStatus = "ok";
		    return mStatus;

	}
	
	private void convertArgbToGrayscale(Bitmap bmpOriginal, int width, int height) {
		    int pixel;
		    int k = 0;
		    int B = 0, G = 0, R = 0;
		    dots = new BitSet();
		    try {

		        for (int x = 0; x < height; x++) {
		            for (int y = 0; y < width; y++) {
		                // get one pixel color
		                pixel = bmpOriginal.getPixel(y, x);

		                // retrieve color of all channels
		                R = Color.red(pixel);
		                G = Color.green(pixel);
		                B = Color.blue(pixel);
		                // take conversion up to one single value by calculating
		                // pixel intensity.
		                R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
		                // set bit into bitset, by calculating the pixel's luma
		                if (R < 55) {                       
		                    dots.set(k);//this is the bitset that i'm printing
		                }
		                k++;
		            }
		        }
		    } catch (Exception e) {
		    }
		}
		
		private List<byte[]> get_print_image(String file) {
			List<byte[]> ls = new ArrayList<byte[]>();
			
			InputStream in = null;
			try {
				in = this.context.getResources().getAssets().open(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			BufferedInputStream bis = new BufferedInputStream(in);
			Bitmap bitmap = BitmapFactory.decodeStream(bis);
		        String convert_ok = convertBitmap(bitmap);
		        if(convert_ok.equals("ok")){
		        ls.add(PrinterCommands.SET_LINE_SPACING_24);
				ls.add(PrinterCommands.FEED_LINE);
		        int offset = 0;
                    byte nL = (byte) (bitmap.getWidth() % 256);
                    byte nH = (byte) ((bitmap.getWidth()/256 > 3)?3:bitmap.getWidth()/256);
                    byte select_bit_image_mode[] = {0x1B, 0x2A, 33,nL, nH};

		        while (offset < bitmap.getHeight()) {
                    ls.add(select_bit_image_mode);

		            for (int x = 0; x < bitmap.getWidth(); x++) {

		                for (int k = 0; k < 3; k++) {

		                    byte slice = 0;
		                    for (int b = 0; b < 8; b++) {
		                        int y = (((offset / 8) + k) * 8) + b;
		                        int i = (y * bitmap.getWidth()) + x;
		                        boolean v = false;
		                        if (i < dots.length()) {
		                            v = dots.get(i);
		                        }
		                        slice |= (byte) ((v ? 1 : 0) << (7 - b));
		                    }
		                    byte[] s = new byte[1];
		                    s[0] = slice;
		                    ls.add(s);
		                }
		            }
		            offset += 24;
		            ls.add(PrinterCommands.FEED_LINE);

		        }

		        ls.add(PrinterCommands.SET_LINE_SPACING_30);
		        return ls;
		}else{
			return null;
		}
	}
		
		public byte[] getImageToPrint(List<byte[]> ls){
			int leng = 0;
			for(byte[] b:ls) leng += b.length;
			byte[] retVal = new byte[leng];
			int k = 0;
			for(byte[] b:ls){
				for(int i= 0; i<b.length; i++) retVal[k++] = b[i];
			}
			return retVal;
		}
		
		public static class PrinterCommands {
			public static byte[] FEED_LINE = {10};
			public static byte[] SET_LINE_SPACING_24 = {0x1B, 0x33, 0};
			public static byte[] SET_LINE_SPACING_30 = {0x1B, 0x33, 0};


		}
	
}
