package com.example.felipe.appgreen.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.felipe.appgreen.R;
import com.example.felipe.appgreen.Tools.PreferenciasEeD;
import com.example.felipe.appgreen.Tools.PreferenciasKeT;
import com.example.felipe.appgreen.Tools.PreferenciasMinCruz;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private Button btMaisK;
    private Button btMenosK;
    private Button btResetKeT;
    private Button btMaisT;
    private Button btMenosT;

    private TextView textViewK;
    private TextView textViewT;

    private Button btMaisE;
    private Button btMenosE;
    private Button btMaisD;
    private Button btMenosD;
    private Button btResetEeD;

    private TextView textViewE;
    private TextView textViewD;

    private Button btMaisC;
    private Button btMenosC;

    private TextView textViewC;

    private File diretorio;
    private String nomeDiretorio = "Split";
    private String diretorioApp;
    Bitmap bitmap = null;
    ArrayList<Bitmap> rev = new ArrayList<>();
    int cont = 0;
    int posicaoBitmap = 0;
    int b = 0;

    private static final int SELECT_PICTURE = 1;
    private static final String  TAG = "OPENCV";
    private String selectedImagePath;


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

//    private final double k = 0.68;
//    private final double t = 20;
//
//    //private final float erocao = (float) 2.5;
//    private final int erocao = 3;
//    private final int dilatacao = 7;

    Mat sampledImage = null;
    Mat originalImage = null;
    Mat outErode = null;
    Mat outDilate = null;
    Mat rgbImage = null;
    Mat rgbGreen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        btMaisK = (Button) findViewById(R.id.btMaisK);
        btMaisT = (Button) findViewById(R.id.btMaisT);
        btResetKeT = (Button) findViewById(R.id.btResetKeT);

        btMenosK = (Button) findViewById(R.id.btMenosK);
        btMenosT = (Button) findViewById(R.id.btMenosT);

        textViewK = (TextView) findViewById(R.id.textViewK);
        textViewT = (TextView) findViewById(R.id.textViewT);

        btMaisE = (Button) findViewById(R.id.btMaisE);
        btMaisD = (Button) findViewById(R.id.btMaisD);
        btResetEeD = (Button) findViewById(R.id.btResetEeD);

        btMenosE = (Button) findViewById(R.id.btMenosE);
        btMenosD = (Button) findViewById(R.id.btMenosD);

        textViewE = (TextView) findViewById(R.id.textViewE);
        textViewD = (TextView) findViewById(R.id.textViewD);

        btMaisC = (Button) findViewById(R.id.btMaisC);
        btMenosC = (Button) findViewById(R.id.btMenosC);

        textViewC = (TextView) findViewById(R.id.textViewC);

