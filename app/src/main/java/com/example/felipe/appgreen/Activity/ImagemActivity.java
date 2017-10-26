package com.example.felipe.appgreen.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.felipe.appgreen.R;
import com.example.felipe.appgreen.Tools.Permissao;
import com.example.felipe.appgreen.Tools.PreferenciasEeD;
import com.example.felipe.appgreen.Tools.PreferenciasKeT;
import com.example.felipe.appgreen.Tools.PreferenciasMinCruz;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ImagemActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static final String  TAG = "OPENCV";
    private String selectedImagePath;




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
    private Toolbar toolbar;

    private TextView bicoUm;
    private TextView bicoDois;
    private TextView bicoTres;
    private TextView bicoQuatro;
    private TextView bicoCinco;

    int cont1 = 0;
    int cont2 = 0;
    int cont3 = 0;
    int cont4 = 0;
    int cont5 = 0;


    private float k = 0.68f;
    private final float kRST = 0.68f;
    private float t = 20.0f;
    private final float tRST = 20.0f;

    private int erocao = 3;
    private final int erocaoRST = 3;
    private int dilatacao = 7;
    private final int dilatacaoRST = 7;

    private int minimoCruzamento = 25;
    private final int minimoCruzamentoRST = 25;

    // Matrizes auxiliares
    Mat sampledImage = null;
    Mat originalImage = null;
    Mat outErode = null;
    Mat outDilate = null;
    Mat rgbImage = null;
    Mat rgbGreen = null;

    private String [] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem);


        btMaisK = (Button) findViewById(R.id.btMaisKi);
        btMaisT = (Button) findViewById(R.id.btMaisTi);
        btResetKeT = (Button) findViewById(R.id.btResetKeTi);

        btMenosK = (Button) findViewById(R.id.btMenosKi);
        btMenosT = (Button) findViewById(R.id.btMenosTi);

        textViewK = (TextView) findViewById(R.id.textViewKi);
        textViewT = (TextView) findViewById(R.id.textViewTi);

        btMaisE = (Button) findViewById(R.id.btMaisEi);
        btMaisD = (Button) findViewById(R.id.btMaisDi);
        btResetEeD = (Button) findViewById(R.id.btResetEeDi);

        btMenosE = (Button) findViewById(R.id.btMenosEi);
        btMenosD = (Button) findViewById(R.id.btMenosDi);

        textViewE = (TextView) findViewById(R.id.textViewEi);
        textViewD = (TextView) findViewById(R.id.textViewDi);

        btMaisC = (Button) findViewById(R.id.btMaisCi);
        btMenosC = (Button) findViewById(R.id.btMenosCi);

        textViewC = (TextView) findViewById(R.id.textViewCi);

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

        Permissao.validaPermissoes(1, this, permissoesNecessarias );

        carregaValoresKeT();
        carregaValoresEeD();
        carregaValorMinCruz();

        btMenosK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(ImagemActivity.this);
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
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(ImagemActivity.this);
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
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(ImagemActivity.this);

                preferenciasKeT.salvarDados_k(kRST);
                preferenciasKeT.salvarDados_t(tRST);

                carregaValoresKeT();
            }
        });

        btMenosT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(ImagemActivity.this);
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
                PreferenciasKeT preferenciasKeT = new PreferenciasKeT(ImagemActivity.this);
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
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(ImagemActivity.this);
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
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(ImagemActivity.this);
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
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(ImagemActivity.this);
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
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(ImagemActivity.this);
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
                PreferenciasEeD preferenciasEeD = new PreferenciasEeD(ImagemActivity.this);

                preferenciasEeD.salvarDados_E(erocaoRST);
                preferenciasEeD.salvarDados_D(dilatacaoRST);

                carregaValoresEeD();
            }
        });

        btMaisC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenciasMinCruz preferenciasMinCruz = new PreferenciasMinCruz(ImagemActivity.this);
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
                PreferenciasMinCruz preferenciasMinCruz = new PreferenciasMinCruz(ImagemActivity.this);
                int minimoCruzamentoGet = preferenciasMinCruz.get_C();

                minimoCruzamentoGet -= 1;
                preferenciasMinCruz.salvarDados_C(minimoCruzamentoGet);

                minimoCruzamentoGet = preferenciasMinCruz.get_C();
                minimoCruzamento = minimoCruzamentoGet;

                textViewC.setText(minimoCruzamentoGet+"");
            }
        });
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater(); // exibir menu na tela
        inflater.inflate(R.menu.menu_imagem, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_openGallary) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
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



        if(id == R.id.action_linha){

            if(sampledImage==null){
                Context context = getApplicationContext();
                CharSequence text = "Você precisa carregar uma imagem primeiro";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            }

            cont1 = 1;
            cont2 = 1;
            cont3 = 1;
            cont4 = 1;
            cont5 = 1;

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

            Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, minimoCruzamento, 300, 200);

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
                Log.i("PONTOS", "PONTO1 " +val[0]+" "+val[1]);
                Log.i("PONTOS", "PONTO2 " +val[2]+" "+val[3]);



                if((val[0]> 30) && (val[0] < 90)){
                    bicoUm.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                    bicoUm.setText("▇▇");
                }
                else if((val[0]>= 90) && (val[0] < 150)){
                    bicoDois.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                    bicoDois.setText("▇▇");
                }
                else if((val[0]>= 150) && (val[0] < 180)){
                    bicoTres.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                    bicoTres.setText("▇▇");
                }
                else if((val[0]> 180) && (val[0] < 270)){
                    bicoQuatro.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                    bicoQuatro.setText("▇▇");
                }
                else if((val[0]>= 270) && (val[0] < 330)){
                    bicoCinco.setTextColor(getResources().getColor(R.color.colorBicoAcionado));
                    bicoCinco.setText("▇▇");
                }

                Core.line(outDilate, new Point(val[2], val[3]), new Point(val[0], val[1]), new Scalar(0, 255, 0), 255); // faz parte do antigo
                val[0] = -1;
            }

            displayImage(outDilate);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();

                selectedImagePath = getPath(selectedImageUri); // getPath funcao

                loadImage(selectedImagePath); // loadImage funcao

                displayImage(sampledImage);
            }
        }
    }

    private String getPath(Uri uri){
        if(uri == null){
            return null;
        }
        //String[] projection = {MediaStore.Images.Media.DATA};
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private void loadImage(String path){
        Log.i("FLAG","LOAD IMAGE " + path);

        originalImage = Highgui.imread(path);
        Log.i("FLAG", "Depois do original image "+originalImage);
        rgbImage = new Mat();
        sampledImage = new Mat();
        Log.i("FLAG","LOAD IMAGE MAT");

        Imgproc.cvtColor(originalImage, rgbImage, Imgproc.COLOR_BGR2RGB);

        Log.i("FLAG","LOAD IMAGE DEPOIS IMGPROC");
        Display display = getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        Log.i("FLAG","LOAD IMAGE DISPLAY");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Log.i("FLAG","LOAD IMAGE GETWINDOW");
        int width = displayMetrics.widthPixels; // largura
        int heigth = displayMetrics.heightPixels; // altura
        Log.i("FLAG","LOAD IMAGE ANTES DOWNSAMPLERATIO");
        double downSampleRatio = calculateSubSampleSize(rgbImage,width,heigth);

        Imgproc.resize(rgbImage, sampledImage, new Size(), downSampleRatio, downSampleRatio, Imgproc.INTER_AREA);
        //Imgproc.resize(rgbImage, sampledImage, new Size(), 360, 480, Imgproc.INTER_AREA);
        Log.i("FLAG","LOAD IMAGE ANTES DO TRY");
        try {
            ExifInterface exif = new ExifInterface(selectedImagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.i("FLAG","LOAD IMAGE DENTRO DO TRY");

            switch (orientation )
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    Log.i("FLAG","LOAD IMAGE CASE 1");
                    //get the mirrored image
                    sampledImage=sampledImage.t();
                    //flip on the y-axis
                    Core.flip(sampledImage, sampledImage, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    Log.i("FLAG","LOAD IMAGE CASE 2");
                    //get up side down image
                    sampledImage=sampledImage.t();
                    //Flip on the x-axis
                    Core.flip(sampledImage, sampledImage, 0);
                    break;
            }
        } catch (IOException e) {
            Log.i("FLAG","LOAD IMAGE EXCEPTION");
            e.printStackTrace();
        }
    }

    // Função calcula novo tamanho da imagem
    private static double calculateSubSampleSize(Mat srcImage, double reqWidth, double reqHeight){
        final int heigth = srcImage.height();
        final int width = srcImage.width();
        double inSampleSize = 1;

        if(heigth > reqHeight || width > reqWidth){
            final double heightRatio = (double) reqHeight / (double)heigth;
            final double widthRatio = (double) reqWidth / (double)width;

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // Função para apresentar imagem na tela
    private void displayImage(Mat image){
        Bitmap bitmap = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(image, bitmap);

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bitmap);
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


    /*
    As funções carregaValores são utilizadas para carregar os valores de K, T, E, D e minimo cruzamento para serem
    utilizadas no programa
     */

    // Carrega os valores K e T
    private void carregaValoresKeT(){
        PreferenciasKeT preferenciasKeT = new PreferenciasKeT(ImagemActivity.this);
        float kk = preferenciasKeT.get_k();
        k=kk;
        float tt = preferenciasKeT.get_t();
        t=tt;
        NumberFormat formato = new DecimalFormat("0.##");

        textViewK.setText(formato.format(kk)+"");
        textViewT.setText(formato.format(tt)+"");
    }

    // Carrega os valores E e D
    private void carregaValoresEeD(){
        PreferenciasEeD preferenciasEeD = new PreferenciasEeD(ImagemActivity.this);
        int erosaoP = preferenciasEeD.get_E();
        erocao=erosaoP;
        int dilatacaoD = preferenciasEeD.get_D();
        dilatacao=dilatacaoD;
        //NumberFormat formato = new DecimalFormat("0.##");

        textViewE.setText(erosaoP+"");
        textViewD.setText(dilatacaoD+"");
    }

    // Carrega o valor do minimo cruzamento
    private void carregaValorMinCruz(){
        PreferenciasMinCruz preferenciasMinCruz = new PreferenciasMinCruz(ImagemActivity.this);
        int  minimoCruzamento = preferenciasMinCruz.get_C();
        textViewC.setText(minimoCruzamento+"");
    }
}
