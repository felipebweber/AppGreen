package com.example.felipe.appgreen.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import wseemann.media.FFmpegMediaMetadataRetriever;

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

    private TextView bicoUm;
    private TextView bicoDois;
    private TextView bicoTres;
    private TextView bicoQuatro;
    private TextView bicoCinco;

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

    int cont1 = 0;
    int cont2 = 0;
    int cont3 = 0;
    int cont4 = 0;
    int cont5 = 0;

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

        bicoUm = (TextView) findViewById(R.id.textBicoUm);
        bicoDois = (TextView) findViewById(R.id.textBicoDois);
        bicoTres = (TextView) findViewById(R.id.textBicoTres);
        bicoQuatro = (TextView) findViewById(R.id.textBicoQuatro);
        bicoCinco = (TextView) findViewById(R.id.textBicoCinco);

        bicoUm.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
        bicoUm.setText("▇▇");
        bicoDois.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
        bicoDois.setText("▇▇");
        bicoTres.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
        bicoTres.setText("▇▇");
        bicoQuatro.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
        bicoQuatro.setText("▇▇");
        bicoCinco.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
        bicoCinco.setText("▇▇");

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

        if(id == R.id.action_reset_imagem){
            if(sampledImage==null){
                Context context = getApplicationContext();
                CharSequence text = "Você precisa carregar uma imagem primeiro";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            }

            bicoUm.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoUm.setText("▇▇");
            bicoDois.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoDois.setText("▇▇");
            bicoTres.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoTres.setText("▇▇");
            bicoQuatro.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoQuatro.setText("▇▇");
            bicoCinco.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoCinco.setText("▇▇");

            displayImage(sampledImage);
        }

        if (id == R.id.action_processar){

            cont1 = 1;
            cont2 = 1;
            cont3 = 1;
            cont4 = 1;
            cont5 = 1;

            bicoUm.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoUm.setText("▇▇");
            bicoDois.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoDois.setText("▇▇");
            bicoTres.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoTres.setText("▇▇");
            bicoQuatro.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoQuatro.setText("▇▇");
            bicoCinco.setTextColor(getResources().getColor(R.color.colorBicoDesligado));
            bicoCinco.setText("▇▇");

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
                // For para percorrer linha x coluna
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
                List<Point> points = new ArrayList<>(); // Mudanca para usar ransac


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

                    points.add(new Point(centroid.x,centroid.y));
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
                Mat grayLine = new Mat();
                Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY);
                Imgproc.blur(outDilateGray, grayLine, new Size(3,3));

                // detect the edges
                Mat edges = new Mat();
                int lowThreshold = 25;
                int ratio = 3;


                Imgproc.Canny(grayLine, edges, lowThreshold, lowThreshold * ratio);

                Mat lines = new Mat(); // matriz auxiliar

                Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, minimoCruzamento, 350, 580);

            /*
            - Saida do detector de bordas (edges)
            - Vetor que armazena os parametros de linhas detectadas (lines)
            - Resolucao de parametro em pixels, 1 pixel (1)
            - A resolucao do parametro em radianos, 1 grau (Math.PI/180)
            - Numero minimo de cruzamentos para detectar uma linha (55)
            - Numero minimo de pontos que podem formar uma linha (70)
            - Diferenca maxima entre dois pontos a serem considerados na mesma linha (200)
             */

                // For detecta as linhas
                for(int i = 0; i < lines.cols(); i++) {
                    double[] val = lines.get(0, i);

                    Log.i("TAM", "TAM: "+i);
                    Log.i("PONTOS", "PONTO1 " +val[0]+" "+val[1]);
                    Log.i("PONTOS", "PONTO2 " +val[2]+" "+val[3]);
                    if(((val[0]> 0) && (val[0] < 90)) && (cont1==1)){
                        cont1 = 2;
                        bicoUm.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                        bicoUm.setText("▇▇");
                    }
                    if(((val[0]>= 90) && (val[0] < 150)) && (cont2 == 1)){
                        cont2 = 2;
                        bicoDois.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                        bicoDois.setText("▇▇");
                    }
                    if(((val[0]>= 150) && (val[0] < 180)) && (cont3 == 1)){
                        cont3=2;
                        bicoTres.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                        bicoTres.setText("▇▇");
                    }
                    if(((val[0]>= 180) && (val[0] < 270)) && (cont4 == 1)){
                        cont4 = 2;
                        bicoQuatro.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                        bicoQuatro.setText("▇▇");
                    }
                    if(((val[0]>= 270) && (val[0] < 360)) && (cont5==1)){
                        cont5=2;
                        bicoCinco.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                        bicoCinco.setText("▇▇");
                    }

                    Core.line(outDilate, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
                }
                displayImage(outDilate);
                break;
            }

        }

