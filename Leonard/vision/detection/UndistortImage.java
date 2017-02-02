package vision.detection;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import vision.DetectionCalibrationGUI;
import vision.ImageManipulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import static vision.utils.Converter.imageToMat;
import static vision.utils.Converter.matToImage;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * A Manipulator to remove lens distortion depending on pitch.
 * Constants are loaded from a separate class @ vision.detection.DistortionConstantsLoader
 */

public class UndistortImage extends ImageManipulator {


    public PitchChosenListener pitchChosenListener = new PitchChosenListener();

    private class PitchChosenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox box = (JComboBox) e.getSource();
            pitchChosen = (DetectionCalibrationGUI.PitchNames) box.getSelectedItem();
        }
    }

    private DetectionCalibrationGUI.PitchNames pitchChosen =
            DetectionCalibrationGUI.PitchNames.STANDARD_OUTPUT;

    private Mat pitch0NewCameraMatrix;
    private Mat pitch0DistCoeffs;
    private Mat pitch0CameraMatrix;

    private Mat pitch1NewCameraMatrix;
    private Mat pitch1DistCoeffs;
    private Mat pitch1CameraMatrix;

    public UndistortImage() {

        Mat[] matrices = DistortionConstantsLoader.loadMatrices();
        pitch0NewCameraMatrix = matrices[0];
        pitch0DistCoeffs = matrices[1];
        pitch0CameraMatrix = matrices[2];

        pitch1NewCameraMatrix = matrices[3];
        pitch1DistCoeffs = matrices[4];
        pitch1CameraMatrix = matrices[5];
    }

    @Override
    protected BufferedImage run(BufferedImage input) {

        Mat undistorted = new Mat();
        switch (pitchChosen) {
            case PITCH_0:
                Imgproc.undistort(
                        imageToMat(input),
                        undistorted,
                        pitch0CameraMatrix,
                        pitch0DistCoeffs,
                        pitch0NewCameraMatrix
                );

                 return matToImage(undistorted);

            case PITCH_1:
                Imgproc.undistort(
                        imageToMat(input),
                        undistorted,
                        pitch1CameraMatrix,
                        pitch1DistCoeffs,
                        pitch1NewCameraMatrix
                );

                return matToImage(undistorted);
            default:
                return input;
        }
    }
}
