package pappu.com.staticimageopengl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Pappu on 8/24/2016.
 */
public class GlSurfaceView extends GLSurfaceView {



    public GlSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

    }


}