//        Permissao.validaPermissoes(1, this, permissoesNecessarias );

        carregaValoresKeT();
        carregaValoresEeD();
        carregaValorMinCruz();

        btMenosK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(VideoActivity.this);
                float kk = preferenciasKeT.get_k();

                NumberFormat formato = new DecimalFormat("0.##");

                textViewK.setText(formato.format(kk)+"");
                //textViewK.setText(teste+"");
                kk -= 0.01f;
                preferenciasKeT.salvarDados_k(kk);

                kk = preferenciasKeT.get_k();
                k=kk;

                textViewK.setText(formato.format(kk)+"");
                //textViewK.setText(teste+"");
            }
        });

        btMaisK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(VideoActivity.this);
                float kk = preferenciasKeT.get_k();

                NumberFormat formato = new DecimalFormat("0.##");

                textViewK.setText(formato.format(kk)+"");

                //textViewK.setText(k+"");
                kk += 0.01f;
                preferenciasKeT.salvarDados_k(kk);

                kk = preferenciasKeT.get_k();
                k=kk;
                textViewK.setText(formato.format(kk)+"");
                //textViewK.setText(k+"");
            }
        });

        btResetKeT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(VideoActivity.this);

                preferenciasKeT.salvarDados_k(kRST);
                preferenciasKeT.salvarDados_t(tRST);

                carregaValoresKeT();
            }
        });

        btMenosT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(VideoActivity.this);
                float tt = preferenciasKeT.get_t();

                NumberFormat formato = new DecimalFormat("0.##");

                textViewT.setText(formato.format(t)+"");
                //textViewK.setText(teste+"");
                tt -= 0.01f;
                preferenciasKeT.salvarDados_t(tt);

                tt = preferenciasKeT.get_t();
                t=tt;

                textViewT.setText(formato.format(tt)+"");
            }
        });

        btMaisT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(VideoActivity.this);
                float tt = preferenciasKeT.get_t();

                NumberFormat formato = new DecimalFormat("0.##");

                textViewT.setText(formato.format(tt)+"");
                //textViewK.setText(teste+"");
                tt += 0.01f;
                preferenciasKeT.salvarDados_t(tt);

                tt = preferenciasKeT.get_t();
                t=tt;

                textViewT.setText(formato.format(tt)+"");
            }
        });

        btMaisE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(VideoActivity.this);
                int ee = preferenciasEeD.get_E();

                //textViewE.setText(ee+"");
                ee += 1;
                preferenciasEeD.salvarDados_E(ee);

                ee = preferenciasEeD.get_E();
                erocao=ee;

                textViewE.setText(ee+"");
            }
        });

        btMenosE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(VideoActivity.this);
                int ee = preferenciasEeD.get_E();

                //textViewE.setText(ee+"");
                ee -= 1;
                preferenciasEeD.salvarDados_E(ee);

                ee = preferenciasEeD.get_E();
                erocao=ee;

                textViewE.setText(ee+"");
            }
        });

        btMaisD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(VideoActivity.this);
                int dd = preferenciasEeD.get_D();

                //textViewE.setText(ee+"");
                dd += 1;
                preferenciasEeD.salvarDados_D(dd);

                dd = preferenciasEeD.get_D();
                dilatacao=dd;

                textViewD.setText(dd+"");
            }
        });

        btMenosD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(VideoActivity.this);
                int dd = preferenciasEeD.get_D();

                //textViewE.setText(ee+"");
                dd -= 1;
                preferenciasEeD.salvarDados_D(dd);

                dd = preferenciasEeD.get_D();
                dilatacao=dd;

                textViewD.setText(dd+"");
            }
        });

        btResetEeD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(VideoActivity.this);

                preferenciasEeD.salvarDados_E(erocaoRST);
                preferenciasEeD.salvarDados_D(dilatacaoRST);

                carregaValoresEeD();
            }
        });

        btMaisC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasMinCruz preferenciasMinCruz = new PreferenciasMinCruz(VideoActivity.this);
                int minimoCruzamentoGet = preferenciasMinCruz.get_C();

                minimoCruzamentoGet += 1;
                preferenciasMinCruz.salvarDados_C(minimoCruzamentoGet);

                minimoCruzamentoGet = preferenciasMinCruz.get_C();
                minimoCruzamento = minimoCruzamentoGet;

                textViewC.setText(minimoCruzamentoGet+"");
            }
        });

        btMenosC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasMinCruz preferenciasMinCruz = new PreferenciasMinCruz(VideoActivity.this);
                int minimoCruzamentoGet = preferenciasMinCruz.get_C();

                minimoCruzamentoGet -= 1;
                preferenciasMinCruz.salvarDados_C(minimoCruzamentoGet);

                minimoCruzamentoGet = preferenciasMinCruz.get_C();
                minimoCruzamento = minimoCruzamentoGet;

                textViewC.setText(minimoCruzamentoGet+"");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater(); // exibir menu na tela
        inflater.inflate(R.menu.menu_video, menu);
        return true;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        //Intent intent = new Intent("org.opencv.engine.BIND");
        //intent.setPackage("org.opencv.engine");
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_openGallary) {
            Intent intent = new Intent();
            intent.setType("video/*"); // Mudar video
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE); // Mudar video
            return true;
        }

        //Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //startActivityForResult(i, RESULT_LOAD_IMAGE);

        if (id == R.id.action_processar){

            ImageView iv = (ImageView) findViewById(R.id.imageView);
                //Utils.bitmapToMat(b,sampledImage);
            while(b<rev.size()){
                iv.setImageBitmap(rev.get(b));
                bitmapToMat(rev.get(b));
                b++;
                //break;
                if(sampledImage==null){
                    Context context = getApplicationContext();
                    CharSequence text = "Você precisa carregar uma imagem primeiro";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return true;
                }
                rgbGreen = new Mat();

                sampledImage.copyTo(rgbGreen);
                Size size = rgbGreen.size();
                for(int i=0; i<sampledImage.rows(); i++){
                    for(int j=0; j<sampledImage.cols(); j++){
                        double[] data = sampledImage.get(i,j);
                        if((data[1] > k * (data[0]+data[2])) & (data[0]+data[2] > t)){
                            //if(data[1] > k * (data[0]+data[2])){
                            data[0]=255;
                            data[1]=255;
                            data[2]=255;
                            //rgbGreen.put(i,j,255);
                            rgbGreen.put(i,j,data);

                            //Log.i("DATA","NO PRIMEIRO IF ");
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
//            Imgproc.erode(rgbGreen, outErode, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));
//            Imgproc.dilate(outErode, outDilate, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 40)));
                Imgproc.erode(rgbGreen, outErode, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erocao, erocao)));
                Imgproc.dilate(outErode, outDilate, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilatacao, dilatacao)));


                Mat outDilateGray = new Mat();
                Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY);

                List<MatOfPoint> contours = new ArrayList<>();
                contours.clear();
                Mat hierarchy = new Mat();
                Imgproc.findContours(outDilateGray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

//            Imgproc.drawContours(outDilate, contours, -1, new Scalar(0, 255, 0), 3);
                Log.i("CONTORNO", "CONTORNO: " + contours.size());

//            // daqui
                List<Moments> mu = new ArrayList<>(contours.size());
                //ArrayList<Moments> mmu = new ArrayList<>(contours.size());

                List<Point> points = new ArrayList<>(); // Mudanca para usar ransac


                for (int i = 0; i < contours.size(); i++) {
                    //Moments moments = Imgproc.moments(contours.get(i));
                    mu.add(i, Imgproc.moments(contours.get(i), true));
                    Log.i("CONTOURS", "CONTOURS: " + contours.get(i).toList());
                    Log.i("CONTOURS", "CONTOURSMOMENTS: " + mu.size());
                    Log.i("CONTOURS", "CONTOURSMOMENTS: " + contours.size());

                    Moments p = mu.get(i);

                    Point centroid = new Point();

                    centroid.x = p.get_m10() / p.get_m00();
                    centroid.y = p.get_m01() / p.get_m00();
                    //points.add(i, new Point(centroid.x,centroid.y));
                    points.add(new Point(centroid.x,centroid.y)); // Mudanca para usar ransac
                    Log.i("PONTO", "X-Y: " + centroid);
                    Log.i("POINTS", "X-Y: " + points.get(i).x);

                    Core.circle(outDilate, centroid, 5, new Scalar(255, 0, 0), -1);
                }


                for(int i=0; i<outDilate.rows(); i++){
                    for(int j=0; j<outDilate.cols(); j++){
                        double[] data = outDilate.get(i,j);
                        if(data[0] == 255 & data[1] == 255 & data[2] == 255){
                            data[0]=0;
                            data[1]=0;
                            data[2]=0;
                            outDilate.put(i,j,data);

                            //Log.i("DATA","NO PRIMEIRO IF ");
                        }else{
//                        data[0]=0;
//                        data[1]=0;
//                        data[2]=0;
//                        outDilate.put(i, j, data);
                        }
                    }
                }



                // ################# Versao antiga de deteccao de linha comeca aqui ####################

                // generate gray scale and blur
                Mat grayLine = new Mat(); // faz parte do antigo
                Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY); // faz parte do antigo
                Imgproc.blur(outDilateGray, grayLine, new Size(3,3)); // faz parte do antigo

                // detect the edges
                Mat edges = new Mat(); // faz parte do antigo
                int lowThreshold = 25; // faz parte do antigo
                int ratio = 3; // faz parte do antigo


                Imgproc.Canny(grayLine, edges, lowThreshold, lowThreshold * ratio); // faz parte do antigo

                Mat lines = new Mat(); // faz parte do antigo
                //Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 55, 70, 200);

                //Imgproc.HoughLinesP(edges, lines, 5, Math.PI/180, 50, 420, 400); // faz parte do antigo, morango
                //Imgproc.HoughLinesP(edges, lines, 5, Math.PI/180, 50, 300, 200); // faz parte do antigo, cebola
                Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, minimoCruzamento, 350, 580); // faz parte do antigo, cebola novo (13-10-16)
                //Imgproc.HoughLinesP(edges, lines, 10, Math.PI/180, 50, 420, 400);

                //Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 10, 50, 0);
                //Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 10);
            /*
            - Saida do detector de bordas (edges)
            - Vetor que armazena os parametros de linhas detectadas (lines)
            - Resolucao de parametro em pixels, 1 pixel (1)
            - A resolucao do parametro em radianos, 1 grau (Math.PI/180)
            - Numero minimo de cruzamentos para detectar uma linha (55)
            - Numero minimo de pontos que podem formar uma linha (70)
            - Diferenca maxima entre dois pontos a serem considerados na mesma linha (200)
             */
                //Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100, 0, 0);

