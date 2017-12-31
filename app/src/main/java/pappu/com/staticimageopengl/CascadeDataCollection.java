package pappu.com.staticimageopengl;

import java.util.ArrayList;
import java.util.List;



public class CascadeDataCollection {
    private static int resource[] = {
           R.raw.haarcascade_frontalface_alt2,
            R.raw.haarcascade_eye_tree_eyeglasses
    };

    private static String xmlFileNames[] = {
            "haarcascade_frontalface_alt2.xml",
            "haarcascade_eye_tree_eyeglasses"

    };

    private static CascadeType cascadeTypes[] = {
            CascadeType.FACE_DETECTION,
            CascadeType.EYE_DETECTION
    };


    public static List<CascadeData> getCascadeDataList() {
        List<CascadeData> cascadeDataList = new ArrayList<>();
        for (int i = 0; i < xmlFileNames.length; i++) {
            CascadeData cascadeData = new CascadeData(xmlFileNames[i], resource[i], cascadeTypes[i]);
            cascadeDataList.add(cascadeData);
        }
        return cascadeDataList;
    }

}
