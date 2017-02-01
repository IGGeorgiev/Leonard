package vision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static vision.DetectionCalibrationGUI.*;

/**
 * Created by Ivan Georgiev (s1410984) on 01/02/17.
 * Class to manage Properties I/O
 */
class DetectionPropertiesManager {

    static void loadValues() {
        File saveFile = new File("Leonard/vision/calibration/pre_saved_values/calibrationOptions.properties");
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            if (saveFile.exists()) {
                fis = new FileInputStream(saveFile);
                prop.load(fis);

                int threshR = Integer.valueOf(prop.getProperty("threshold_R"));
                int threshG = Integer.valueOf(prop.getProperty("threshold_G"));
                int threshB = Integer.valueOf(prop.getProperty("threshold_B"));
                int gaussBlur = Integer.valueOf(prop.getProperty("gaussianBlur"));
                int dilation = Integer.valueOf(prop.getProperty("dilation"));
                int erosion = Integer.valueOf(prop.getProperty("erosion"));

                rthresholdSlider.setValue(threshR);
                gthresholdSlider.setValue(threshG);
                bthresholdSlider.setValue(threshB);
                gaussianBlurSlider.setValue(gaussBlur);
                dilationSlider.setValue(dilation);
                erosionSlider.setValue(erosion);

            }
        } catch (IOException io) {
            System.out.println("No Save File found!");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.out.println("No save file to close.");
                }
            }
        }

    }

    static void saveValues() {
        File saveFile = new File("Leonard/vision/calibration/pre_saved_values/calibrationOptions.properties");
        Properties prop = new Properties();
        FileOutputStream fos = null;

        try {
            if (!saveFile.exists())
                saveFile.createNewFile();

            fos = new FileOutputStream(saveFile);

            int threshR = rthresholdSlider.getValue();
            int threshG = gthresholdSlider.getValue();
            int threshB = bthresholdSlider.getValue();
            int gaussBlur = gaussianBlurSlider.getValue();
            int dilation = dilationSlider.getValue();
            int erosion = erosionSlider.getValue();

            prop.setProperty("threshold_R", String.valueOf(threshR));
            prop.setProperty("threshold_G", String.valueOf(threshG));
            prop.setProperty("threshold_B", String.valueOf(threshB));
            prop.setProperty("gaussianBlur", String.valueOf(gaussBlur));
            prop.setProperty("dilation", String.valueOf(dilation));
            prop.setProperty("erosion", String.valueOf(erosion));

            prop.store(fos, "Detection Data Save File");

        } catch (IOException io) {
            System.out.println("Unable to create a save file!");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println("Unable to close file.");
                }
            }

        }
    }
}
