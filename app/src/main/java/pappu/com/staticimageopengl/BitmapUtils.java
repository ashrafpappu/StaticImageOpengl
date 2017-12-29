package pappu.com.staticimageopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by user on 5/23/2017.
 */

public class BitmapUtils {

    public static Bitmap getBitmapFromAssetsImage(Context context, String imageFilePath) throws IOException {
        InputStream inputStream = context.getAssets().open(imageFilePath);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        return bitmap;
    }

    public static byte[] getBytesBitmap(Bitmap bitmap) throws IOException {
        int byteCount = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(byteCount);
        bitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    public static byte[] convertBitmapToGrayScale(Bitmap bitmap ) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int rowIndex = 0;
        byte[] grayBytes = new byte[width * height];
        for (int i = 0; i < height; i++ ) {
            rowIndex = i * width;
            for (int j = 0; j < width; j++) {
                int argb = bitmap.getPixel(j,i);
                int r = Color.red(argb);
                int g = Color.green(argb);
                int b = Color.blue(argb);
                int gray = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                grayBytes[rowIndex + j] = (byte) ((gray < 0) ? 0 : ((gray > 255) ? 255 : gray));
            }
        }
        return grayBytes;
    }


    public static Bitmap rotateBitmap(Bitmap original, float degrees) {
        int width = original.getWidth();
        int height = original.getHeight();


        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);

        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap,width,height,true);

        Log.d("angle","width : "+width+"   "+height+ "  "+scaledBitmap.getWidth());
//        Canvas canvas = new Canvas(rotatedBitmap);
      //  canvas.drawBitmap(original, 0.0f, 0.0f, null);

//        Matrix matrix = new Matrix();
//
//        matrix.postRotate(degrees);
//
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original,width,height,true);
//
//        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);








        return scaledBitmap;
    }
}
