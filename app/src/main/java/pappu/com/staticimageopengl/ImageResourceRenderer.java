package pappu.com.staticimageopengl;

/**
 * Created by nishu on 7/22/17.
 */

public interface ImageResourceRenderer {
    int getImageQuantity();
    boolean ready();
    void render(int textureUnit);
    void reset();
    void dispose();
}
