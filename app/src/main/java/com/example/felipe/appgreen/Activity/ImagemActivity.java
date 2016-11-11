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


    // Esse era para o morango
//    private final double k = 0.65;
//    private final double t = 20;
//
//    private final int erocao = 4;
//    private final int dilatacao = 25;
    // ate aqui do morango

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

    private String [] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem);

//        toolbar = (Toolbar) findViewById(R.id.to;
//        toolbar.setTitle("Imagem");
//        setSupportActionBar(toolbar);

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

        //Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //startActivityForResult(i, RESULT_LOAD_IMAGE);

        if(id == R.id.action_reset_imagem){
            if(sampledImage==null){
                Context context = getApplicationContext();
                CharSequence text = "Você precisa carregar uma imagem primeiro";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            }

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

            Point pt1 = new Point();
            Point pt2 = new Point();
            double a, b;
            double x0, y0;
            double rho, theta;

            Imgproc.Canny(grayLine, edges, lowThreshold, lowThreshold * ratio); // faz parte do antigo

            Mat lines = new Mat(); // faz parte do antigo
            //Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 55, 70, 200);

            //Imgproc.HoughLinesP(edges, lines, 5, Math.PI/180, 50, 420, 400); // faz parte do antigo, morango
            //Imgproc.HoughLinesP(edges, lines, 5, Math.PI/180, 50, 300, 200); // faz parte do antigo, cebola
            Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, minimoCruzamento, 300, 200); // faz parte do antigo, cebola novo (13-10-16)
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
//                rho = val[0];
//                theta = val[1];
//                a = Math.cos(theta);
//                b = Math.sin(rho);
//                x0 = a * rho;
//                y0 = b * rho;
//                pt1.x = Math.round(x0 + 1000*(-b));
//                pt1.y = Math.round(y0 + 1000*a);
//                pt2.x = Math.round(x0 + 1000*(-b));
//                pt2.y = Math.round(y0 + 1000*a);
                Core.line(outDilate, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2); // faz parte do antigo
                //Core.line(outDilate, pt1, pt2, new Scalar(0, 0, 255), 3);
            } // faz parte do antigo

            // ############### e termina aqui ##########################
//            Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 50, 50, 50);
//            for(int i = 0; i < lines.cols(); i++) {
//                double[] val = lines.get(0, i);
//                Core.line(outDilate, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
//            }


            //Core.line(outDilate, melhorPonto[0], melhorPonto[1], new Scalar(0,255,0), 2);
            displayImage(outDilate);
        }

        return super.onOptionsItemSelected(item);
    }

    static Mat ImageCanny(Mat imageGray) {
        Mat imgCanny = new Mat();
        Imgproc.Canny(imageGray, imgCanny, 10, 200);
        return imgCanny;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();

                selectedImagePath = getPath(selectedImageUri); // getPath funcao
//                Log.i(TAG, "selectedImagePath: " + selectedImagePath);
//                Log.i(TAG, "ANTES DISPLAY ");

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
        //originalImage = Imgcodecs.imread(path);
        //originalImage = Imgcodecs.imread(path); //aqui mudou
        originalImage = Highgui.imread(path);
        Log.i("FLAG", "Depois do original image "+originalImage);
        rgbImage = new Mat();
        sampledImage = new Mat();
        Log.i("FLAG","LOAD IMAGE MAT");

        //Imgproc.cvtColor(originalImage, rgbImage, Imgproc.COLOR_BGR2RGB);
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

        //Imgproc.resize(rgbImage,sampledImage,new Size(),width, height, Imgproc.INTER_AREA);

        Imgproc.resize(rgbImage, sampledImage, new Size(), downSampleRatio, downSampleRatio, Imgproc.INTER_AREA);
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

    private void displayImage(Mat image){
        Bitmap bitmap = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(image, bitmap);

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bitmap);
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
//    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

//    public Mat erode(Mat input, int elementSize, int elementShape){
//        Mat outputImage = new Mat();
//        Mat element = getKernelFromShape(elementSize, elementShape);
//        Imgproc.erode(input,outputImage, element);
//        return outputImage;
//    }

//    private Mat getKernelFromShape(int elementSize, int elementShape) {
//        return Imgproc.getStructuringElement(elementShape, new Size(elementSize*2+1, elementSize*2+1), new Point(elementSize, elementSize) );
//    }

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

    private void carregaValorMinCruz(){
        PreferenciasMinCruz preferenciasMinCruz = new PreferenciasMinCruz(ImagemActivity.this);
        int  minimoCruzamento = preferenciasMinCruz.get_C();
        textViewC.setText(minimoCruzamento+"");
    }
}
