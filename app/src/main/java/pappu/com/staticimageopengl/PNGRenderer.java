package pappu.com.staticimageopengl;

import android.content.Context;
import android.graphics.Bitmap;



public class PNGRenderer implements ImageResourceRenderer {

    private TextureRenderer textureRenderer;
    private Context context;

    public PNGRenderer(Context context, TextureRenderer textureRenderer, Bitmap data) {
        this.context = context;
        this.textureRenderer = textureRenderer;
        textureRenderer.uploadData(data);
    }

    @Override
    public int getImageQuantity() {return 1;}

    @Override
    public boolean ready() {return true;}

    @Override
    public void render(int textureUnit) {
        this.textureRenderer.render(textureUnit);
    }

    @Override
    public void reset() {}

    @Override
    public void dispose() {

    }
}
