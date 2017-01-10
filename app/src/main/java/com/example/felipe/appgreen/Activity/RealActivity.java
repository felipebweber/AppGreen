package com.example.felipe.appgreen.Activity;

import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.felipe.appgreen.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

public class RealActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OPENCV";
    private CameraBridgeViewBase mOpenCvCameraView;
    private CameraManager mCamera;

    private float k = 0.68f;
    private final float kRST = 0.68f;
    private float t = 20.0f;
    private final float tRST = 20.0f;

    //private final float erocao = (float) 2.5;
    private int erocao = 3;
    private final int erocaoRST = 3;
    private int dilatacao = 7;
    private final int dilatacaoRST = 7;

    private int minimoCruzamento = 25;
    private final int minimoCruzamentoRST = 25;

    Mat sampledImage = null;
    Mat originalImage = null;
    Mat outErode = null;
    Mat outDilate = null;
    Mat rgbImage = null;
    Mat rgbGreen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_real);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        //mOpenCvCameraView.setCameraIndex();
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //Mat src = inputFrame.gray();
        //Mat mRgbaT = new Mat();

        Mat src = inputFrame.rgba();
        //Mat mRgba = new Mat();

        // Rotate mRgba 90 degrees
        // Rotate clockwise 90 degrees
//        Core.transpose(mRgba, mRgbaT);
//
//        Core.flip(mRgbaT, mRgbaT, 1);
//        mRgba = inputFrame.rgba();

//        Mat mRgbaT = mRgba.t();
//        Core.flip(mRgba.t(), mRgbaT, 1);
//        Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());

//        Core.transpose(mRgba, mRgbaT);
//        Imgproc.resize(mRgbaT, mRgbaT, mRgbaT.size(), 0,0, 0);
//        Core.flip(mRgbaT, mRgba, 1 );

        if(src != null){
            return  processar(src);
        }

//        Mat cannyEdges = new Mat();
//        Imgproc.Canny(src, cannyEdges, 10, 100);


        //return  processar(src);
        return src;
    }

    private Mat processar(Mat input){


        rgbGreen = new Mat();

        sampledImage.copyTo(rgbGreen);
        //Size size = rgbGreen.size();
        for(int i=0; i<sampledImage.rows(); i++){
            for(int j=0; j<sampledImage.cols(); j++){
                double[] data = sampledImage.get(i,j);
                // condição necessária para detecção do verde
                if((data[1] > k * (data[0]+data[2])) & (data[0]+data[2] > t)){

                    data[0]=255;
                    data[1]=255;
                    data[2]=255;
                    rgbGreen.put(i,j,data);

                }else{
                    data[0]=0;
                    data[1]=0;
                    data[2]=0;
                    rgbGreen.put(i, j, data);
                }
            }
        }


        outErode = new Mat();
        outDilate = new Mat();

        // Operações morfologicas erosão e dilatação
        Imgproc.erode(rgbGreen, outErode, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erocao, erocao)));
        Imgproc.dilate(outErode, outDilate, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilatacao, dilatacao)));


        Mat outDilateGray = new Mat();

        // Converte o resultado da dilatação para gray
        Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY);

        List<MatOfPoint> contours = new ArrayList<>();
        contours.clear();
        Mat hierarchy = new Mat();

        // Função retorna os contornos
        Imgproc.findContours(outDilateGray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


        Log.i("CONTORNO", "CONTORNO: " + contours.size());

        // Momentos são utilizados para encontrar a soma dos pixels que compõem o contorno
        List<Moments> mu = new ArrayList<>(contours.size());

        // Variavel auxilar para encontrar o centroide do objeto.
        List<Point> points = new ArrayList<>();


        // For responsavel por encontrar todos os centroides
        for (int i = 0; i < contours.size(); i++) {
            mu.add(i, Imgproc.moments(contours.get(i), true));
            Log.i("CONTOURS", "CONTOURS: " + contours.get(i).toList());
            Log.i("CONTOURS", "CONTOURSMOMENTS: " + mu.size());
            Log.i("CONTOURS", "CONTOURSMOMENTS: " + contours.size());

            Moments p = mu.get(i);

            Point centroid = new Point();

            // Obtem os centroides de x e y
            centroid.x = p.get_m10() / p.get_m00();
            centroid.y = p.get_m01() / p.get_m00();

            points.add(new Point(centroid.x,centroid.y)); // Mudanca para usar ransac
            Log.i("PONTO", "X-Y: " + centroid);
            Log.i("POINTS", "X-Y: " + points.get(i).x);

            // Adiciona o centroide no objeto
            Core.circle(outDilate, centroid, 5, new Scalar(255, 0, 0), -1);
        }


        // For utilizado para remover o branco da imagem
        for(int i=0; i<outDilate.rows(); i++){
            for(int j=0; j<outDilate.cols(); j++){
                double[] data = outDilate.get(i,j);
                if(data[0] == 255 & data[1] == 255 & data[2] == 255){
                    data[0]=0;
                    data[1]=0;
                    data[2]=0;
                    outDilate.put(i,j,data);

                }else{

                }
            }
        }


        // generate gray scale and blur
        Mat grayLine = new Mat(); // faz parte do antigo
        Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY); // faz parte do antigo
        Imgproc.blur(outDilateGray, grayLine, new Size(3,3)); // faz parte do antigo

        // detect the edges
        Mat edges = new Mat(); // faz parte do antigo
        int lowThreshold = 25; // faz parte do antigo
        int ratio = 3; // faz parte do antigo

        Imgproc.Canny(grayLine, edges, lowThreshold, lowThreshold * ratio); // faz parte do antigo

        Mat lines = new Mat(); // matriz auxiliar

        Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, minimoCruzamento, 300, 200); // faz parte do antigo, cebola novo (13-10-16)

            /*
            - Saida do detector de bordas (edges)
            - Vetor que armazena os parametros de linhas detectadas (lines)
            - Resolucao de parametro em pixels, 1 pixel (1)
            - A resolucao do parametro em radianos, 1 grau (Math.PI/180)
            - Numero minimo de cruzamentos para detectar uma linha (55)
            - Numero minimo de pontos que podem formar uma linha (70)
            - Diferenca maxima entre dois pontos a serem considerados na mesma linha (200)
             */


        for(int i = 0; i < lines.cols(); i++) { // faz parte do antigo
            double[] val = lines.get(0, i); // faz parte do antigo
            Log.i("PONTOS", "PONTO1 " +val[0]+" "+val[1]);
            Log.i("PONTOS", "PONTO2 " +val[2]+" "+val[3]);


//            if((val[0]> 30) && (val[0] < 90)){
//                bicoUm.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
//                bicoUm.setText("▇▇");
//            }
//            else if((val[0]>= 90) && (val[0] < 150)){
//                bicoDois.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
//                bicoDois.setText("▇▇");
//            }
//            else if((val[0]>= 150) && (val[0] < 180)){
//                bicoTres.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
//                bicoTres.setText("▇▇");
//            }
//            else if((val[0]> 180) && (val[0] < 270)){
//                bicoQuatro.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
//                bicoQuatro.setText("▇▇");
//            }
//            else if((val[0]>= 270) && (val[0] < 330)){
//                bicoCinco.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
//                bicoCinco.setText("▇▇");
//            }

            Core.line(outDilate, new Point(val[2], val[3]), new Point(val[0], val[1]), new Scalar(0, 0, 255), 2); // faz parte do antigo
            val[0] = -1;
        } // faz parte do antigo

        //displayImage(outDilate);




        //return rgbGreen;
        return outDilate;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
