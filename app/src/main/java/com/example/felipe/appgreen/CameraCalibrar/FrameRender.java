package com.example.felipe.appgreen.CameraCalibrar;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

/**
 * Created by Felipe on 22/09/2016.
 */

public abstract class FrameRender {
    protected CameraCalibrator mCalibrator;

    public abstract Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
}
