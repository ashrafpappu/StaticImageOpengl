package pappu.com.staticimageopengl;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TextureLoader {
    private int texture[] = new int[1];

    public TextureLoader() {
        texture[0] = 0;
    }

    public void generateTexture() {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        this.setTexParameters();
    }

    public void generateTextureOfSize(int width, int height) {
        this.generateTexture();
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.allocateDirect(4 * width * height));
    }

    public void activeTexture(int textureUnit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureUnit);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
    }

    public void uploadData(Bitmap data) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, data, 0);
        this.setTexParameters();
    }

    public void uploadData(Buffer data, int width, int height, int format) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, format,
                GLES20.GL_UNSIGNED_BYTE, data);
        this.setTexParameters();
    }

    private void setTexParameters() {
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private void deleteTexture() {
        GLES20.glDeleteTextures(1, texture, 0);
        texture[0] = 0;
    }
}
