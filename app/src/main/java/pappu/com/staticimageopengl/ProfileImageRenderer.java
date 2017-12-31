package pappu.com.staticimageopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;



public class ProfileImageRenderer {

    private int shaderProgram;
    private int uAlphaFactorLoc;

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    // buffer for texture coordinates
    private FloatBuffer textureCoordinatesBuffer;

    private int indexCount;


    private  ImageResourceRenderer imageResourceRenderer;
    private GLRenderHelper glRenderHelper;
    float[] vertices;

    int screenWidth, screenHeight;


    public ProfileImageRenderer(Context context, Bitmap bitmap, int screenWidth, int screenHeight, float[] projecMatrix,int pos){
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        this.setBuffer(screenWidth, screenHeight);

        shaderProgram = GLHelper.getShaderProgramm(context, R.raw.watermarkvertex_shader, R.raw.fragment_shader_withoutblend);

        uAlphaFactorLoc = GLES20.glGetUniformLocation(shaderProgram, "u_AlphaFactor");
        glRenderHelper = new GLRenderHelper(shaderProgram,projecMatrix);
        TextureRenderer textureRenderer = new TextureRendererNormal();
        textureRenderer.setShaderProgram(shaderProgram);

        imageResourceRenderer = new PNGRenderer(context,textureRenderer,bitmap);


    }

    float left, right,top ,bottom ;

    public void resetImagePosition(){
         left =0 ;
         right = screenWidth;
         top = 0;
         bottom = screenHeight;
    }



    public void updateVertices(long[] vertexPoints){



        float changeOfX=(float)vertexPoints[4]/500,changeOfY=(float)vertexPoints[5]/500;


//         left += changeOfX ;
//         right += changeOfX;
//         top += changeOfY;
//         bottom += changeOfY;


        left += vertexPoints[4] ;
        right += vertexPoints[4];
        top += vertexPoints[5];
        bottom += vertexPoints[5];


       // Log.d("vertices",""+changeOfX+"     "+changeOfY+"    "+left+"    "+right+"    "+top+"   "+bottom);

        vertices[0] = left;
        vertices[1] = top;
        vertices[2] = 0;
        vertices[3] = left;
        vertices[4] = bottom;
        vertices[5] = 0;
        vertices[6] = vertexPoints[0];
        vertices[7] = vertexPoints[1];
        vertices[8] = 0;
        vertices[9] = vertexPoints[2];
        vertices[10] = vertexPoints[3];
        vertices[11] = 0;
        vertices[12] = right;
        vertices[13] = bottom;
        vertices[14] = 0;
        vertices[15] = right;
        vertices[16] = top;
        vertices[17] = 0;

        updateVertexBuffer();
    }

    private void updateVertexBuffer(){
        vertexBuffer.position(0);
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }





    public void dispose() {
        imageResourceRenderer.dispose();
    }

    public void render(int textureUnit,float alphaFactor){
        if (!imageResourceRenderer.ready())
            return;

        GLES20.glUseProgram(shaderProgram);
        glRenderHelper.updateVertexAtrrib(vertexBuffer,3);
        glRenderHelper.updateTexCoordAtrrib(textureCoordinatesBuffer,2);
        glRenderHelper.updateProjectionMtrx();
        imageResourceRenderer.render(textureUnit);
        GLES20.glUniform1f(uAlphaFactorLoc, alphaFactor);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount,
                GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        glRenderHelper.disableVertexAttrib();
        glRenderHelper.disableTexCoordAttrib();
    }

    private  void setBuffer(int screenWidth, int screenHeight){

        vertices = new float[18];

        short[] indices = new short[]{

             //  0,1,2,2,1,4,0,2,5,2,4,3,5,2,3,3,4,5
                0,1,4,0,4,5
        };
        float[] textureCoordinates = new float[]{
                0.0f, 1.0f,
                0.0f, 0.f,
                0.47f,0.5f,
                0.67f,0.5f,
                1.0f, 0.f,
                1.0f, 1.0f
        };

        this.indexCount = indices.length;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        updateVertexBuffer();


        ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureCoordinatesBuffer = byteBuf.asFloatBuffer();
        textureCoordinatesBuffer.put(textureCoordinates);
        textureCoordinatesBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

}