//        if(id == R.id.action_linha){
//            if(sampledImage==null){
//                Context context = getApplicationContext();
//                CharSequence text = "Você precisa carregar uma imagem primeiro";
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
//                return true;
//            }
//
//
//            rgbGreen = new Mat();
//
//            sampledImage.copyTo(rgbGreen);
//            Size size = rgbGreen.size();
//            for(int i=0; i<sampledImage.rows(); i++){
//                for(int j=0; j<sampledImage.cols(); j++){
//                    double[] data = sampledImage.get(i,j);
//                    if((data[1] > k * (data[0]+data[2])) & (data[0]+data[2] > t)){
//                        //if(data[1] > k * (data[0]+data[2])){
//                        data[0]=255;
//                        data[1]=255;
//                        data[2]=255;
//                        //rgbGreen.put(i,j,255);
//                        rgbGreen.put(i,j,data);
//
//                        //Log.i("DATA","NO PRIMEIRO IF ");
//                    }else{
//                        data[0]=0;
//                        data[1]=0;
//                        data[2]=0;
//                        rgbGreen.put(i, j, data);
//                    }
//                }
//            }
//
//            outErode = new Mat();
//            outDilate = new Mat();
//
//            // Operações morfologicas erosão e dilatação
//            Imgproc.erode(rgbGreen, outErode, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erocao, erocao)));
//            Imgproc.dilate(outErode, outDilate, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilatacao, dilatacao)));
//
//
//            Mat outDilateGray = new Mat();
//            // Converte o resultado da dilatação para gray
//            Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY);
//
//            List<MatOfPoint> contours = new ArrayList<>();
//            contours.clear();
//            Mat hierarchy = new Mat();
//            // Função retorna os contornos
//            Imgproc.findContours(outDilateGray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
////            Imgproc.drawContours(outDilate, contours, -1, new Scalar(0, 255, 0), 3);
//            Log.i("CONTORNO", "CONTORNO: " + contours.size());
//
//
//            // Momentos são utilizados para encontrar a soma dos pixels que compõem o contorno
//            List<Moments> mu = new ArrayList<>(contours.size());
//
//            // Variavel auxilar para encontrar o centroide do objeto.
//            List<Point> points = new ArrayList<>();
//
//            // For responsavel por encontrar todos os centroides
//            for (int i = 0; i < contours.size(); i++) {
//
//                mu.add(i, Imgproc.moments(contours.get(i), true));
//                Log.i("CONTOURS", "CONTOURS: " + contours.get(i).toList());
//                Log.i("CONTOURS", "CONTOURSMOMENTS: " + mu.size());
//                Log.i("CONTOURS", "CONTOURSMOMENTS: " + contours.size());
//
//                Moments p = mu.get(i);
//
//                Point centroid = new Point();
//                // Obtem os centroides de x e y
//                centroid.x = p.get_m10() / p.get_m00();
//                centroid.y = p.get_m01() / p.get_m00();
//
//                points.add(new Point(centroid.x,centroid.y));
//                Log.i("PONTO", "X-Y: " + centroid);
//                Log.i("POINTS", "X-Y: " + points.get(i).x);
//                // Adiciona o centroide no objeto
//                Core.circle(outDilate, centroid, 5, new Scalar(255, 0, 0), -1);
//            }
//
//
//            for(int i=0; i<outDilate.rows(); i++){
//                for(int j=0; j<outDilate.cols(); j++){
//                    double[] data = outDilate.get(i,j);
//                    if(data[0] == 255 & data[1] == 255 & data[2] == 255){
//                        data[0]=0;
//                        data[1]=0;
//                        data[2]=0;
//                        outDilate.put(i,j,data);
//
//
//                    }else{
//
//                    }
//                }
//            }
//
//
//            // generate gray scale and blur
//            Mat grayLine = new Mat(); // faz parte do antigo
//            Imgproc.cvtColor(outDilate, outDilateGray, Imgproc.COLOR_BGRA2GRAY);
//            Imgproc.blur(outDilateGray, grayLine, new Size(3,3));
//
//            // detect the edges
//            Mat edges = new Mat(); // matriz auxiliar
//            int lowThreshold = 25;
//            int ratio = 3;
//
//
//            Imgproc.Canny(grayLine, edges, lowThreshold, lowThreshold * ratio);
//
//            Mat lines = new Mat(); // matriz auxiliar
//
//            Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, 25, 300, 200);
//
//            /*
//            - Saida do detector de bordas (edges)
//            - Vetor que armazena os parametros de linhas detectadas (lines)
//            - Resolucao de parametro em pixels, 1 pixel (1)
//            - A resolucao do parametro em radianos, 1 grau (Math.PI/180)
//            - Numero minimo de cruzamentos para detectar uma linha (55)
//            - Numero minimo de pontos que podem formar uma linha (70)
//            - Diferenca maxima entre dois pontos a serem considerados na mesma linha (200)
//             */
//
//
//            for(int i = 0; i < lines.cols(); i++) {
//                double[] val = lines.get(0, i);
//
//                Core.line(outDilate, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
//
//            }
//
//            displayImage(outDilate);
//        }

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


    /*
    Função loadVideo carrega video da bibioteca do dispositivo e divide em frames em um array list de bitmap
     */
    private void loadVideo(String path){



        Uri videoFileUri = Uri.parse(path);

        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        retriever.setDataSource(path);




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

            //bitmap = retriever.getFrameAtTime(i*1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            bitmap = retriever.getScaledFrameAtTime(i*1000, 360, 480);

            rev.add(bitmap);
            Log.i("BITMAP", "BITMAP "+rev.size());


            try {
                saveFrames(rev);

            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    /*
    Função saveFrames salva os frames no dispositivo
     */
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


    /*
    As funções carregaValores são utilizadas para carregar os valores de K, T, E, D e minimo cruzamento para serem
    utilizadas no programa
     */
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