//            Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100, 50, 0);

//            Log.i("LINE", "LINE "+lines.dump().toString());

                for(int i = 0; i < lines.cols(); i++) { // faz parte do antigo
                    double[] val = lines.get(0, i); // faz parte do antigo

                    Core.line(outDilate, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2); // faz parte do antigo
                    //Core.line(outDilate, pt1, pt2, new Scalar(0, 0, 255), 3);
                }
                displayImage(outDilate);
                break;
            }



        }



        if(id == R.id.action_linha){
            if(sampledImage==null){
                Context context = getApplicationContext();
                CharSequence text = "Você precisa carregar uma imagem primeiro";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            }


            rgbGreen = new Mat();

            sampledImage.copyTo(rgbGreen);
            Size size = rgbGreen.size();
            for(int i=0; i<sampledImage.rows(); i++){
                for(int j=0; j<sampledImage.cols(); j++){
                    double[] data = sampledImage.get(i,j);
                    if((data[1] > k * (data[0]+data[2])) & (data[0]+data[2] > t)){
                        //if(data[1] > k * (data[0]+data[2])){
                        data[0]=255;
                        data[1]=255;
                        data[2]=255;
                        //rgbGreen.put(i,j,255);
                        rgbGreen.put(i,j,data);

                        //Log.i("DATA","NO PRIMEIRO IF ");
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
//            Imgproc.erode(rgbGreen, outErode, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));
//            Imgproc.dilate(outErode, outDilate, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 40)));
            Imgproc.erode(rgbGreen, outErode, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erocao, erocao)));
            Imgproc.dilate(outErode, outDilate, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilatacao, dilatacao)));


            Mat outDilateGray = new Mat();
            Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY);

            List<MatOfPoint> contours = new ArrayList<>();
            contours.clear();
            Mat hierarchy = new Mat();
            Imgproc.findContours(outDilateGray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

//            Imgproc.drawContours(outDilate, contours, -1, new Scalar(0, 255, 0), 3);
            Log.i("CONTORNO", "CONTORNO: " + contours.size());

//            // daqui
            List<Moments> mu = new ArrayList<>(contours.size());
            //ArrayList<Moments> mmu = new ArrayList<>(contours.size());

            List<Point> points = new ArrayList<>(); // Mudanca para usar ransac


            for (int i = 0; i < contours.size(); i++) {
                //Moments moments = Imgproc.moments(contours.get(i));
                mu.add(i, Imgproc.moments(contours.get(i), true));
                Log.i("CONTOURS", "CONTOURS: " + contours.get(i).toList());
                Log.i("CONTOURS", "CONTOURSMOMENTS: " + mu.size());
                Log.i("CONTOURS", "CONTOURSMOMENTS: " + contours.size());

                Moments p = mu.get(i);

                Point centroid = new Point();

                centroid.x = p.get_m10() / p.get_m00();
                centroid.y = p.get_m01() / p.get_m00();
                //points.add(i, new Point(centroid.x,centroid.y));
                points.add(new Point(centroid.x,centroid.y)); // Mudanca para usar ransac
                Log.i("PONTO", "X-Y: " + centroid);
                Log.i("POINTS", "X-Y: " + points.get(i).x);

                Core.circle(outDilate, centroid, 5, new Scalar(255, 0, 0), -1);
            }


            for(int i=0; i<outDilate.rows(); i++){
                for(int j=0; j<outDilate.cols(); j++){
                    double[] data = outDilate.get(i,j);
                    if(data[0] == 255 & data[1] == 255 & data[2] == 255){
                        data[0]=0;
                        data[1]=0;
                        data[2]=0;
                        outDilate.put(i,j,data);

                        //Log.i("DATA","NO PRIMEIRO IF ");
                    }else{
//                        data[0]=0;
//                        data[1]=0;
//                        data[2]=0;
//                        outDilate.put(i, j, data);
                    }
                }
            }



            // ################# Versao antiga de deteccao de linha comeca aqui ####################

            // generate gray scale and blur
            Mat grayLine = new Mat(); // faz parte do antigo
            Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY); // faz parte do antigo
            Imgproc.blur(outDilateGray, grayLine, new Size(3,3)); // faz parte do antigo

            // detect the edges
            Mat edges = new Mat(); // faz parte do antigo
            int lowThreshold = 25; // faz parte do antigo
            int ratio = 3; // faz parte do antigo


            Imgproc.Canny(grayLine, edges, lowThreshold, lowThreshold * ratio); // faz parte do antigo

            Mat lines = new Mat(); // faz parte do antigo
            //Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 55, 70, 200);

            //Imgproc.HoughLinesP(edges, lines, 5, Math.PI/180, 50, 420, 400); // faz parte do antigo, morango
            //Imgproc.HoughLinesP(edges, lines, 5, Math.PI/180, 50, 300, 200); // faz parte do antigo, cebola
            Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, 25, 300, 200); // faz parte do antigo, cebola novo (13-10-16)
            //Imgproc.HoughLinesP(edges, lines, 10, Math.PI/180, 50, 420, 400);

            //Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 10, 50, 0);
            //Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 10);
            /*
            - Saida do detector de bordas (edges)
            - Vetor que armazena os parametros de linhas detectadas (lines)
            - Resolucao de parametro em pixels, 1 pixel (1)
            - A resolucao do parametro em radianos, 1 grau (Math.PI/180)
            - Numero minimo de cruzamentos para detectar uma linha (55)
            - Numero minimo de pontos que podem formar uma linha (70)
            - Diferenca maxima entre dois pontos a serem considerados na mesma linha (200)
             */
            //Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100, 0, 0);

