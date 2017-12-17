package pappu.com.staticimageopengl;

import android.graphics.Bitmap;



public interface TextureRenderer {
    void setShaderProgram(int shaderProgram);
    void uploadData(Bitmap data);
    void render(int textureUnit);
    void render(Bitmap data, int textureUnit);
}
