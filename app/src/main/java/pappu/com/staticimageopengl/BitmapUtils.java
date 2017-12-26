package pappu.com.staticimageopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

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
}