//            Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100, 50, 0);

//            Log.i("LINE", "LINE "+lines.dump().toString());

            for(int i = 0; i < lines.cols(); i++) { // faz parte do antigo
                double[] val = lines.get(0, i); // faz parte do antigo

                Core.line(outDilate, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2); // faz parte do antigo
                //Core.line(outDilate, pt1, pt2, new Scalar(0, 0, 255), 3);
            } // faz parte do antigo

            displayImage(outDilate);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){ // Mudar video
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();

                selectedImagePath = getPath(selectedImageUri); // getPath funcao
//                Log.i(TAG, "selectedImagePath: " + selectedImagePath);
//                Log.i(TAG, "ANTES DISPLAY ");

                //loadImage(selectedImagePath); // loadImage funcao
                loadVideo(selectedImagePath);

                //displayImage(sampledImage);
            }
        }
    }

    private void displayImage(Mat image){
        Bitmap bitmap = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(image, bitmap);

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bitmap);
    }

    private void bitmapToMat(Bitmap bitmap){
        Mat imageMat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = bitmap.copy(Bitmap.Config.RGB_565, true);
        Utils.bitmapToMat(myBitmap32, imageMat);
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2RGB);
        sampledImage = new Mat();

        Imgproc.resize(imageMat, sampledImage,new Size(360,480));
        //imageMat.copyTo(sampledImage);
    }

    private String getPath(Uri uri){
        if(uri == null){
            return null;
        }
        //String[] projection = {MediaStore.Images.Media.DATA};
        //String[] projection = {MediaStore.Images.Media.DATA}; //Mudar video
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null){
            //int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); // Mudar video
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }


    private void loadVideo(String path){

//        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//        mediaMetadataRetriever.setDataSource(path);
//        String durata = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        int durata_millisec = Integer.parseInt(durata);
//        int durata_video_micros = durata_millisec * 1000;
//        int durata_segundi = durata_millisec / 1000;
//        String bitrate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
//        int fps = 10;
//        int numeroFrameCaptured = fps * durata_segundi;
//        ArrayList<Bitmap> frames;
//        frames = new ArrayList<>();
//        Bitmap bmFrame;
//        int totalFotogramas = (durata_millisec)/(1000 * fps);
//        Log.i("FOTO", "FOTO  "+durata_millisec);
//
//        for(int i=0; i< durata_millisec; i+=100){
//            //bmFrame = mediaMetadataRetriever.getFrameAtTime(100000*i, MediaMetadataRetriever.OPTION_CLOSEST);
//            bmFrame = mediaMetadataRetriever.getFrameAtTime(1000*i, MediaMetadataRetriever.OPTION_CLOSEST);
//            frames.add(bmFrame);
//        }
//
//        try {
////                ImageView iv = (ImageView) findViewById(R.id.imageView);
////                iv.setImageBitmap(bitmap);
//                saveFrames(frames);
//
//            }catch (IOException e){
//                e.printStackTrace();
//            }
        //Bitmap bitmap = null;
        //int j = 0;
        //File videoFile = new File(path);
//        Mat teste = Highgui.imread(path);
//        teste.release();

        //VideoCapture videoCapture = new VideoCapture(Integer.parseInt(path));



        Uri videoFileUri = Uri.parse(path);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        retriever.setDataSource(path);

        //retriever.setDataSource(videoFile.getAbsolutePath());
//        ArrayList<Bitmap> rev = new ArrayList<>();


        //Create a new Media Player
        //MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoFileUri);
        MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoFileUri);
        //int millis = mp.getDuration();
        int millis = mp.getDuration();

        Log.i("DURACAO", "DURACAO "+millis);

