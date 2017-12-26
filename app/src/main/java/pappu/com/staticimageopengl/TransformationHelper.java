package pappu.com.staticimageopengl;


import android.util.Log;

/**
 * Created by pappu on 1/1/17.
 */

public class TransformationHelper {


    public static long[] transformRectangle(long rect[],
                                            int viewWidth,
                                            int viewHeight,
                                            int imageWidth,
                                            int imageHeight)
    {

        if(imageWidth>imageHeight){
            imageHeight+=imageWidth;
            imageWidth=imageHeight-imageWidth;
            imageHeight-=imageWidth;
        }

        long transformedRect[] = new long[4];
        float ratiox,ratioy;
        ratiox = (float) viewWidth/imageWidth;
        ratioy = (float) viewHeight/imageHeight;

        Log.d("Transform","ratiox : "+ratiox +"  rect[0] "+rect[0] +" rect[1]  "+rect[1]);
            transformedRect[0] = (long) (rect[0] );
            transformedRect[1] = (long) ((rect[1]));
            transformedRect[2] = (long) (rect[2] );
            transformedRect[3] = (long) ((rect[3]) );

        return transformedRect;
    }

}
