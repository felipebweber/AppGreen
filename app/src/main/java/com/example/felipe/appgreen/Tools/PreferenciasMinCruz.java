package com.example.felipe.appgreen.Tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by felipe on 10/11/16.
 */

public class PreferenciasMinCruz {
    private Context contexto;
    private SharedPreferences preferencesC;
    private final String NOME_ARQUIVO_MINIMO = "Preferencias";
    private final int C_Preferences = 25;
    private final int MODE = 0;
    private final SharedPreferences.Editor editor;

    public PreferenciasMinCruz(Context contexto) {
        this.contexto = contexto;
        this.preferencesC = contexto.getSharedPreferences(NOME_ARQUIVO_MINIMO, MODE);
        this.editor = preferencesC.edit();
    }

    public void salvarDados_C(int c){
        editor.putInt("C_Preferences", c);
        editor.commit();
    }

    public int get_C(){
        return preferencesC.getInt("C_Preferences", C_Preferences);
    }
}
