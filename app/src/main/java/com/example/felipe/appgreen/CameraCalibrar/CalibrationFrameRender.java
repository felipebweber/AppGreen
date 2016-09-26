package com.example.felipe.appgreen.CameraCalibrar;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

/**
 * Created by Felipe on 22/09/2016.
 */

public class CalibrationFrameRender extends FrameRender{
    public CalibrationFrameRender(CameraCalibrator calibrator) {
        mCalibrator = calibrator;
    }

    @Override
    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgbaFrame = inputFrame.rgba();
        Mat grayFrame = inputFrame.gray();
        mCalibrator.processFrame(grayFrame, rgbaFrame);

        return rgbaFrame;
    }
}
