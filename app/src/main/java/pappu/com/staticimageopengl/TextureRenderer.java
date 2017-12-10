package pappu.com.staticimageopengl;

import android.graphics.Bitmap;

/**
 * Created by nishu on 7/18/17.
 */

public interface TextureRenderer {
    void setShaderProgram(int shaderProgram);
    void uploadData(Bitmap data);
    void render(int textureUnit);
    void render(Bitmap data, int textureUnit);
}
