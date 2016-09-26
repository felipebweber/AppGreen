package com.example.felipe.appgreen.CameraCalibrar;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Felipe on 22/09/2016.
 */

public class UndistortionFrameRender extends FrameRender{
    public UndistortionFrameRender(CameraCalibrator calibrator) {
        mCalibrator = calibrator;
    }

    @Override
    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat renderedFrame = new Mat(inputFrame.rgba().size(), inputFrame.rgba().type());
        Imgproc.undistort(inputFrame.rgba(), renderedFrame,
                mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients());

        return renderedFrame;
    }
}