//        try{
//            for(int i = 0; i < millis; i+=100){
//                Bitmap bitmap = retriever.getFrameAtTime(millis ,MediaMetadataRetriever.OPTION_CLOSEST);
//                rev.add(bitmap);
////                ImageView iv = (ImageView) findViewById(R.id.imageView);
////                iv.setImageBitmap(bitmap);
//                Thread.sleep(1);
//            }
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
        for(int i = 1000; i < millis-1000; i+=1000) { //int i = 0; i < millis; i+=1000
            //j++;
            ///Bitmap bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            //Bitmap bitmap = retriever.getFrameAtTime(millis, MediaMetadataRetriever.OPTION_CLOSEST);
            //bitmap = retriever.getFrameAtTime();
            //bitmap = retriever.getFrameAtTime(i*1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            //bitmap = retriever.getFrameAtTime(i*1000);
            //bitmap = retriever.getFrameAtTime(i*2000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); //funcionando precariamente
            bitmap = retriever.getFrameAtTime(i*1000, MediaMetadataRetriever.OPTION_CLOSEST);
            //bitmap = retriever.getScaledFrameAtTime(i*1000, 360, 480);

            //bitmap = retriever.getScaledFrameAtTime(i, 360, 480);
            //bitmap = retriever.getScaledFrameAtTime(i, FFmpegMediaMetadataRetriever.OPTION_CLOSEST, 360, 480);

            //bitmap = Highgui.CV_CAP_PROP_FRAME_HEIGHT
            //Highgui.CV_CAP_PROP_FRAME_HEIGHT


            rev.add(bitmap);
            Log.i("BITMAP", "BITMAP "+rev.size());

            //}
            try {
//                ImageView iv = (ImageView) findViewById(R.id.imageView);
//                iv.setImageBitmap(bitmap);
                saveFrames(rev);

            }catch (IOException e){
                e.printStackTrace();
            }
        }


