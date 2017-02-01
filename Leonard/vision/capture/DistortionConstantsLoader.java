package vision.capture;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by Ivan Georgiev (s1410984) on 31/01/17.
 *
 * These constants are taken from Project Venus - https://github.com/patchgreen99/venus
 */
class DistortionConstantsLoader {

    private final static double[] pitch0NewCameraMatrix = new double[] {
            368.84759521484375, 0.0, 330.1697498466274 ,
             0.0, 399.0144348144531, 260.8702191937118 ,
            0.0, 0.0, 1.0
    };

    private final static double[] pitch0DistCoeffs   = new double[]
            {-0.1884435833399087, 0.06431797306545822, -0.002320217516340173,
                    -0.00043173314817984086, -0.017394027049864598 };

    private final static double[] pitch0CameraMatrix =new double[] {
            416.3701490805172, 0.0, 328.9051457812704,
            0.0, 425.7129820390328, 260.05185042747104,
            0.0, 0.0, 1.0
    };

    private final static double[] pitch1NewCameraMatrix = new double[] {
            737.6613159179688, 0.0, 301.69352630824505,
            0.0, 806.8742065429688, 249.5121153848213,
            0.0, 0.0, 1.0
    };

    private final static double[] pitch1DistCoeffs   = new double[]
            { -0.8312713046926262, 0.9585898323424059, 0.004142889467292573,
                    0.0016643540576712656, -0.4182301848006621 };

    private final static double[] pitch1CameraMatrix =new double[] {
            844.7725414044543, 0.0, 305.30154615328377,
            0.0, 866.4574601099283, 247.81476162327613,
            0.0, 0.0, 1.0
    };

    static Mat[] loadMatrices() {
        Mat[] matrices = new Mat[6];

        matrices[0] = new Mat(3,3, CvType.CV_64F);
        matrices[0].put(0, 0, pitch0NewCameraMatrix);

        matrices[1] = new Mat(1,5, CvType.CV_64F);
        matrices[1].put(0,0, pitch0DistCoeffs);


        matrices[2] = new Mat(3,3, CvType.CV_64F);
        matrices[2].put(0, 0, pitch0CameraMatrix);

        matrices[3] = new Mat(3,3, CvType.CV_64F);
        matrices[3].put(0, 0, pitch1NewCameraMatrix);

        matrices[4] = new Mat(1,5, CvType.CV_64F);
        matrices[4].put(0,0, pitch1DistCoeffs);


        matrices[5] = new Mat(3,3, CvType.CV_64F);
        matrices[5].put(0, 0, pitch1CameraMatrix);

        return matrices;
    }
}
