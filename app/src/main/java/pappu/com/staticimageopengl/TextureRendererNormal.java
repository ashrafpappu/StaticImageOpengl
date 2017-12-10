package pappu.com.staticimageopengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;



public class TextureRendererNormal implements TextureRenderer {
    private TextureLoader textureLoader;
    private int samplerLocation;
    private int shaderProgram;

    public TextureRendererNormal() {
        textureLoader = new TextureLoader();
        textureLoader.generateTexture();
    }

    @Override
    public void setShaderProgram(int shaderProgram) {
        this.shaderProgram = shaderProgram;
        samplerLocation = GLES20.glGetUniformLocation(this.shaderProgram, "u_texture");
    }

    @Override
    public void uploadData(Bitmap data) {
        textureLoader.uploadData(data);
    }

    @Override
    public void render(int textureUnit) {
        textureLoader.activeTexture(textureUnit);
        GLES20.glUniform1i(samplerLocation, textureUnit);
    }

    @Override
    public void render(Bitmap data, int textureUnit) {
        this.render(textureUnit);
        textureLoader.uploadData(data);
    }
}
