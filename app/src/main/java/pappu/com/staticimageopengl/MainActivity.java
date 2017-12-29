package pappu.com.staticimageopengl;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pappu.com.staticimageopengl.java_jni.Facedetection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    Button pickImage;
    private  ImageRenderer imageRenderer;
    private GlSurfaceView glSurfaceView;
    private FrameLayout parentView;
    private Facedetection facedetection;
    private List<CascadeData> cascadeDataList;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<long[]> eyePointsArray;
    ArrayList<long[]> faceRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        parentView = (FrameLayout)findViewById(R.id.preview);
        pickImage = (Button)findViewById(R.id.pick_image);
        pickImage.setOnClickListener(this);
        cascadeDataList = CascadeDataCollection.getCascadeDataList();
        facedetection = new Facedetection();

        moveCascadeDataToInternalDir();
        deserialize();


    }

    private void moveCascadeDataToInternalDir() {

        try {
            File cascadeDir = this.getFilesDir();


            Log.d(" desirealize  ",""  + cascadeDataList.size());
            for (int i = 0; i < cascadeDataList.size(); i++) {
                CascadeData cascadeData = cascadeDataList.get(i);
                File cascadeFile = new File(cascadeDir, cascadeData.getCascadeFileName());
                cascadeData.setCascadeFilePath(cascadeFile.getAbsolutePath());
                if (cascadeFile.exists()) {
                    continue;
                }

                InputStream is = this.getResources().openRawResource(cascadeData.getResourceId());
                FileOutputStream os = null;
                os = new FileOutputStream(cascadeFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();
            }
            //Log.d(" desirealize  ",""  + facedetection.deserialize(mCascadeFile.getAbsolutePath()));
            //cascadeDir.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deserialize() {
        int [] ids = new int[cascadeDataList.size()];
        String [] xmlFilePaths = new String[cascadeDataList.size()];
        for (int i = 0; i < cascadeDataList.size(); i++) {
            CascadeData cascadeData = cascadeDataList.get(i);
            ids[i] = cascadeData.getCascadeType().getId();
            xmlFilePaths[i] = cascadeData.getCascadeFilePath();
        }
        facedetection.deserialize(ids,xmlFilePaths);
    }




    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.pick_image:
               // loadImageFromGallery();
                Bitmap bitmap = null;
                eyePointsArray = new ArrayList<>();
              for(int i = 1;i<=4;i++) {
//                  if(i==3)
//                      continue;
                   bitmap = FileUtils.createMaskBitmap("pappu" + i, this);
                   long[] vertices = getVerticesOfImages(bitmap);



                  double angle = getRotateAngle(vertices);

                  Bitmap bitmap1 = null;
                  if(angle>0){
                     // vertices[1] = vertices[3];
                      bitmap1 = BitmapUtils.rotateBitmap(bitmap,(float) angle);
                      bitmapArrayList.add(bitmap1);
                     // vertices = null;
                      //vertices = getVerticesOfImages(bitmap1);
                      Log.d("angle",""+angle +"  "+vertices[0]+"  "+vertices[1]+"  "+vertices[2]+"  "+vertices[3]+"  "+bitmap.getWidth()+"  "+bitmap1.getWidth());
                  }else {
                      bitmapArrayList.add(bitmap);
                  }
                //vertices[4] = 0;
                //vertices[5] = 0;
                  eyePointsArray.add(vertices);


        }
                viewInGlview(bitmapArrayList);
                imageRenderer.drawFaceRect(faceRect);

                pickImage.setVisibility(View.GONE);
                break;
        }


    }


    double getRotateAngle(long[] vertices){
        double hypotanus = Math.sqrt((vertices[0]-vertices[2])*(vertices[0]-vertices[2])+(vertices[1]-vertices[3])*(vertices[1]-vertices[3]));
        double adjacent = vertices[0]-vertices[2];
        if(adjacent<0) adjacent*=-1;
        double angle = Math.acos(adjacent/hypotanus)*(180/3.1416);
        return angle;
    }

    void viewInGlview(ArrayList<Bitmap> bitmapArrayList){
        imageRenderer = new ImageRenderer(this, 1280, 720);
        glSurfaceView = new GlSurfaceView(this);
        glSurfaceView.setRenderer(imageRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        parentView.addView(glSurfaceView);
        pickImage.setVisibility(View.GONE);
        imageRenderer.setVertices(eyePointsArray);
        imageRenderer.setImage(bitmapArrayList);
        
    }




    long[] getVerticesOfImages(Bitmap bitmap){

        long[] eyePoints = new long[6];

            try {
                byte [] imageByte = BitmapUtils.getBytesBitmap(bitmap);
                int[] detected = facedetection.detecttionOfGalleryImage(imageByte,bitmap.getWidth(),bitmap.getHeight());
                if(detected!=null){
                    for(int j=0;j<detected.length;j++){
                        faceRect = facedetection.getRectangle(detected[j]);

                        if(detected[j]==CascadeType.FACE_DETECTION.getId()){
                            Log.d(" detectObject  ","detected Face " );
                        }
                        if(detected[j]==CascadeType.EYE_DETECTION.getId()){
                            long[] lefteye = faceRect.get(0);
                            long[] righteye = faceRect.get(1);
                            int lefteyex = (int)(lefteye[0]+lefteye[2])/2;
                            int lefteyey = (int)(lefteye[1]+lefteye[3])/2;
                            int righteyex = (int)(righteye[0]+righteye[2])/2;
                            int righteyey = (int)(righteye[1]+righteye[3])/2;




                            if(lefteyex>righteyex){
                                eyePoints[0] = righteyex;
                                eyePoints[1] = righteyey;
                                eyePoints[2] = lefteyex;
                                eyePoints[3] = lefteyey;
                            }else {
                                eyePoints[0] = lefteyex;
                                eyePoints[1] = lefteyey;
                                eyePoints[2] = righteyex;
                                eyePoints[3] = righteyey;
                            }

                            eyePoints[4] = (long)(513-eyePoints[0]);
                            eyePoints[5] = (long)(eyePoints[1]-(long)(965));
                            Log.d(" detectObject  ","detected eye "+faceRect.size()+"    " +lefteyex+"    "+lefteyey+"    "+righteyex+"    "+righteyey +"    "+eyePoints[4]);
                        }
                    }
                }
            }catch (IOException e)
            {

            }

        return eyePoints;


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
                    bitmapArrayList.add(bitmap);
                    bitmap = FileUtils.createMaskBitmap("pappu",this);
                    Log.d("MainActivity",""+imageEncoded+"   "+bitmap.getWidth());
                    bitmapArrayList.add(bitmap);
                    viewInGlview(bitmapArrayList);
                    pickImage.setVisibility(View.GONE);

                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
//        }



        super.onActivityResult(requestCode, resultCode, data);

    }

    void loadImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
    }

}
