package com.example.felipe.appgreen.Tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by felipe on 10/11/16.
 */

public class PreferenciasKeT {
    private Context contexto;
    private SharedPreferences preferencesKeT;
    private final String NOME_ARQUIVO_RGB = "PreferenciasRGB";
    private final float k_Preferences = 0.68f;
    private final float t_Preferences = 20.0f;
    private final int MODE = 0;
    private final SharedPreferences.Editor editor;

    public PreferenciasKeT(Context contexto) {
        this.contexto = contexto;
        this.preferencesKeT = contexto.getSharedPreferences(NOME_ARQUIVO_RGB, MODE);
        this.editor = preferencesKeT.edit();
    }

    public void salvarDados_k(float k){
        //editor.putFloat(String.valueOf(k_Preferences), k);
        editor.putFloat("k_Preferences", k);
        editor.commit();
    }

    public float get_k(){
        return preferencesKeT.getFloat("k_Preferences", k_Preferences);
    }

    public void salvarDados_t(float t){
        editor.putFloat("t_Preferences", t);
        editor.commit();
    }

    public float get_t(){
        return preferencesKeT.getFloat("t_Preferences", t_Preferences);
    }
}
