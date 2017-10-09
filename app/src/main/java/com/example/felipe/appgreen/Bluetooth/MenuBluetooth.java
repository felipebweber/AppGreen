package com.example.felipe.appgreen.Bluetooth;

/**
 * Created by weber on 08/10/2017.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.felipe.appgreen.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MenuBluetooth extends AppCompatActivity{
    /*Declara os itens da tela*/
    Button btnConectar, btnEnviar;
    EditText edtDado;
    TextView tvDadosRecebidosBT;

    /*Bluetooth*/
    BluetoothAdapter meuBluetoothAdapter = null;
    private static String MAC = null;
    BluetoothDevice meuDevice = null;
    BluetoothSocket meuSocket = null;
    UUID UUID_SERIAL_PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ConnectedThread connectedThread;

    /*Variaveis de controle*/
    private static final int SOLICITA_ATIVACAO_BT = 1;
    private static final int SOLICITA_CONEXAO_BT = 2;
    private static final int MENSAGEM_RECEBIDA_BT = 3;
    boolean conectado = false;
    android.os.Handler meuHandler;
    StringBuilder dadosBluetooth = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_bluetooth);

        /*Define os itens na tela*/
        btnConectar = (Button) findViewById(R.id.btnConectar);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        edtDado = (EditText) findViewById(R.id.editText);
        tvDadosRecebidosBT = (TextView) findViewById(R.id.tvDadosRecebidosBT);

        /*Pega o adaptador bluetooth do aparelho*/
        meuBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Verifica se o aparelho realmente tem bluetooth ou nao*/
        if (meuBluetoothAdapter == null){
            /*Aparelho nao possui bluetooth*/
            Toast.makeText(getApplicationContext(), "O aparelho não possui Bluetooth, o app será encerrado!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            /*Foi encontrado um adaptador bluetooth no aparelho
              Sera verificado se o mesmo esta ligado ou nao */
            if(!meuBluetoothAdapter.isEnabled()) {
                /* Caso nao esteja ligado sera solicitado ao usuario para que ative ele */
                Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(ativaBluetooth, SOLICITA_ATIVACAO_BT);
            }

        }


        /*Funcao chamada ao apertar o botao conectar */
        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Caso esteja conectado */
                if (conectado) {
                    /*Tenta desconectar o dispositivo */
                    try {
                        meuSocket.close();
                        conectado = false;
                        btnConectar.setText("CONECTAR");
                        Toast.makeText(getApplicationContext(), "Bluetooth DESCONECTADO", Toast.LENGTH_LONG).show();
                    } catch (IOException erro) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro ao desconectar!"+"\n"+"ERRO :"+erro, Toast.LENGTH_LONG).show();

                    }

                } /*Caso nao esteja conectado ainda */
                else {
                    /* Abre uma janela com os dispositivos disponiveis */
                    Intent abreListaDispositivos = new Intent(MenuBluetooth.this, ListaDispositivosBluetooth.class);
                    startActivityForResult(abreListaDispositivos, SOLICITA_CONEXAO_BT);
                }


            }
        });

        /* Funcao chamada ao apertar o botao enviar */
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Verifica se esta conectado */
                if (conectado){
                    /* Salva o texto digitado em uma string e passa para a funcao de envio */
                    String paraEnviar = edtDado.getText().toString();
                    //Toast.makeText(MenuBluetooth.this, paraEnviar, Toast.LENGTH_SHORT).show();
                    connectedThread.enviar(paraEnviar);

                } else {
                    Toast.makeText(getApplicationContext(),"Bluetooth não está conectado!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        meuHandler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {

                /* Verifica se a mensagem recebida veio do BT */
                if(msg.what == MENSAGEM_RECEBIDA_BT){

                    String recebidos = (String) msg.obj;

                    /*Adiciona o caracter recebido a mensagem */
                    dadosBluetooth.append(recebidos);

                    /* Verifica se o caracter de fim de mensagem foi recebido */
                    int fimMsg = dadosBluetooth.indexOf("}");
                    if (fimMsg > 0){

                        /* Pega tudo que foi recebido ate o caracter de fim de mensagem */
                        String msgCompleta = dadosBluetooth.substring(0, fimMsg);

                        int tamanhoMsg = msgCompleta.length();

                        /* Verifica se o primeiro caracter recebido foi de inicio de mensagem */
                        if(dadosBluetooth.charAt(0) == '{') {
                            String msgFinal = dadosBluetooth.substring(1,tamanhoMsg);

                            /*Escreve a string recebida na tela */
                            tvDadosRecebidosBT.setText(msgFinal);
                            Toast.makeText(getApplicationContext(), msgFinal,Toast.LENGTH_SHORT).show();

                        }

                        dadosBluetooth.delete(0, dadosBluetooth.length());

                    }


                }
            }
        };


    }


    /* Funcao do android que e chamada quando uma nova tela/janela e chamada e a mesma retorna um inteiro (startActivityForResult) */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*Verifica qual requisicao foi feita e o valor retornado */
        switch (requestCode) {

            case SOLICITA_ATIVACAO_BT:
                if (resultCode == Activity.RESULT_OK) {
                    /*Se o usuario permitiu a ativacao do Bluetooth */
                    Toast.makeText(getApplicationContext(), "Bluetooth ATIVO!", Toast.LENGTH_SHORT).show();
                } else {
                    /*Se o usuario nao permitiu ativar o bluetooth */
                    Toast.makeText(getApplicationContext(), "Bluetooth DESATIVADO! O app será encerrado!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case SOLICITA_CONEXAO_BT:
                if (resultCode == Activity.RESULT_OK) {
                    /* Caso tenha recebido o endereco MAC para realizar a conexao */
                    /* Salva o MAC retornado */
                    MAC = data.getExtras().getString(ListaDispositivosBluetooth.ENDERECO_MAC);
                    /* Cria um dispositivo com o endereco retornado */
                    meuDevice = meuBluetoothAdapter.getRemoteDevice(MAC);

                    /* Tenta criar um socket para comunicacao */
                    try{
                        /* Cria um socket utilizando o dispositivo externo passando o UUID desejado (SERIAL) */
                        meuSocket = meuDevice.createRfcommSocketToServiceRecord(UUID_SERIAL_PORT);

                        /* Tenta estabilizar a conexao */
                        meuSocket.connect();
                        conectado = true;
                        btnConectar.setText("DESCONECTAR");

                        /*Cria uma thread */
                        connectedThread = new ConnectedThread(meuSocket);
                        connectedThread.start();

                        Toast.makeText(getApplicationContext(), "Conectado !", Toast.LENGTH_SHORT).show();

                    } catch (IOException erro) {
                        /* Caso ocorra algum problema durante a conexao */
                        conectado = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro ao tentar conectar"+"\n"+"ERRO :"+erro,Toast.LENGTH_LONG).show();
                    }

                } else {
                    /* Caso nenhum endereco MAC tenha sido retornado */
                    Toast.makeText(getApplicationContext(), "Falha ao retornar o endereço MAC do dispositivo selecionado.", Toast.LENGTH_SHORT).show();
                }


        }

    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            meuSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String recebidos = new String(buffer, 0, bytes);

                    // Send the obtained bytes to the UI activity
                    meuHandler.obtainMessage(MENSAGEM_RECEBIDA_BT, bytes, -1, recebidos).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void enviar (String dado) {
            byte[] msgBuffer = dado.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

    }
}
