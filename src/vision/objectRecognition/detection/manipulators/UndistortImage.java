package vision.objectRecognition.detection.manipulators;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import vision.objectRecognition.detection.ImageManipulatorWithOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A Manipulator to remove lens distortion depending on pitch.
 * Constants are loaded from a separate class @ vision.objectRecognition.detection.manipulators.DistortionConstantsLoader
 */

public class UndistortImage extends ImageManipulatorWithOptions implements ActionListener {

    private static final String PITCH_NAME_PROPERTY = "PitchName";

    private Mat pitch0NewCameraMatrix;
    private Mat pitch0DistCoeffs;
    private Mat pitch0CameraMatrix;

    private Mat pitch1NewCameraMatrix;
    private Mat pitch1DistCoeffs;
    private Mat pitch1CameraMatrix;


    private enum PitchName {
        STANDARD_OUTPUT, PITCH_0, PITCH_1
    }

    private JComboBox<PitchName> pitchChooser = new JComboBox<>(PitchName.values());

    private PitchName pitchChosen =
            PitchName.STANDARD_OUTPUT;

    @Override
    public Component getModificationGUI() {
        return pitchChooser;
    }

    @Override
    public void saveModificationSettings(Properties prop) {
        prop.setProperty(PITCH_NAME_PROPERTY, pitchChosen.name());
    }

    @Override
    public void loadModificationSettings(Properties prop) {
        pitchChosen =
                PitchName.valueOf(prop.getProperty(PITCH_NAME_PROPERTY, pitchChosen.name()));
        pitchChooser.setPrototypeDisplayValue(pitchChosen);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox) e.getSource();
        pitchChosen = (PitchName) box.getSelectedItem();
    }

    public UndistortImage() {
        pitchChooser.addActionListener(this);
        pitchChooser.setPrototypeDisplayValue(pitchChosen);

        Mat[] matrices = DistortionConstantsLoader.loadMatrices();
        pitch0NewCameraMatrix = matrices[0];
        pitch0DistCoeffs = matrices[1];
        pitch0CameraMatrix = matrices[2];

        pitch1NewCameraMatrix = matrices[3];
        pitch1DistCoeffs = matrices[4];
        pitch1CameraMatrix = matrices[5];
    }

    @Override
    protected Mat run(Mat input) {

        Mat undistorted = new Mat();
        switch (pitchChosen) {
            case PITCH_0:
                Imgproc.undistort(
                        input,
                        undistorted,
                        pitch0CameraMatrix,
                        pitch0DistCoeffs,
                        pitch0NewCameraMatrix
                );

                 return undistorted;

            case PITCH_1:
                Imgproc.undistort(
                        input,
                        undistorted,
                        pitch1CameraMatrix,
                        pitch1DistCoeffs,
                        pitch1NewCameraMatrix
                );

                return undistorted;
            default:
                return input;
        }
    }

    @Override
    public Mat catchMat() {
        return manipulatedImage.clone();
    }

    @Override
    public void onFrameReceived(Mat image, long time) {
        manipulatedImage = run(image);
        if (manipulatedImage != null) {
            if (isDisplayed) {
                BufferedImage frame = catchFrame();
                if (frame != null && manipulatorDisplay.getGraphics() != null)
                    if (manipulatorDisplay != null)
                        manipulatorDisplay.getGraphics().drawImage(catchFrame(), 0, 0, null);
            }
            if (nextManipulator != null) {
                new Thread(() -> nextManipulator.onFrameReceived(manipulatedImage.clone(), time)).run();
            }
        }
    }
}
