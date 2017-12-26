package pappu.com.staticimageopengl;

/**
 * Created by Zakir Hossain on 11/21/17.
 */

public enum CascadeType {
    NONE(0),
    FACE_DETECTION(1),
    EYE_DETECTION(2);

    private int id;
    CascadeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
