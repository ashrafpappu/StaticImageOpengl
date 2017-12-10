package pappu.com.staticimageopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;



public class BannerRenderer {

    private int shaderProgram;
    private int uAlphaFactorLoc;

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    // buffer for texture coordinates
    private FloatBuffer textureCoordinatesBuffer;

    private int indexCount;

    private  ImageResourceRenderer imageResourceRenderer;
    private GLRenderHelper glRenderHelper;


    public BannerRenderer(Context context, Bitmap bitmap, int screenWidth, int screenHeight, float[] projecMatrix){
        this.setBuffer(screenHeight, screenWidth);
        shaderProgram = GLHelper.getShaderProgramm(context, R.raw.watermarkvertex_shader, R.raw.fragment_shader_withoutblend);
        uAlphaFactorLoc = GLES20.glGetUniformLocation(shaderProgram, "u_AlphaFactor");
        glRenderHelper = new GLRenderHelper(shaderProgram,projecMatrix);
        TextureRenderer textureRenderer = new TextureRendererNormal();
        textureRenderer.setShaderProgram(shaderProgram);

        imageResourceRenderer = new PNGRenderer(context,textureRenderer,bitmap);


    }

    public void dispose() {
        imageResourceRenderer.dispose();
    }

    public void render(int textureUnit){
        if (!imageResourceRenderer.ready())
            return;

        GLES20.glUseProgram(shaderProgram);
        glRenderHelper.updateVertexAtrrib(vertexBuffer,3);
        glRenderHelper.updateTexCoordAtrrib(textureCoordinatesBuffer,2);
        glRenderHelper.updateProjectionMtrx();
        imageResourceRenderer.render(textureUnit);
        GLES20.glUniform1f(uAlphaFactorLoc, 1);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount,
                GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        glRenderHelper.disableVertexAttrib();
        glRenderHelper.disableTexCoordAttrib();
    }

    private  void setBuffer(int screenWidth, int screenHeight){
        float left = 0 ;
        float right = screenWidth;
        float top = 0;
        float bottom = screenHeight;
        float[] vertices = new float[]{
                left,top,0,
                left,bottom,0,
                right,bottom,0,
                right,top,0

        };
        short[] indices = new short[]{
                0, 1, 3,1 ,3,2
        };
        float[] textureCoordinates = new float[]{
                0.f, 1.f,
                0.f, 0.f,
                1.f, 0.f,
                1.f, 1.f
        };

        this.indexCount = indices.length;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);


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
