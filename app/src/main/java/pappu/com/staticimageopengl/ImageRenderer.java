package pappu.com.staticimageopengl;

import android.content.Context;
import android.graphics.Bitmap;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;


import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;




public class ImageRenderer implements GLSurfaceView.Renderer{

    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];
    private boolean maskRenderOn = false;
    private int width = 0;
    private  int height = 0;
    int offsceenPreviewWidth, offScreenPreviewHeight;
    private static final int RECORDING_OFF = 0;
    private static String TAG = "ImageRenderer";
    private ArrayList<ProfileImageRenderer> imageDrawList;


    private int[] offScreenFrameBufferId = new int[1];
    private int[] offScreenTexureId = new int[1];

    private Context context;
    private boolean show = false;

    PreviewInfo previewInfo;
    ArrayList<Bitmap> bitmapArrayList;



    public ImageRenderer(Context context, int previewWidth, int previewHeight){
        this.context = context;
        this.offsceenPreviewWidth = (int) Math.ceil(previewWidth / 16.0) * 16;
        this.offScreenPreviewHeight = previewHeight;
        imageDrawList = new ArrayList<>();

    }


    public void clearAllImageRendering() {
        if (imageDrawList.size()>0) {
            for(int i = 0;i<imageDrawList.size();i++){
                imageDrawList.get(i).dispose();
            }
            imageDrawList.clear();
        }
    }

    private void createWatermarkRenderer() {
        this.clearAllImageRendering();
        Log.d(TAG,"create>>>>>"+bitmapArrayList.size());
        for (int j=0;j<bitmapArrayList.size();j++){
            Log.d(TAG,"create>>>>>");
//            if(j==0){
//                imageDrawList.add(new ProfileImageRenderer(context,bitmapArrayList.get(j),500,previewInfo.preiviewHeight,mtrxProjectionAndView,j));
//            }else {
                imageDrawList.add(new ProfileImageRenderer(context,bitmapArrayList.get(j),previewInfo.previewWidth,previewInfo.preiviewHeight,mtrxProjectionAndView,j));
           // }

        }

    }

    void setImage(ArrayList<Bitmap> bitmapArrayList){
        this.bitmapArrayList = bitmapArrayList;
        Log.d(TAG,"create>>>>> first "+bitmapArrayList.size());

    }



    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        // clear display color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }


    void createOffScreenTexture(){
        GLES20.glGenFramebuffers(1, offScreenFrameBufferId, 0);

        GLES20.glGenTextures(1,offScreenTexureId,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, offScreenTexureId[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, offScreenPreviewHeight, offsceenPreviewWidth, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, width, height);
        previewInfo = AppUtils.getadjustedPreview(width,height, offsceenPreviewWidth, offScreenPreviewHeight);

        Log.d("ImageRender","First>>>"+offsceenPreviewWidth+"  "+previewInfo.preiviewHeight);

        GLES20.glViewport(previewInfo.offsetX, previewInfo.offsetY, previewInfo.previewWidth, previewInfo.preiviewHeight);

        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        Matrix.orthoM(mtrxProjection, 0, 0f, previewInfo.previewWidth, 0.0f, previewInfo.preiviewHeight, 0, 10);
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

        createOffScreenTexture();
        createWatermarkRenderer();

    }



    void onScreenDraw(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glViewport(previewInfo.offsetX, previewInfo.offsetY, previewInfo.previewWidth, previewInfo.preiviewHeight);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        renderOnBuffer();

    }

    void offScreenRender(){

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, offScreenFrameBufferId[0]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, offScreenTexureId[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, offScreenTexureId[0], 0);
        GLES20.glViewport(0, 0, offScreenPreviewHeight, offsceenPreviewWidth);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        renderOnBuffer();
    }

    float in=(float) 0.0,in1 = (float)1.0;
    int firstpos = 0,seconpos=1;
    void renderOnBuffer(){

        if(imageDrawList.size()>0){
            Log.d("ImageRender","render>>>");


                    imageDrawList.get(seconpos).render(1,in);
                        in+=(float) (1.0/1000.0)*3.0;
                        if(in>1){
                            in=0;
                            seconpos=++seconpos%imageDrawList.size();
                        }

                    imageDrawList.get(firstpos).render(1,in1);
                        in1-=(float) (1.0/1000.0)*3.0;
                        if(in1<0){
                            in1=1;
                            firstpos=++firstpos%imageDrawList.size();
                        }
        }


    }


    @Override
    public void onDrawFrame(GL10 gl10) {

        onScreenDraw();

    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsceenPreviewWidth() {
        return offScreenPreviewHeight;
    }

    public int getOffScreenPreviewHeight() {
        return offsceenPreviewWidth;
    }


}