//        for(int i = 0; i < millis; i+=1000){
//            Bitmap bitmap = retriever.getFrameAtTime(i * 1000 ,MediaMetadataRetriever.OPTION_CLOSEST);
//            ImageView iv = (ImageView) findViewById(R.id.imageView);
//            iv.setImageBitmap(bitmap);
//        }

//        Bitmap bitmap = retriever.getFrameAtTime(millis ,MediaMetadataRetriever.OPTION_CLOSEST);
//        ImageView iv = (ImageView) findViewById(R.id.imageView);
//        iv.setImageBitmap(bitmap);
    }

    public void saveFrames(ArrayList<Bitmap> saveBitmapList) throws IOException{

        diretorioApp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + nomeDiretorio +"/";
        diretorio = new File(diretorioApp);

        if(!diretorio.exists()){
            diretorio.mkdirs();
        }

        Log.i("BITMAP-SAVE", "BITMAP-SAVE "+saveBitmapList.size());
        int i=1;
        for(Bitmap b : saveBitmapList){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            File f = new File(diretorio, ("frame"+i+".jpg"));
            f.createNewFile();
//            ImageView iv = (ImageView) findViewById(R.id.imageView);
//            iv.setImageBitmap(b);
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            fo.flush();
            fo.close();

            i++;
        }
        //saveBitmapList.clear();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void carregaValoresKeT(){
        PreferenciasKeT preferenciasKeT = new PreferenciasKeT(VideoActivity.this);
        float kk = preferenciasKeT.get_k();
        k=kk;
        float tt = preferenciasKeT.get_t();
        t=tt;
        NumberFormat formato = new DecimalFormat("0.##");

        textViewK.setText(formato.format(kk)+"");
        textViewT.setText(formato.format(tt)+"");
    }

    private void carregaValoresEeD(){
        PreferenciasEeD preferenciasEeD = new PreferenciasEeD(VideoActivity.this);
        int erosaoP = preferenciasEeD.get_E();
        erocao=erosaoP;
        int dilatacaoD = preferenciasEeD.get_D();
        dilatacao=dilatacaoD;
        //NumberFormat formato = new DecimalFormat("0.##");

        textViewE.setText(erosaoP+"");
        textViewD.setText(dilatacaoD+"");
    }

    private void carregaValorMinCruz(){
        PreferenciasMinCruz preferenciasMinCruz = new PreferenciasMinCruz(VideoActivity.this);
        int  minimoCruzamento = preferenciasMinCruz.get_C();
        textViewC.setText(minimoCruzamento+"");
    }

}
