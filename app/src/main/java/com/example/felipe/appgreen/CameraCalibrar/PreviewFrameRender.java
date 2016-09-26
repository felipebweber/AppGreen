package com.example.felipe.appgreen.CameraCalibrar;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

/**
 * Created by Felipe on 22/09/2016.
 */

public class PreviewFrameRender extends FrameRender{
    @Override
    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }
}
